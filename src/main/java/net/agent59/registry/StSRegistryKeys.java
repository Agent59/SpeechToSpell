package net.agent59.registry;

import net.agent59.StSMain;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell.spells.Spell;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class StSRegistryKeys {
    public static final RegistryKey<Registry<SpellTypes.SpellType<? extends Spell>>> SPELL_TYPE = of("spell_type");
    public static final RegistryKey<Registry<SpellStateComponentTypes.SpellStateComponentType<?>>> SPELL_STATE_COMPONENT_TYPE = of("spell_state_component_type");

    private static <T> RegistryKey<Registry<T>> of(String path) {
        return RegistryKey.ofRegistry(new Identifier(StSMain.MOD_ID, path));
    }
}
