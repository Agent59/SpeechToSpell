package net.agent59.registry;

import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class ModRegistries {
    public static final Registry<SpellTypes.SpellType<? extends Spell>> SPELL_TYPE = create(ModRegistryKeys.SPELL_TYPE);
    public static final Registry<SpellStateComponentTypes.SpellStateComponentType<?>> SPELL_STATE_COMPONENT_TYPE = create(ModRegistryKeys.SPELL_STATE_COMPONENT_TYPE);

    private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
}
