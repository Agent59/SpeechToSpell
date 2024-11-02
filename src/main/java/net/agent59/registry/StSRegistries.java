package net.agent59.registry;

import com.mojang.serialization.Lifecycle;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.mixin.registry.sync.RegistriesAccessor;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class StSRegistries {
    public static final Registry<SpellTypes.SpellType<? extends Spell>> SPELL_TYPE = createStS(StSRegistryKeys.SPELL_TYPE);
    public static final Registry<SpellStateComponentTypes.SpellStateComponentType<?>> SPELL_STATE_COMPONENT_TYPE = createStS(StSRegistryKeys.SPELL_STATE_COMPONENT_TYPE);
    public static final Registry<TargetAreaTypes.TargetAreaType<? extends TargetArea>> TARGET_AREA_TYPE = createStS(StSRegistryKeys.TARGET_AREA_TYPE);
    public static final Registry<TargetTypes.TargetType<? extends Target>> TARGET_TYPE = createStS(StSRegistryKeys.TARGET_TYPE);

    private static <T> Registry<T> create(RegistryKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }

    /**
     * Used to create an {@link StSSimpleRegistry},
     * which behaves almost the same as a {@link net.minecraft.registry.SimpleRegistry},
     * only changing the  default namespace of the registry's codec.
     * <p>This simply imitates the behaviour of the {@link FabricRegistryBuilder}.
     * @implNote If this should not work anymore {@link #create(RegistryKey)}
     * can always be used as a simple working alternative.
     * @see StSSimpleRegistry
     * @see FabricRegistryBuilder#buildAndRegister()
     */
    @SuppressWarnings("unchecked")
    private static <T> Registry<T> createStS(RegistryKey<Registry<T>> key) {
        StSSimpleRegistry<T> registry = new StSSimpleRegistry<>(key, Lifecycle.stable(), false);
        RegistryKey<?> key1 = registry.getKey();
        RegistryAttributeHolder.get(key).addAttribute(RegistryAttribute.MODDED);
        RegistriesAccessor.getROOT().add((RegistryKey<MutableRegistry<?>>) key1, registry, Lifecycle.stable());
        return registry;
    }
}
