package net.agent59.cardinal_component.player_magic_comp;

import net.agent59.cardinal_component.Components;
import net.agent59.cardinal_component.MagicComponent;
import net.agent59.command.StSGameRules;
import net.agent59.spell.SpellManager;
import net.agent59.spell.SpellState;
import net.agent59.spell.spells.Spell;
import net.agent59.spell_school.SpellSchool;
import net.agent59.spell_school.SpellSchoolManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Attaches logic and data, that are needed for SpeechToSpells magic, to players.
 *
 * <p>The PlayerMagicComponent is split into the {@link ServerPlayerMagicComponent} and {@code ClientPlayerMagicComponent}.
 * Though the ClientPlayerMagicComponent is registered through a mixin,
 * see {@link Components} for more information.
 *
 * <p>Logic such as casting or cooling down {@link Spell}s is executed in {@link #serverTick()} only on the server
 * and the changes are then synced to the client ({@link SyncType}).<br>
 * If the client wants the server to do something, a server-to-client message is used ({@link C2SMessageType}).<br>
 * Thanks to that, most methods can also be called on the client and the action is automatically sent to the server.
 *
 * <p>Every player stores a {@link SpellState} for each spell in a hashmap ({@link #spellStates}).<br>
 * The state of a players spell is accessed with the spell as the key ({@link #getSpellsState(Spell)}).
 * <br>
 * <p>The following data is attached to every player each and independently:
 * <ul>
 *      <li>the players {@link SpellSchool} ({@link #spellSchool})</li>
 *      <li>a state for each spell ({@link #spellStates})</li>
 *      <li>the spell the player is currently casting ({@link #activeSpell})</li>
 *      <li>the spells that are cooling down ({@link #spellsCoolingDown})</li>
 *      <li>the spells the player has in his spell-hotbar ({@link #spellHotbar})</li>
 *      <li>the slot of the spell-hotbar the player has selected ({@link #selectedSpellHotbarSlot})</li>
 * </ul>
 * <br>
 * <i>The {@code ClientPlayerMagicComponent} cannot be linked here,
 * because it is in the client source and the sources are split.</i>
 *
 * @see Components
 * @see ServerPlayerMagicComponent
 */
public abstract class PlayerMagicComponent implements MagicComponent {
    public static final String SPELL_SCHOOL_FIELD = "spell_school";
    public static final String SPELL_STATES_FIELD = "spell_states";
    public static final String SPELL_HOTBAR_FIELD = "spell_hotbar";
    public static final String SELECTED_SPELL_HOTBAR_SLOT_FIELD = "selected_spell_hotbar_slot";
    public static final String SPELL_HOTBAR_SIZE_FIELD = "spell_hotbar_size_field";
    protected static final Logger LOGGER = LogManager.getLogger();

    protected final PlayerEntity player;

    protected @Nullable SpellSchool spellSchool;
    protected final HashMap<Spell, SpellState> spellStates = new HashMap<>();
    protected @Nullable Spell activeSpell = null;
    protected final HashSet<Spell> spellsCoolingDown = new HashSet<>();
    /**
     * The actual initialization is done in the {@link ServerPlayerMagicComponent}.
     * <p>The actual size of the spellHotbar depends on the {@link StSGameRules#SPELL_HOTBAR_SLOTS} gamerule
     * and the correct size is synced to the client during a full sync.
     * @see ServerPlayerMagicComponent#onSpellHotbarSlotsGameruleChange()
     */
    protected @Nullable Spell @NotNull[] spellHotbar = new Spell[0];
    protected int selectedSpellHotbarSlot = 0;

    public PlayerMagicComponent(PlayerEntity player) {
        this.player = player;
    }

    /**
     * Used to check whether this is the {@code ClientPlayerMagicComponent} or the {@link ServerPlayerMagicComponent}.
     * <p>On an integrated server both the client and server version are created.
     * @return {@code true} if this is the {@code ClientPlayerMagicComponent}.
     */
    public abstract boolean isClient();

    /**
     * @param spell The spell of which the state is being retrieved.
     * @return The state of the spell.
     */
    public SpellState getSpellsState(Spell spell) {
        return this.spellStates.get(spell);
    }

    /**
     * The returned spell-hotbar should not be modified directly.
     * @return The spells the player has in his spell-hotbar.
     */
    public Spell[] getSpellHotbar() {
        return this.spellHotbar;
    }

    public abstract void setSpellHotbarSlot(@Nullable Spell spell, int slot);

    public int getSelectedSpellHotbarSlot() {
        return this.selectedSpellHotbarSlot;
    }

    /**
     * Will always return {@code null} if the {@link #spellHotbar} is of length {@code 0}.
     */
    public @Nullable Spell getSelectedSpellHotbarSpell() {
        if (this.spellHotbar.length == 0) return null;
        return this.spellHotbar[this.selectedSpellHotbarSlot];
    }

    public abstract void setSelectedSpellHotbarSlot(int slot);

    /**
     * Will always return {@code false} if the {@link #spellHotbar} is of length {@code 0}.
     */
    public boolean spellHotbarSlotValid(int slot) {
        return 0 <= slot && slot < this.spellHotbar.length;
    }

    /**
     * @return The SpellSchool of the player or {@code null} if the player doesn't have a SpellSchool.
     */
    @Override
    public @Nullable SpellSchool getSpellSchool() {
        return this.spellSchool;
    }

    /**
     * @return The spell the player is currently casting
     * or {@code null} if the player is not casting a spell.
     */
    @Override
    public @Nullable Spell getActiveSpell() {
        return this.activeSpell;
    }

    /**
     * Makes the player cast a spell.
     * <p>Casting may be refused without notice,
     * i.e. if the player is not holding a wand but would need to,to cast the spell.
     * @param spell The spell that should be cast.
     * @param speechless Whether the spell is being cast speechless,<br>which can't be checked / verified by the server.
     */
    @Override
    public abstract void castSpell(Spell spell, boolean speechless);

    /**
     * @return A set of the players spells, which are cooling down.
     */
    @Override
    public Set<Spell> getSpellsCoolingDown() {
        return this.spellsCoolingDown;
    }

    /**
     * Checks whether the player is allowed to cast the given spell.
     * @param spell The spell that is checked.
     * @param speechless Whether the spell might be cast speechless,<br>which can't be checked / verified by the server.
     * @return {@code true} if the player is allowed to cast the spell.
     */
    @Override
    public boolean canCast(Spell spell, boolean speechless) {
        return this.getSpellsState(spell).canCast(speechless);
    }

    /**
     * Loads the PlayerMagicComponent from nbt.
     * <p>Works both for the client and server, though with some checks
     * and the ServerPlayerMagicComponent has to clear a private field first.
     * <p>Allows for custom logger messages on parsing errors,
     * where strings are the error messages and the NbtElements are the data that could not be parsed.
     * @param tag The nbt from which the PlayerMagicComponent is loaded.
     * @param schoolErrLogger Called on an error when parsing the players school.
     * @param stateErrLogger  Called on an error when parsing the players states.
     * @param hotbarErrLogger Called on an error when parsing the players hotbar.
     * @see ServerPlayerMagicComponent
     */
    public void readFromNbt(
            NbtCompound tag,
            BiConsumer<NbtElement, String> schoolErrLogger,
            BiConsumer<NbtElement, String> stateErrLogger,
            BiConsumer<NbtElement, String> hotbarErrLogger
    ) {
        this.spellStates.clear();
        SpellManager.getSpells().forEach((id, spell) -> this.spellStates.put(spell, spell.getDefaultState(this.player)));
        this.activeSpell = null;
        this.spellsCoolingDown.clear();
        if (!this.isClient()) {
            this.spellHotbar = new Spell[this.player.getWorld().getGameRules().getInt(StSGameRules.SPELL_HOTBAR_SLOTS)];
        } else { // Gamerules are not synced to the client, thus the size of the spell-hotbar has to be synced.
            this.spellHotbar = new Spell[tag.getInt(SPELL_HOTBAR_SIZE_FIELD)];
        }

        this.spellSchool = SpellSchoolManager.getOptionalCodec().parse(NbtOps.INSTANCE, tag.get(SPELL_SCHOOL_FIELD))
                .resultOrPartial((errMsg) -> schoolErrLogger.accept(tag.get(SPELL_SCHOOL_FIELD), errMsg))
                .orElse(Optional.empty()).orElse(null);

        for (NbtElement e : tag.getList(SPELL_STATES_FIELD, NbtElement.COMPOUND_TYPE)) {
            SpellState.CODEC.parse(NbtOps.INSTANCE, e)
                    .resultOrPartial((errMsg) -> stateErrLogger.accept(e, errMsg))
                    .ifPresent(spellState -> {
                        spellState.setEntity(this.player);
                        if (!this.isClient()) {
                            SpellState oldState = new SpellState(spellState); // Copy is only used for logging.
                            if (spellState.update()) { // True if the update changed the state.
                                LOGGER.info("Updated the old spellState {} of player {} " +
                                        "to the new spellState {}.", oldState, this.player.getName(), spellState);
                            }
                        }
                        this.spellStates.put(spellState.getSpell(), spellState);

                        switch (spellState.getPhase()) {
                            case SpellState.Phase.IS_BEING_CAST -> {
                                // In case there are somehow multiple spells in Phase IS_BEING_CAST
                                // one spells Phase must be corrected (endCastingEarly sets the Phase to COOLING_DOWN).
                                if (this.activeSpell != null) spellState.endCastingEarly();
                                else this.activeSpell = spellState.getSpell();
                            }
                            case SpellState.Phase.COOLING_DOWN -> this.spellsCoolingDown.add(spellState.getSpell());
                        }
                    });
        }

        NbtList spellHotbarNbtList = tag.getList(SPELL_HOTBAR_FIELD, NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < this.spellHotbar.length; i++) {
            if (spellHotbarNbtList.size() <= i) break;
            NbtElement e = spellHotbarNbtList.get(i);
            Spell spell = SpellManager.getOptionalCodec().parse(NbtOps.INSTANCE, e)
                    .resultOrPartial((errMsg) -> hotbarErrLogger.accept(e, errMsg))
                    .orElse(Optional.empty()).orElse(null);
            this.spellHotbar[i] = spell;
        }
        this.selectedSpellHotbarSlot = tag.getInt(SELECTED_SPELL_HOTBAR_SLOT_FIELD); // Defaults to 0 if not found.
    }

    /**
     * Loads the PlayerMagicComponent from nbt.
     * <p>Uses the {@link #readFromNbt(NbtCompound, BiConsumer, BiConsumer, BiConsumer)} method
     * but with the logger messages already supplied.<br>
     * The errors are logged as errors with their error message and the corresponding nbt.
     * @param tag The nbt from which the PlayerMagicComponent is loaded.
     */
    @Override
    public void readFromNbt(NbtCompound tag) {
        this.readFromNbt(
                tag,
                (schoolTag, errMsg) -> LOGGER.error("""
                        Could not read the SpellSchool {} of player {} from nbt, due to the following error(s):
                        {}
                        Setting the players school to null.
                        """, schoolTag, this.player, errMsg),
                (stateTag, errMsg) -> LOGGER.error("""
                            Could not read the SpellState {} of player {} from nbt, due to the following error(s):
                            {}
                            If the spell is registered, its default state will be used, otherwise skipping it.
                            """, stateTag, this.player, errMsg),
                (hotbarTag, errMsg) -> LOGGER.error("""
                            Could not read the spell {} of the spell-hotbar of player {} from nbt, \
                            due to the following error(s):
                            {}
                            Setting it to null.
                            """, hotbarTag, this.player, errMsg)
        );
    }

    /**
     * Writes the PlayerMagicComponent to the given nbt.
     * <p>Used for saving the component to disk and syncing the full component to the client.
     * <p>Not needed on the client, but is inherited anyway,
     * as this method may be used by other modders
     * and there are possible future use cases not to be mentioned here.
     * @param tag The NbtCompound on which the component's data should be written.
     */
    @Override
    public void writeToNbt(NbtCompound tag) {
        SpellSchoolManager.getOptionalCodec().encodeStart(NbtOps.INSTANCE, Optional.ofNullable(this.spellSchool))
                .resultOrPartial((errMsg) -> LOGGER.error("""
                        Could not write the SpellSchool {} of player {} to nbt, due to the following error(s):
                        {}
                        The players school will not be saved.
                        """, this.spellSchool, this.player, errMsg)
                ).ifPresent(schoolNbt -> tag.put(SPELL_SCHOOL_FIELD, schoolNbt));

        NbtList spellStatesNbtList = new NbtList();
        for (SpellState spellState : this.spellStates.values()) {
            SpellState.CODEC.encodeStart(NbtOps.INSTANCE, spellState)
                    .resultOrPartial((errMsg) -> LOGGER.error("""
                            Could not write the SpellState {} for spell {} of player {} to nbt, \
                            due to the following error(s):
                            {}
                            The players state for this spell will not be saved.
                            """, spellState, spellState.getSpell(), this.player, errMsg)
                    ).ifPresent(spellStatesNbtList::add);
        }
        tag.put(SPELL_STATES_FIELD, spellStatesNbtList);

        // Only read on the client as this is used for syncing the spell-hotbar size to the client.
        tag.putInt(SPELL_HOTBAR_SIZE_FIELD, this.spellHotbar.length);

        NbtList spellHotbarNbtList = new NbtList();
        for (Spell spell : this.spellHotbar) {
            SpellManager.getOptionalCodec().encodeStart(NbtOps.INSTANCE, Optional.ofNullable(spell))
                    .resultOrPartial((errMsg) -> LOGGER.error("""
                            Could not write the spell {} from the spell-hotbar of player {} to nbt, \
                            due to the following error(s):
                            {}
                            The spell will not be saved to the players spell-hotbar.
                            """, spell, this.player, errMsg)
                    ).ifPresent(spellHotbarNbtList::add);
        }
        tag.put(SPELL_HOTBAR_FIELD, spellHotbarNbtList);

        tag.putInt(SELECTED_SPELL_HOTBAR_SLOT_FIELD, this.selectedSpellHotbarSlot);
    }

    /**
     * Used to specify what kind of information the {@link PacketByteBuf}
     * should contain, when the servers data is synced to the client.
     * <p><i>The {@code ClientPlayerMagicComponent} cannot be linked here,
     * because it is in the client source and the sources are split.</i><br>
     * But still have a look at the clients {@code applySyncPacket(PacketByteBuf)} method.
     * @see ServerPlayerMagicComponent#sync(SyncType, Consumer)
     * @see ServerPlayerMagicComponent#fullSync()
     */
    public enum SyncType {
        STATES,
        SELECTED_HOTBAR_SLOT,
        HOTBAR_SLOT,
        FULL,
    }

    /**
     * Used to specify what kind of information the {@link PacketByteBuf} should contain,
     * when the client requests the server to do something.
     * <p><i>The {@code ClientPlayerMagicComponent} cannot be linked here,
     * because it is in the client source and the sources are split.</i><br>
     * But still have a look at the clients
     * {@code sendC2SMessage(PlayerMagicComponent.C2SMessageType, Consumer)} method.
     * @see ServerPlayerMagicComponent#handleC2SMessage(PacketByteBuf)
     */
    public enum C2SMessageType {
        SELECTED_HOTBAR_SLOT,
        CAST_SPELL,
        HOTBAR_SLOT,
    }
}
