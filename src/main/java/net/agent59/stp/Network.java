package net.agent59.stp;

import net.agent59.stp.item.custom.WandItem;
import net.agent59.stp.spell.SpellHandler;
import net.agent59.stp.spell.SpellInterface;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class Network {

    // executes the spell, the client has sent as a String, on the server
    public static final boolean SPELL = ServerPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "spell"), ((server, player, handler, buf, responseSender) -> {
                String spellString = buf.readString();
                SpellHandler.executeSpellIfAllowed(player, spellString);
            }));

    public static final boolean UPDATEWANDNBT = ServerPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "update_wand_nbt"), ((server, player, handler, buf, responseSender) ->{

                ItemStack wand;
                if (((wand = player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) {
                    NbtCompound nbt = buf.readNbt();

                    assert nbt != null;
                    for (String key : nbt.getKeys()) {
                        assert wand.getNbt() != null;
                        wand.getNbt().put(key, nbt.get(key));
                    }
                }
            }));

    public static void registerNetworkPackets() {
        System.out.println("Registering NetworkPackets for " + Main.MOD_ID);
    }
}