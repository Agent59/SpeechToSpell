package net.agent59;

import net.agent59.item.ModItems;
import net.agent59.item.custom.WandItem;
import net.agent59.spell.SpellHandler;
import net.agent59.spell.spells.Portus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Network {

    // executes the spell, the client has sent as a String, on the server
    public static final boolean SPELL = ServerPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "spell"), ((server, player, handler, buf, responseSender) -> {
                String spellString = buf.readString();
                server.execute(() -> SpellHandler.executeSpellIfAllowed(player, spellString));
            }));

    public static final boolean UPDATE_WAND_NBT = ServerPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "update_wand_nbt"), ((server, player, handler, buf, responseSender) -> {
                NbtCompound nbt = buf.readNbt();
                server.execute(() -> {
                    ItemStack wand;
                    if (((wand = player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) {
                        if (wand.getNbt() == null) {
                            wand.setNbt(new NbtCompound());
                        }
                        assert nbt != null;
                        for (String key : nbt.getKeys()) {
                            wand.getNbt().put(key, nbt.get(key));
                        }
                    }
                });
            }));

    public static final boolean PORTUS_ENTRY_COORDINATES = ServerPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "portus_entry_coordinates"), (((server, player, handler, buf, responseSender) -> {
                int[] coords = buf.readIntArray();
                if (coords.length != 3) {
                    return;
                }
                server.execute(() -> ((Portus) ModItems.PORTUS).setPortkey(new BlockPos(coords[0], coords[1], coords[2]), player));
            })));

    public static void registerNetworkPackets() {
        Main.LOGGER.info("Registering NetworkPackets for " + Main.MOD_ID);
    }
}
