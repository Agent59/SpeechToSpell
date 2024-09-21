package net.agent59.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Based on the DispatchedMapCodec from the DataFixerUpper library.
 * <p>If an error is encountered when <u>parsing</u> an entry, the error is logged as a warning and the entry is skipped.
 * <p>Will still throw errors when <u>encoding</u> faulty data.
 *
 * @see <a href="https://github.com/Mojang/DataFixerUpper/blob/master/src/main/java/com/mojang/serialization/codecs/DispatchedMapCodec.java">Mojang DataFixerUpper DispatchedMapCodec</a>
 * @param keyCodec The codec of what is used for the key.
 * @param valueCodecFunction The function that returns a codec given the key.
 * @param <K> The type of the key, contains at most times at some point a wildcard.
 * @param <V> The type of the value, contains at most times at some point a wildcard.
 */
public record ErrorSkippingDispatchedMapCodec<K, V>(
        Codec<K> keyCodec,
        Function<K, Codec<? extends V>> valueCodecFunction
) implements Codec<Map<K, V>> {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public <T> DataResult<T> encode(final Map<K, V> input, final DynamicOps<T> ops, final T prefix) {
        final RecordBuilder<T> mapBuilder = ops.mapBuilder();
        for (final Map.Entry<K, V> entry : input.entrySet()) {
            mapBuilder.add(keyCodec.encodeStart(ops, entry.getKey()), encodeValue(valueCodecFunction.apply(entry.getKey()), entry.getValue(), ops));
        }
        return mapBuilder.build(prefix);
    }

    @SuppressWarnings("unchecked")
    private <T, V2 extends V> DataResult<T> encodeValue(final Codec<V2> codec, final V input, final DynamicOps<T> ops) {
        return codec.encodeStart(ops, (V2) input);
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(final DynamicOps<T> ops, final T input) {
        return ops.getMap(input).map(map -> {
            Map<K, V> entries = new Object2ObjectArrayMap<>();
            map.entries().forEach((entry -> parseEntry(ops, entry, entries)));
            return Pair.of(entries, input);
        });
    }

    private <T> void parseEntry(final DynamicOps<T> ops, final Pair<T, T> input, final Map<K, V> entries) {
        Optional<K> optionalKey = keyCodec.parse(ops, input.getFirst())
                .resultOrPartial(errMsg -> LOGGER.warn("""
                                Could not parse the key codec {} for the entry {}, due to the following error:
                                {}
                                Skipping this entry.
                                """, input.getFirst(), input, errMsg)
                );
        if (optionalKey.isEmpty()) return;
        K key = optionalKey.get();

        if (entries.containsKey(key)) {
            LOGGER.warn("Duplicate entry for key {}, using the value {} which was read first.",
                    key, entries.get(key));
            return;
        }
        valueCodecFunction.apply(key).parse(ops, input.getSecond())
                .resultOrPartial(errMsg -> LOGGER.warn("""
                        Could not parse the value codec {} for the entry {}, due to the following error:
                        {}
                        Skipping this entry.
                        """, input.getSecond(), input, errMsg)
                ).ifPresent(value -> entries.put(key, value));
    }
}
