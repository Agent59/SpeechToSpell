package net.agent59.cardinal_component;

import io.netty.handler.codec.EncoderException;
import net.agent59.cardinal_component.player_magic_comp.PlayerMagicComponent;
import net.agent59.network.StSNetwork;
import net.agent59.spell.SpellManager;
import net.agent59.spell.SpellState;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * The client-side implementation of the {@link PlayerMagicComponent}.
 * <p>This implementation is registered through a mixin, which overrides the default implementation,
 * see {@link net.agent59.mixin.client.ComponentsMixin} for more.
 *
 * <p>It does the following things:
 * <ul>
 *      <li>store data for it to be available on the client</li>
 *      <li>send messages / actions to the server ({@link #sendC2SMessage(C2SMessageType, Consumer)})</li>
 * </ul>
 *
 * @see net.agent59.mixin.client.ComponentsMixin
 * @see Components
 * @see PlayerMagicComponent
 * @see net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent
 */
public class ClientPlayerMagicComponent extends PlayerMagicComponent {

    public ClientPlayerMagicComponent(PlayerEntity player) {
        super(player);
    }

    /**
     * A utility method for getting the ClientPlayerMagicComponent of the given player in a shorter way.
     * @param player The player of which the ClientPlayerMagicComponent should be returned.
     */
    public static ClientPlayerMagicComponent getInstance(ClientPlayerEntity player) {
        return (ClientPlayerMagicComponent) Components.MAGICIAN.get(player);
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void setSpellHotbarSlot(@Nullable Spell spell, int slot) {
        sendC2SMessage(PlayerMagicComponent.C2SMessageType.HOTBAR_SLOT, buf -> {
            buf.writeInt(slot);
            buf.encodeAsJson(SpellManager.getOptionalCodec(), Optional.ofNullable(spell));
        });
    }

    @Override
    public void setSelectedSpellHotbarSlot(int slot) {
        sendC2SMessage(PlayerMagicComponent.C2SMessageType.SELECTED_HOTBAR_SLOT, buf -> buf.writeInt(slot));
    }

    @Override
    public void castSpell(Spell spell, boolean speechless) {
        sendC2SMessage(PlayerMagicComponent.C2SMessageType.CAST_SPELL, buf -> {
            buf.encodeAsJson(SpellManager.getCodec(), spell);
            buf.writeBoolean(speechless);
        });
    }

    @Override
    public void serverTick() {
        throw new UnsupportedOperationException("ClientPlayerMagicComponent can't be serverTicked.");
    }

    /**
     * A utility method used for shortening the creation of server-to-client-message calls.
     */
    private static void sendC2SMessage(PlayerMagicComponent.C2SMessageType messageType, Consumer<PacketByteBuf> writer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeEnumConstant(messageType);
        writer.accept(buf);
        ClientPlayNetworking.send(StSNetwork.PLAYER_MAGIC_COMP_C2S_MESSAGE_ID, buf);
    }

    /**
     * Handles changes sent by the server.
     * <p>The {@link SyncType} is matched against to determine what data the buf contains
     * and what changes need to be made.
     * <p>This is the receiving end of the sync calls made on the server.
     * @param buf The packet information sent by the server.
     */
    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        PlayerMagicComponent.SyncType status = buf.readEnumConstant(PlayerMagicComponent.SyncType.class);
        try {
            switch (status) {
                case PlayerMagicComponent.SyncType.STATES -> {
                    for (SpellState state : buf.decodeAsJson(SpellState.CODEC.listOf())) {
                        state.setEntity(this.player);
                        this.spellStates.put(state.getSpell(), state);
                        this.activeSpell = null; // If the active spell is not actually null, this will be corrected down below.
                        switch (state.getPhase()) {
                            case SpellState.Phase.IS_BEING_CAST -> this.activeSpell = state.getSpell();
                            case SpellState.Phase.COOLING_DOWN -> this.spellsCoolingDown.add(state.getSpell());
                            case SpellState.Phase.INACTIVE -> this.spellsCoolingDown.remove(state.getSpell());
                        }
                    }
                }
                case PlayerMagicComponent.SyncType.SELECTED_HOTBAR_SLOT -> {
                    int slot = buf.readInt();
                    if (this.spellHotbarSlotValid(slot)) this.selectedSpellHotbarSlot = slot;
                    else LOGGER.error("The slot {} is out of bounds {}, " +
                            "when setting the selectedSpellHotbarSlot", slot, this.spellHotbar.length - 1);
                }
                case PlayerMagicComponent.SyncType.HOTBAR_SLOT -> {
                    int slot = buf.readInt();
                    if (this.spellHotbarSlotValid(slot)) {
                        this.spellHotbar[slot] = buf.decodeAsJson(SpellManager.getOptionalCodec()).orElse(null);
                    } else LOGGER.error("The slot {} is out of bounds {}, when updating the spellHotbar",
                            slot, this.spellHotbar.length - 1);
                }
                case PlayerMagicComponent.SyncType.FULL -> {
                    NbtCompound tag = buf.readNbt();
                    if (tag != null) this.readFromNbt(tag);
                    else LOGGER.error("Could not read nbtCompound from packetByteBuf {}, " +
                            "", buf);
                }
                default -> LOGGER.warn("Received unknown sync status {} with packet {}", status, buf);
            }
        } catch (EncoderException e) {
            LOGGER.error("Could not decode networking information for status {} from packetByteBuf {}, " +
                    "due to the following error:\n{}", status, buf, e);
        }
    }
}
