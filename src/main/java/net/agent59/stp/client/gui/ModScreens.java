package net.agent59.stp.client.gui;

import net.agent59.stp.Main;
import net.agent59.stp.screen.ModScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(value=EnvType.CLIENT)
public class ModScreens {
    public static void registerModScreens() {
        HandledScreens.register(ModScreenHandlers.WANDMAKER_SCREEN_HANDLER, WandMakerScreen::new);

        Main.LOGGER.info("Registering Mod Screens for " + Main.MOD_ID);
    }
}
