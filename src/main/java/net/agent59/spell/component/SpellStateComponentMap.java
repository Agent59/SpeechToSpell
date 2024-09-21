package net.agent59.spell.component;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.Map;

/**
 * Holds a map of {@link net.agent59.spell.component.SpellStateComponentTypes.SpellStateComponentType}s.
 */
public class SpellStateComponentMap implements Iterable<Reference2ObjectMap.Entry<SpellStateComponentTypes.SpellStateComponentType<?>, Object>> {
    public static final Codec<SpellStateComponentMap> ERROR_SKIPPING_CODEC =
            SpellStateComponentTypes.SpellStateComponentType.ERROR_SKIPPING_TYPE_TO_VALUE_MAP_CODEC.xmap(
                    map -> new SpellStateComponentMap(new Reference2ObjectArrayMap<>(map)),
                    spellStateComponentMap -> spellStateComponentMap.components
            );

    /**
     * It must always be ensured, that every pair of {@code ?} and {@code Object} are of the same type.
     */
    private final Reference2ObjectMap<SpellStateComponentTypes.SpellStateComponentType<?>, Object> components;

    public SpellStateComponentMap(Reference2ObjectMap<SpellStateComponentTypes.SpellStateComponentType<?>, Object> components) {
        this.components = components;
    }

    public SpellStateComponentMap(Map<SpellStateComponentTypes.SpellStateComponentType<?>, Object> components) {
        this(new Reference2ObjectArrayMap<>(components));
    }

    /**
     * Copy Constructor
     */
    public SpellStateComponentMap(SpellStateComponentMap spellStateComponentMap) {
        this.components = new Reference2ObjectArrayMap<>(spellStateComponentMap.components);
    }

    public @Unmodifiable Reference2ObjectMap<SpellStateComponentTypes.SpellStateComponentType<?>, Object> getMap() {
        return this.components;
    }

    public int size() {
        return components.size();
    }

    public boolean containsKey(SpellStateComponentTypes.SpellStateComponentType<?> type) {
        return this.components.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(SpellStateComponentTypes.SpellStateComponentType<T> type) {
        return (T) this.components.get(type);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T set(SpellStateComponentTypes.SpellStateComponentType<T> type, T value) {
        return (T) this.components.put(type, value);
    }

    /**
     * Same as {@link #set(SpellStateComponentTypes.SpellStateComponentType, Object)} but allows wildcards as input.
     * <p>Will crash if {@code ?} and {@code Object} can't be cast to the same type {@link T}.
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T setWithWildcard(SpellStateComponentTypes.SpellStateComponentType<?> type, Object value) {
        return this.set((SpellStateComponentTypes.SpellStateComponentType<T>) type, (T) value);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T remove(SpellStateComponentTypes.SpellStateComponentType<T> type) {
        return (T) this.components.remove(type);
    }

    @Override
    public @NotNull Iterator<Reference2ObjectMap.Entry<SpellStateComponentTypes.SpellStateComponentType<?>, Object>> iterator() {
        return this.components.reference2ObjectEntrySet().iterator();
    }

    @Override
    public String toString() {
        return this.components.toString();
    }
}
