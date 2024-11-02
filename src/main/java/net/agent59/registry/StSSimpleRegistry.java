package net.agent59.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import net.agent59.StSMain;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

/**
 * Behaves the same as {@link SimpleRegistry},<br>
 * the only difference being, that when deserializing a registry entry with {@link #getCodec()},
 * the default namespace is changed to {@link StSMain#MOD_ID}.<br>
 * This means if the namespace is omitted, {@link StSMain#MOD_ID} will be added
 * ({@code entry_name -> speech_to_spell:entry_name}).
 * @see StSRegistries
 * @see net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
 */
public class StSSimpleRegistry<T> extends SimpleRegistry<T> {

    public static final Codec<Identifier> STS_IDENTIFIER =  Codec.STRING.comapFlatMap(
            str -> {
                if (!str.contains(":")) str = StSMain.MOD_ID + ":" + str;
                return Identifier.validate(str);
            },
            Identifier::toString
    ).stable();

    public StSSimpleRegistry(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, boolean intrusive) {
        super(key, lifecycle, intrusive);
    }

    @Override
    public Codec<T> getCodec() {
        Codec<T> codec = STS_IDENTIFIER.flatXmap(id -> Optional.ofNullable(this.get(id)).map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry key in " + this.getKey() + ": " + id)),
                value -> this.getKey(value).map(RegistryKey::getValue).map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown registry element in " + this.getKey() + ":" + value))
        );
        Codec<T> codec2 = Codecs.rawIdChecked(
                value -> this.getKey(value).isPresent() ? this.getRawId(value) : -1, this::get, -1
        );
        return Codecs.withLifecycle(Codecs.orCompressed(codec, codec2), this::getEntryLifecycle, this::getEntryLifecycle);
    }
}
