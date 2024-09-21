package net.agent59.gui;

import net.agent59.StSMain;
import net.agent59.screen.StSScreenHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(value=EnvType.CLIENT)
public class StSScreens {
    public static void registerModScreens() {
        HandledScreens.register(StSScreenHandlers.WANDMAKER_SCREEN_HANDLER, WandMakerScreen::new);

        StSMain.LOGGER.info("Registering Mod Screens for " + StSMain.MOD_NAME);
    }
}
