package net.agent59.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agent59.codecs.CodecUtil;
import net.agent59.item.custom.WandItem;
import net.agent59.spell.component.SpellStateComponentMap;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell.spells.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Stores the state of a {@link Spell}.
 *
 * <p>This is a data container similar to {@link net.minecraft.item.ItemStack}
 * and holds data in regard to a {@link #spell} of an {@link #entity}.
 *
 * <p>Every magical entity stores a state for each spell in its {@link net.agent59.cardinal_component.MagicComponent}.
 * <p>The SpellState also controls the casting duration and cooldown of its spell.
 *
 * @see Spell
 * @see net.agent59.cardinal_component.MagicComponent
 * @see net.agent59.cardinal_component.player_magic_comp.PlayerMagicComponent
 */
public class SpellState {
    public static final Codec<SpellState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SpellManager.getCodec().fieldOf("spell").forGetter(SpellState::getSpell),
            UnlockState.CODEC.fieldOf("spell_unlock_state").forGetter(SpellState::getUnlockState),
            Codecs.NONNEGATIVE_INT.fieldOf("remaining_cooldown").forGetter(SpellState::getRemainingCooldown),
            Codecs.NONNEGATIVE_INT.fieldOf("remaining_casting_ticks").forGetter(SpellState::getRemainingCastingTicks),
            Phase.CODEC.fieldOf("phase").forGetter(SpellState::getPhase),
            Codec.BOOL.fieldOf("is_casting_speechless").forGetter(SpellState::isCastingSpeechless),
            SpellStateComponentMap.ERROR_SKIPPING_CODEC.fieldOf("components").forGetter(SpellState::getUnmodifiableComponentMap)
    ).apply(instance, SpellState::new));

    /**
     * Will be null after being loaded with a codec.
     * <p><strong>This field should only be changed by {@link #setEntity(LivingEntity)}
     * directly after the state was loaded with a codec.</strong>
     */
    private LivingEntity entity;
    private final Spell spell;
    private UnlockState spellUnlockState;
    /**
     * Set to 0 when not cooling down.
     */
    private int remainingCooldown;
    /**
     * Set to 0 when not casting.
     */
    private int remainingCastingTicks;
    private Phase phase;
    /**
     * Only relevant during {@link Phase#IS_BEING_CAST}.
     */
    private boolean isCastingSpeechless;
    /**
     * Custom data, spells might store.
     */
    private final SpellStateComponentMap components;

    public SpellState(
            Spell spell,
            UnlockState unlockState,
            int remainingCooldown,
            int remainingCastingTicks,
            Phase phase, boolean isCastingSpeechless,
            SpellStateComponentMap components
    ) {
        this.spell = spell;
        this.spellUnlockState = unlockState;
        this.remainingCooldown = remainingCooldown;
        this.remainingCastingTicks = remainingCastingTicks;
        this.phase = phase;
        this.isCastingSpeechless = isCastingSpeechless;
        this.components = components;
    }

    /**
     * Creates an inactive SpellState.
     */
    public SpellState(LivingEntity entity, Spell spell, UnlockState unlockState, SpellStateComponentMap components) {
        this(spell, unlockState, 0, 0, Phase.INACTIVE, false, components);
        this.setEntity(entity);
    }

    /**
     * Copy Constructor – Returns a copy of the given SpellState.
     */
    public SpellState(SpellState state) {
        this.entity = state.entity;
        this.spell = state.spell;
        this.spellUnlockState = state.spellUnlockState;
        this.remainingCooldown = state.remainingCooldown;
        this.remainingCastingTicks = state.remainingCastingTicks;
        this.phase = state.phase;
        this.isCastingSpeechless = state.isCastingSpeechless;
        this.components = new SpellStateComponentMap(state.components);
    }

    /**
     * Should only be called directly after being loaded with a codec to set the entity.
     *
     * <p>The entity is not saved with the codec because an entities uuid can change
     * e.g. when joining a single-player world while not being logged in.
     *
     * @param entity The entity this state belongs to.
     */
    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * Updates the state's fields in regard to the spell's configuration.
     * <p>Ensures no values are worse than what the spell's default configuration provides.
     * <p>E.g. if the minCastingChance of the spell was changed
     * and the default value is now higher than what is stored in the SpellState,
     * the SpellState should be updated to not contain a value lower than what the minCastingChance of the spell is.
     * @return {@code true} when the SpellState changed.
     */
    public boolean update() {
        boolean madeChanges = false;

        for (Map.Entry<SpellStateComponentTypes.SpellStateComponentType<?>, Object> entry : this.spell.getRequiredSpellStateComponents().entrySet()) {
            if (this.getComp(entry.getKey()) == null) {
                this.components.setWithWildcard(entry.getKey(), entry.getValue());
                madeChanges = true;
            }
        }

        UnlockState defaultUnlockState = this.spell.getDefaultUnlockState();
        if (7 == this.getUnlockState().value + defaultUnlockState.value) { // Combines wandless and speechless.
            this.setUnlockState(UnlockState.SPEECHLESS_AND_WANDLESS);
            madeChanges = true;
        }
        else if (this.getUnlockState().value < defaultUnlockState.value) { // Chooses the better unlock-state.
            this.setUnlockState(defaultUnlockState);
            madeChanges = true;
        }

        if (this.remainingCooldown > this.spell.getCooldownTime()) { // If the cooldown got shortened
            this.remainingCooldown = this.spell.getCooldownTime();
            madeChanges = true;
        }

        if (this.remainingCastingTicks > this.spell.getCastingTime()) { // If the casting time got shortened
            this.remainingCastingTicks = this.spell.getCastingTime();
            madeChanges = true;
        }

        return madeChanges;
    }

    /**
     * @return The entity this state belongs to.
     */
    public LivingEntity getEntity() {
        return this.entity;
    }

    /**
     * @return The spell this state is attached to.
     */
    public Spell getSpell() {
        return this.spell;
    }

    /**
     * @return The unlock-state of the entities spell.
     */
    public UnlockState getUnlockState() {
        return this.spellUnlockState;
    }

    /**
     * Sets the unlock-state of the entities spell.
     */
    public void setUnlockState(UnlockState unlockState) {
        this.spellUnlockState = unlockState;
    }

    public boolean canCastSpeechless() {
        return this.getUnlockState() == UnlockState.SPEECHLESS || this.getUnlockState() == UnlockState.SPEECHLESS_AND_WANDLESS;
    }

    public boolean canCastWandless() {
        return this.getUnlockState() == UnlockState.WANDLESS || this.getUnlockState() == UnlockState.SPEECHLESS_AND_WANDLESS;
    }

    /**
     * @return The remaining cooldown.
     */
    public int getRemainingCooldown() {
        return this.remainingCooldown;
    }

    /**
     * @return The remaining duration the spell will be cast for.
     */
    public int getRemainingCastingTicks() {
        return this.remainingCastingTicks;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public boolean isCastingSpeechless() {
        return this.isCastingSpeechless;
    }

    /**
     * <strong>The states components should not be modified through the returned SpellStateComponentMap,</strong>
     * <p>use {@link #getComp(SpellStateComponentTypes.SpellStateComponentType)},
     * {@link #setComp(SpellStateComponentTypes.SpellStateComponentType, Object)}
     * and {@link #removeComp(SpellStateComponentTypes.SpellStateComponentType)} instead.
     * @return The states SpellStateComponentMap which should not be modified directly.
     */
    public @Unmodifiable SpellStateComponentMap getUnmodifiableComponentMap() {
        return this.components;
    }

    public <T> T getComp(SpellStateComponentTypes.SpellStateComponentType<T> type) {
        return this.components.get(type);
    }

    public <T> T setComp(SpellStateComponentTypes.SpellStateComponentType<T> type, T value) {
        return this.components.set(type, value);
    }

    /**
     * @throws UnsupportedOperationException When a component type, that the spell requires, is attempted to be removed.
     */
    public <T> T removeComp(SpellStateComponentTypes.SpellStateComponentType<T> type) {
        if (this.spell.getRequiredSpellStateComponents().containsKey(type)) {
            throw new UnsupportedOperationException("cannot remove component of type " + type +
                    " from SpellState " + this + " because it is required by its spell " + this.spell);
        }
        return this.components.remove(type);
    }

    /**
     * <p>There are multiple conditions that have to be met so the entity can cast the spell:
     * <ul>
     *      <li>It must either hold a wand</li>
     *      <li>or be able to perform the spell without a wand (wandless).</li>
     *      <li>Its spell must also not be cooling down.</li>
     * </ul>
     * @param speechless Whether the spell might be cast speechless,<br>which can't be checked / verified by the server.
     * @return {@code true} when the spell can be cast.
     */
    public boolean canCast(boolean speechless) {
        return (this.getPhase() != Phase.COOLING_DOWN) &&
                (!speechless || this.canCastSpeechless()) &&
                (this.entity.getMainHandStack().getItem() instanceof WandItem ||
                        this.entity.getOffHandStack().getItem() instanceof WandItem || this.canCastWandless()) &&
                this.spell.canCast(this);
    }

    /**
     * Should be called before starting to tick casting.
     * @see net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent#castSpell(Spell, boolean)
     */
    public void setToActive(boolean speechless) {
        this.phase = Phase.IS_BEING_CAST;
        this.isCastingSpeechless = speechless;
        this.remainingCastingTicks = this.spell.getCastingTime();
    }

    /**
     * Controls the time for which the spell is cast.
     *
     * <p>Calls the spells {@link Spell#tickCasting(SpellState)},
     * which controls the actual logic of the spell.
     *
     * <p><strong>Should only be called when in {@code Phase.IS_BEING_CAST}</strong><br>
     * and only by the entities MagicComponent.
     *
     * @return {@code true} when the spell finishes casting.
     */
    public boolean tickCasting() {
        assert this.phase == Phase.IS_BEING_CAST;
        // Checks whether the spell can be cast, and if so ticks it.
        // Otherwise, the spell is ended early. Though spell might end casting early as well.
        if (!this.canCast(this.isCastingSpeechless) || this.spell.tickCasting(this)) {
            this.endCastingEarly();
            return true;
        }
        this.remainingCastingTicks--;
        if (this.remainingCastingTicks <= 0) {
            this.onCastingEnd();
            return true;
        }
        return false;
    }

    /**
     * Is called if the spell is stopped early during casting, e.g. when the player tries to cast another spell.
     */
    public void endCastingEarly() {
        // TODO maybe create an event here, e.g. for the animation system
        this.spell.endEarly(this);
        this.onCastingEnd();
    }

    private void onCastingEnd() {
        this.remainingCooldown = this.spell.getCooldownTime();
        this.remainingCastingTicks = 0;
        this.phase = Phase.COOLING_DOWN;
    }

    /**
     * <p><strong>Should only be called when in {@code Phase.COOLING_DOWN}</strong><br>
     * and only by the entities MagicComponent.
     * @return {@code true} when the cooldown has finished.
     */
    public boolean cooldownTick() {
        assert this.phase == Phase.COOLING_DOWN;
        this.remainingCooldown--;
        if (this.remainingCooldown <= 0) {
            this.onCooldownEnd();
            return true;
        }

        boolean shouldEndCooldownEarly = this.spell.tickCooldown(this);
        if (shouldEndCooldownEarly) {
            this.endCooldownEarly();
        }
        return shouldEndCooldownEarly;
    }

    public void endCooldownEarly() {
        this.onCooldownEnd();
    }

    private void onCooldownEnd() {
        this.remainingCooldown = 0;
        this.phase = Phase.INACTIVE;
    }

    public enum Phase {
        IS_BEING_CAST,
        COOLING_DOWN,
        INACTIVE;
        public static Codec<Phase> CODEC = CodecUtil.getEnumCodec(Phase.class);
    }

    public enum UnlockState {
        LOCKED(0),
        UNLOCKED(1),
        SPEECHLESS(3),
        WANDLESS(4),
        SPEECHLESS_AND_WANDLESS(9);
        /**
         * The values are used for calculating updates, see {@link #update()}.
         */
        public final int value;
        UnlockState(int value) { this.value = value; }
        public static Codec<UnlockState> CODEC = CodecUtil.getEnumCodec(UnlockState.class);
    }

    @Override
    public String toString() {
        BiFunction<String, Object, String> lastField = (name, value) -> "\"" + name + "\": " + value;
        BiFunction<String, Object, String> field = (name, value) -> lastField.apply(name, value) + ", ";
        return "SpellState{" +
                field.apply("entity", this.entity) +
                field.apply("spell", this.spell.getId()) +
                field.apply("spell_unlocked_state", this.spellUnlockState) +
                field.apply("remaining_cooldown", this.remainingCooldown) +
                field.apply("remaining_casting_ticks", this.remainingCastingTicks) +
                field.apply("phase", this.phase) +
                field.apply("is_casting_speechless", this.isCastingSpeechless) +
                lastField.apply("components", this.components.getMap()) + "}";
    }
}
