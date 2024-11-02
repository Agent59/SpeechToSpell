package net.agent59.registry;

import net.agent59.StSMain;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell.spells.Spell;
import net.agent59.spell.target.TargetAreaTypes;
import net.agent59.spell.target.TargetTypes;
import net.agent59.spell.target.areas.TargetArea;
import net.agent59.spell.target.targets.Target;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class StSRegistryKeys {
    public static final RegistryKey<Registry<SpellTypes.SpellType<? extends Spell>>> SPELL_TYPE = of("spell_type");
    public static final RegistryKey<Registry<SpellStateComponentTypes.SpellStateComponentType<?>>> SPELL_STATE_COMPONENT_TYPE = of("spell_state_component_type");
    public static final RegistryKey<Registry<TargetAreaTypes.TargetAreaType<? extends TargetArea>>> TARGET_AREA_TYPE = of("target_area_type");
    public static final RegistryKey<Registry<TargetTypes.TargetType<? extends Target>>> TARGET_TYPE = of("target_type");

    private static <T> RegistryKey<Registry<T>> of(String path) {
        return RegistryKey.ofRegistry(new Identifier(StSMain.MOD_ID, path));
    }
}
