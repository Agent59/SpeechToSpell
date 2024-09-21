package net.agent59.spell.component;

import com.mojang.serialization.Codec;
import net.agent59.StSMain;
import net.agent59.codecs.ErrorSkippingDispatchedMapCodec;
import net.agent59.registry.StSRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Map;

/**
 * {@link SpellStateComponentType}s enable the storage of more customizable
 * and spell-specific data inside {@link net.agent59.spell.SpellState}s.
 */
public class SpellStateComponentTypes {
    public static final SpellStateComponentType<Integer> INDEX = register("index", Codec.INT);
    public static final SpellStateComponentType<Integer> RANGE = register("range", Codecs.NONNEGATIVE_INT);

    /**
     * This method may only be used to register the component types the SpeechToSpell mod implements directly.
     */
    private static <T> SpellStateComponentType<T> register(String path, Codec<T> codec) {
        return register(StSMain.id(path), codec);
    }

    /**
     * Other mods may use this method to register more {@link SpellStateComponentType}s.
     * @param id The identifier of the component type.
     * @param codec The codec with which the stored objects can be serialized and deserialized.
     * @return The registered component type.
     * @param <T> The type object that is stored for the component type.
     */
    public static <T> SpellStateComponentType<T> register(Identifier id, Codec<T> codec) {
        return Registry.register(StSRegistries.SPELL_STATE_COMPONENT_TYPE, id, new SpellStateComponentType<>(codec));
    }

    public static void initialize() {
        StSMain.LOGGER.info("Registering SpellStateComponentTypes for " + StSMain.MOD_NAME);
    }

    /**
     * Used to identify the type of object and provide the codec
     * for tracking more customizable data in a {@link net.agent59.spell.SpellState}.
     * @param codec The codec for the object.
     * @param <T> The type of object that is saved.
     */
    public record SpellStateComponentType<T>(Codec<T> codec) {
        public static final Codec<Map<SpellStateComponentType<?>, Object>> ERROR_SKIPPING_TYPE_TO_VALUE_MAP_CODEC =
                new ErrorSkippingDispatchedMapCodec<>(
                        StSRegistries.SPELL_STATE_COMPONENT_TYPE.getCodec(),
                        SpellStateComponentType::codec
                );

        @Override
        public String toString() {
            Identifier identifier = StSRegistries.SPELL_STATE_COMPONENT_TYPE.getId(this);
            return identifier == null ? "[unregistered]" : identifier.toString();
        }
    }
}
