package net.agent59.spell;

import com.mojang.serialization.Codec;
import net.agent59.Main;
import net.agent59.registry.ModRegistries;
import net.agent59.spell.spells.Spell;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * A Registry for {@link SpellType}s.
 *
 * <p>SpellTypes are used to determine which Spell-Class should be used when loading a spell from json.<br>
 * The codec contains the corresponding deserialization approach.
 *
 * <p>With the {@code "spell_type"} field in a spells json file
 * the {@link SpellManager} can dispatch to the correct codec.
 *
 * <p>Spells need to return their corresponding type in their {@link Spell#getType()} method.
 *
 * @see <a href="https://docs.fabricmc.net/1.20.4/develop/codecs#registry-dispatch">Fabric-Wiki Registry Dispatch</a>
 */
public class SpellTypes {

    /**
     * This method may only be used to register the spells the SpeechToSpell mod implements directly.
     */
    private static <T extends Spell> SpellType<T> register(String name, Codec<T> spellCodec) {
        return register(Main.id(name), spellCodec);
    }

    /**
     * Registers a new {@link SpellType}.
     * <p>This method may be used by other mods that want to create their own Spell-Classes.
     * @param identifier The id of the spell.
     * @param spellCodec The codec determines how a spell can be configured from json.
     * @param <T> The class of the spell.
     */
    public static <T extends Spell> SpellType<T> register(Identifier identifier, Codec<T> spellCodec) {
        return Registry.register(ModRegistries.SPELL_TYPE, identifier, new SpellType<>(spellCodec));
    }

    public static void initialize() {
        Main.LOGGER.info("Registering Spells for " + Main.MOD_NAME);
    }

    /**
     * Holds the codec that is needed to load a spell of the corresponding type.
     * @param codec The codec that is used to load the configuration of a spell of this type.
     * @param <T> The class all spells of this type use.
     */
    public record SpellType<T extends Spell>(Codec<T> codec) {}
}