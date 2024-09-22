package net.agent59.cardinal_component.player_magic_comp;

import io.netty.handler.codec.EncoderException;
import net.agent59.StSEventListeners;
import net.agent59.cardinal_component.Components;
import net.agent59.command.StSGameRules;
import net.agent59.spell.SpellManager;
import net.agent59.spell.SpellState;
import net.agent59.spell.spells.Spell;
import net.agent59.spell_school.SpellSchool;
import net.agent59.spell_school.SpellSchoolManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The server-side implementation of the {@link PlayerMagicComponent}.
 * <p>It does the following things:
 * <ul>
 *      <li>store data that is allowed to be modified (through various methods)</li>
 *      <li>sync changes to the client ({@link #sync(SyncType, Consumer)}, {@link #fullSync()})</li>
 *      <li>receive messages from the client ({@link #handleC2SMessage(PacketByteBuf)})</li>
 *      <li>handle reloads of datapacks and react to changes ({@link #onDataPacksReloaded()})</li>
 *      <li>react to changes of the {@code SPELL_HOTBAR_SLOTS} gamerule ({@link #onSpellHotbarSlotsGameruleChange()})</li>
 * </ul>
 * <br>
 * <i>The {@code ClientPlayerMagicComponent} cannot be linked here,
 * because it is in the client source and the sources are split.</i>
 *
 * @see Components
 * @see PlayerMagicComponent
 */
public class ServerPlayerMagicComponent extends PlayerMagicComponent {
    /**
     * The SpellStates that will be synced to the client during the next tick.<br>
     * Allows syncing a whole list instead of doing many single syncs.
     */
    private final HashSet<Spell> spellsStatesToBeSynced = new HashSet<>();
    /**
     * Is set when the component is saved during a reload, otherwise {@code null}.<br>
     * The nbt can then be used to reload the PlayerMagicComponent with
     * {@link #readFromNbt(NbtCompound, BiConsumer, BiConsumer, BiConsumer)}.
     * @see #onDataPacksReloaded()
     */
    private @Nullable NbtCompound reloadNbt;

    /**
     * Sets a random SpellSchool and initializes all SpellStates and the spell-hotbar.
     */
    public ServerPlayerMagicComponent(PlayerEntity player) {
        super(player);
        this.spellSchool = SpellSchoolManager.getRandomSchool();
        SpellManager.getSpells().forEach((id, spell) -> spellStates.put(spell, spell.getDefaultState(this.player)));
        this.spellHotbar = new Spell[player.getWorld().getGameRules().getInt(StSGameRules.SPELL_HOTBAR_SLOTS)];
    }

    public boolean isClient() {
        return false;
    }

    /**
     * A utility method for getting the ServerPlayerMagicComponent of the given player in a shorter way.
     * @param player The player of which the ServerPlayerMagicComponent should be returned.
     */
    public static ServerPlayerMagicComponent getInstance(ServerPlayerEntity player) {
        return (ServerPlayerMagicComponent) Components.MAGICIAN.get(player);
    }

    /**
     * Applies necessary changes, when the {@link StSGameRules#SPELL_HOTBAR_SLOTS} gamerule changes.
     * <p> Shortens or lengthens the spell-hotbar, resets the selected slot to {@code 0} and does a {@link #fullSync()}.
     */
    public void onSpellHotbarSlotsGameruleChange() {
        Spell[] newHotbar = new Spell[player.getWorld().getGameRules().getInt(StSGameRules.SPELL_HOTBAR_SLOTS)];
        for (int i = 0; i < newHotbar.length; i++) {
            if (spellHotbar.length == i) break;
            newHotbar[i] = spellHotbar[i];
        }
        this.spellHotbar = newHotbar;
        this.selectedSpellHotbarSlot = 0; // Resets the selected slot because it might be out of bounds.
        this.fullSync();
    }

    /**
     * Applies the necessary changes, when the datapacks are reloaded.
     * <p>Does this by (re-)loading the component from the {@link #reloadNbt}, making use of
     * the {@link #readFromNbt(NbtCompound, BiConsumer, BiConsumer, BiConsumer)}.<br>
     * If the error is an expected one (e.g. because a spell got removed) only a non-error info message is logged.
     * <p>The {@link #reloadNbt} is set inside the {@link #writeToNbt(NbtCompound)} method during a reload.
     * <p>If a reload is in progress can generally be checked with {@link StSEventListeners#isReloadInProgress()}.
     */
    public void onDataPacksReloaded() {
        assert this.reloadNbt != null;
        BiConsumer<String, String> logError = (errorObj, errMsg) -> LOGGER.error("""
                Error(s) when reloading {} of player {}:
                {}""", errorObj, this.player, errMsg);
        this.readFromNbt(
                this.reloadNbt,
                (schoolTag, errMsg) -> {
                    if (errMsg.startsWith(SpellSchoolManager.NO_SUCH_REGISTERED_SPELL_SCHOOL_ERROR)) {
                        LOGGER.info("""
                                The SpellSchool {} was removed. Setting the SpellSchool of player {} to null.
                                """, schoolTag, this.player);
                    } else logError.accept("the SpellSchool", errMsg);
                },
                (stateTag, errMsg) -> {
                    if (errMsg.startsWith(SpellManager.NO_SUCH_REGISTERED_SPELL_ERROR)) {
                        LOGGER.info("""
                                Removing the SpellState {} of player {}, because the spell was removed.
                                """, stateTag, this.player);
                    } else logError.accept("a SpellState", errMsg);
                },
                (hotbarTag, errMsg) -> {
                    if (errMsg.startsWith(SpellManager.NO_SUCH_REGISTERED_SPELL_ERROR)) {
                        LOGGER.info("""
                                The spell {} was removed. Removing it from the spell-hotbar of player {}.
                                """, hotbarTag, this.player);
                    } else logError.accept("the spell-hotbar", errMsg);
                }
        );
        this.reloadNbt = null;
    }

    @Override
    public void setSpellHotbarSlot(@Nullable Spell spell, int slot) {
        assert this.spellHotbarSlotValid(slot);
        this.spellHotbar[slot] = spell;

        this.sync(SyncType.HOTBAR_SLOT, (buf) -> {
            buf.writeInt(slot);
            buf.encodeAsJson(SpellManager.getOptionalCodec(), Optional.ofNullable(spell));
        });
    }

    @Override
    public void setSelectedSpellHotbarSlot(int slot) {
        assert this.spellHotbarSlotValid(slot);
        this.selectedSpellHotbarSlot = slot;
        this.sync(SyncType.SELECTED_HOTBAR_SLOT, (buf) -> buf.writeInt(this.selectedSpellHotbarSlot));
    }

    public void setSpellSchool(SpellSchool school) {
        this.spellSchool = school;
        this.fullSync();
    }

    public void setSpellsUnlockState(Spell spell, SpellState.UnlockState unlockState) {
        this.getSpellsState(spell).setUnlockState(unlockState);
        this.spellsStatesToBeSynced.add(spell);
    }

    @Override
    public void castSpell(Spell spell, boolean speechless) {
        if (!this.canCast(spell, speechless) || spell == this.activeSpell) return;

        if (this.activeSpell != null) {
            this.spellStates.get(this.activeSpell).endCastingEarly();
            this.spellsCoolingDown.add(this.activeSpell);
        }
        this.activeSpell = spell;
        this.spellStates.get(spell).setToActive(speechless);
    }

    /**
     * Responsible for casting spells and cooling them down.
     * <p> Casts the {@link #activeSpell} and cools down the spells in {@link #spellsCoolingDown}.
     * If the active spell has finished, it is added to {@link #spellsCoolingDown}.
     * If a spell has finished its cooldown, it is removed from the cooldown-list and can be cast again.
     * <p> All ticked spells (both active and cooling down)
     * are synced to the player every tick (they are added to {@link #spellsStatesToBeSynced}).
     */
    @Override
    public void serverTick() {
        // Ticks the spell that is currently being cast.
        if (this.activeSpell != null) {
            this.spellsStatesToBeSynced.add(this.activeSpell);
            if (this.getSpellsState(this.activeSpell).tickCasting()) {
                this.spellsCoolingDown.add(this.activeSpell);
                this.activeSpell = null;
            }
        }

        // Cools down the spells that have to be.
        // No for loop is used because it will throw a ConcurrentModificationException when removing while iterating.
        Iterator<Spell> cooldownIter = this.spellsCoolingDown.iterator();
        while (cooldownIter.hasNext()) {
            Spell spell = cooldownIter.next();
            this.spellsStatesToBeSynced.add(spell);
            // Ticks cooldown and checks whether the cooldown has finished.
            if (this.getSpellsState(spell).cooldownTick()) cooldownIter.remove();
        }

        if (!this.spellsStatesToBeSynced.isEmpty()) {
            this.sync(SyncType.STATES, (buf) -> {
                List<SpellState> states = this.spellsStatesToBeSynced.stream().map(this.spellStates::get).toList();
                buf.encodeAsJson(SpellState.CODEC.listOf(), states);
            });
            this.spellsStatesToBeSynced.clear();
        }
    }

    /**
     * The only addition is the clearing of {@link #spellsStatesToBeSynced}.
     */
    @Override
    public void readFromNbt(
            NbtCompound tag, BiConsumer<NbtElement, String> schoolErrLogger,
            BiConsumer<NbtElement, String> stateErrLogger, BiConsumer<NbtElement, String> hotbarErrLogger
    ) {
        this.spellsStatesToBeSynced.clear();
        super.readFromNbt(tag, schoolErrLogger, stateErrLogger, hotbarErrLogger);
    }

    /**
     * The only addition is the setting of {@link #reloadNbt}, which is done when a reload is in progress.
     */
    @Override
    public void writeToNbt(NbtCompound tag) {
        super.writeToNbt(tag);
        if (StSEventListeners.isReloadInProgress()) this.reloadNbt = tag;
    }

    /**
     * Ensures that the component is only synced with the {@link #player} that the component belongs to.
     */
    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player;
    }

    /**
     * {@code Components.MAGICIAN.sync(this.player)} will cause the whole component to be synced to its client,
     * see {@link #fullSync()}.
     */
    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        LOGGER.debug("Fully syncing the PlayerMagicComponent of player {} to the corresponding client.", this.player);
        buf.writeEnumConstant(SyncType.FULL);
        NbtCompound tag = new NbtCompound();
        this.writeToNbt(tag);
        buf.writeNbt(tag);
    }

    public void fullSync() {
        Components.MAGICIAN.sync(this.player);
    }

    /**
     * A utility method used for shortening the creation of syncing calls.
     */
    private void sync(SyncType syncType, Consumer<PacketByteBuf> writer) {
        Components.MAGICIAN.sync(this.player, (buf, recipient) -> {
            buf.writeEnumConstant(syncType);
            writer.accept(buf);
        });
    }

    /**
     * Handles messages from the corresponding client.
     * <p>The {@link C2SMessageType} is matched against to determine what action the client wants the server to perform.
     * <p>This is the receiving end of the sendC2SMessage calls made on the client.
     * @param buf The packet information sent by the client.
     */
    public void handleC2SMessage(PacketByteBuf buf) {
        C2SMessageType message = buf.readEnumConstant(C2SMessageType.class);
        try {
            switch (message) {
                case SELECTED_HOTBAR_SLOT -> this.setSelectedSpellHotbarSlot(buf.readInt());
                case CAST_SPELL -> this.castSpell(buf.decodeAsJson(SpellManager.getCodec()), buf.readBoolean());
                case HOTBAR_SLOT -> {
                    int slot = buf.readInt();
                    Spell spell = buf.decodeAsJson(SpellManager.getOptionalCodec()).orElse(null);
                    if (this.spellHotbarSlotValid(slot)) this.setSpellHotbarSlot(spell, slot);
                    else LOGGER.error("The slot {} is out of bounds {}, " +
                            "when setting the selectedSpellHotbarSlot", slot, this.spellHotbar.length - 1);
                }
                default -> LOGGER.warn("Received unknown message type {} with packet {}", message, buf);
            }
        } catch (EncoderException e) {
            LOGGER.error("Could not decode networking information for message {} from PacketByteBuf {}, " +
                    "due to the following error(s):\n{}", message, buf, e);
        }
    }
}
