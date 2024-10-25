package net.agent59;

import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.gui.StSClientScreen;
import net.agent59.gui.cottonguis.SpellHotbarConfigGui;
import net.agent59.speech.SpeechRecognizer;
import net.agent59.spell.spells.Spell;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class StSKeybindings {
    public static final String KEYBINDING_CATEGORY_BASE = "key.categories." + StSMain.MOD_ID;
    public static final String MAGIC_KEYBINDING_CATEGORY = KEYBINDING_CATEGORY_BASE + ".magic";

    public static final KeyBinding SPELL_HOTBAR_CONFIG = registerKeybinding("spell_hotbar_config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N);
    public static final KeyBinding CYCLE_SPELL_HOTBAR = registerKeybinding("cycle_spell_hotbar", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R);
    public static final KeyBinding CAST_SPELL_FROM_SPELL_HOTBAR = registerKeybinding("execute_spell_from_spell_hotbar", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G);
    public static final KeyBinding LISTEN_FOR_CAST = registerKeybinding("listen_for_cast", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B);

    private static KeyBinding registerKeybinding(String name, InputUtil.Type inputType, int keycode) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + StSMain.MOD_ID + "." + name, inputType, keycode, MAGIC_KEYBINDING_CATEGORY
        ));
    }

    public static void registerCustomKeybindings() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (SPELL_HOTBAR_CONFIG.wasPressed()) {
                assert client.player != null;
                // When the spell hotbar is deactivated (length 0), the spell-hotbar config screen has no use.
                if (ClientPlayerMagicComponent.getInstance(client.player).getSpellHotbar().length == 0) break;

                MinecraftClient.getInstance().setScreen(new StSClientScreen(
                        new SpellHotbarConfigGui(client.player), false, StSKeybindings.SPELL_HOTBAR_CONFIG
                ));
            }

            while (CYCLE_SPELL_HOTBAR.wasPressed()) {
                assert client.player != null;
                ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(client.player);

                // Cycling doesn't make sense if the spellHotbar is of length 0.
                if (playerMagicComp.getSpellHotbar().length == 0) break;

                int selectedSlot = playerMagicComp.getSelectedSpellHotbarSlot();
                if (selectedSlot == playerMagicComp.getSpellHotbar().length - 1) {
                    playerMagicComp.setSelectedSpellHotbarSlot(0);
                } else {
                    playerMagicComp.setSelectedSpellHotbarSlot(selectedSlot + 1);
                }
            }

            while (CAST_SPELL_FROM_SPELL_HOTBAR.wasPressed()) {
                assert client.player != null;
                ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(client.player);
                Spell selectedSpell = playerMagicComp.getSelectedSpellHotbarSpell();

                if (selectedSpell != null && playerMagicComp.canCast(selectedSpell, true)) {
                    playerMagicComp.castSpell(selectedSpell, true);
                }
            }

            // Has to be held while talking and recognizing, otherwise the speech recognition canceled.
            SpeechRecognizer.State state = SpeechRecognizer.getState();
            if (LISTEN_FOR_CAST.isPressed() && state == SpeechRecognizer.State.ACTIVATED) {
                SpeechRecognizer.startRecognition();
            } else if (!LISTEN_FOR_CAST.isPressed() && state == SpeechRecognizer.State.RECOGNIZING) {
                SpeechRecognizer.stopRecognition();
            }
        });

        StSMain.LOGGER.info("Registering Keybindings for " + StSMain.MOD_NAME);
    }
}
