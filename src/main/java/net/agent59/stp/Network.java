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
                System.out.println("\n HERE: " + spellString);

                ItemStack wand;
                if (((wand = player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) {

                    // executes the spell if the player is holding rightclick with a wand or has the spell selected in his Spell-hotbar
                    // and if the wand has no cooldown
                    assert wand.getNbt() != null;
                    int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
                    String selectedHotbarSpellName = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + selectedSlot);

                    // get the spell by the string
                    SpellInterface spell = SpellHandler.getSpellNameHashmap().get(spellString);
                    assert spell != null;

                    // check if cooling down
                    boolean coolingDown = player.getItemCooldownManager().isCoolingDown(spell.asItem());

                    if ((player.getActiveItem() == wand || spellString.equals(selectedHotbarSpellName)) && !coolingDown) {
                        spell.execute(player);
                    }
                }
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
