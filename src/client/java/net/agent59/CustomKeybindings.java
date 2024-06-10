package net.agent59;

import net.agent59.gui.WandScreen;
import net.agent59.gui.cottonguis.WandSettingsGui;
import net.agent59.item.custom.WandItem;
import net.agent59.util.UpdateNbt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CustomKeybindings {
    public static final KeyBinding WANDSETTINGS = registerKeybinding("wand_settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N);

    public static final KeyBinding CYCLESPELLHOTBAR = registerKeybinding("cycle_spell_hotbar", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R);

    public static final KeyBinding EXECUTESPELLFROMSPELLHOTBAR = registerKeybinding("execute_spell_from_spell_hotbar", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G);

    private static KeyBinding registerKeybinding(String name, InputUtil.Type inputType, int keycode) {

        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key."+ Main.MOD_ID +"."+ name , inputType, keycode,
                "category."+ Main.MOD_ID + "_keys"));
    }

    public static void registerCustomKeybindings(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (WANDSETTINGS.wasPressed()) {
                ItemStack wand;
                // if the player holds a wand
                assert client.player != null;
                if (((wand = client.player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = client.player.getOffHandStack()).getItem() instanceof WandItem)) {
                    MinecraftClient.getInstance().setScreen(new WandScreen(new WandSettingsGui(wand)));
                }
            }

            while (CYCLESPELLHOTBAR.wasPressed()) {
                ItemStack wand;
                // if the player holds a wand
                assert client.player != null;
                if (((wand = client.player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = client.player.getOffHandStack()).getItem() instanceof WandItem)) {
                    assert wand.getNbt() != null;
                    int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
                    if (selectedSlot == 5) {
                        UpdateNbt.updateWandNbtFromClient(".spellHotbarSelectedSlot", null, 1);
                    } else {
                        UpdateNbt.updateWandNbtFromClient(".spellHotbarSelectedSlot", null, 1 + selectedSlot);
                    }
                }
            }

            while (EXECUTESPELLFROMSPELLHOTBAR.wasPressed()) {
                ItemStack wand;
                // if the player holds a wand
                assert client.player != null;
                if (((wand = client.player.getMainHandStack()).getItem() instanceof WandItem) || ((wand = client.player.getOffHandStack()).getItem() instanceof WandItem)) {

                    assert wand.getNbt() != null;
                    int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
                    String spellName = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + selectedSlot);

                    if (!Objects.equals(spellName, "")) {
                        PlayerEntity player = client.player;

                        //create the packet for the spell to send to the server
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeString(spellName);
                        //send the packaged spell to the server
                        ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "spell"), buf);
                    }
                }
            }
        });

        Main.LOGGER.info("Registering Keybindings for " + Main.MOD_ID);
    }
}
