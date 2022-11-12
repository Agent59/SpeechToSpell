package net.agent59.stp.screen;

import net.agent59.stp.Main;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModScreenHandlers {
    public static ScreenHandlerType<WandMakerScreenHandler> WANDMAKER_SCREEN_HANDLER =
            registerScreenHandler("wandmaker_screen_handler", ((syncId, inventory) -> new WandMakerScreenHandler(syncId, inventory, ScreenHandlerContext.EMPTY)));

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerScreenHandler(String name, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registry.SCREEN_HANDLER, new Identifier(Main.MOD_ID, name), new ScreenHandlerType<>(factory));
    }

    public static void registerModScreenHandlers() {
        Main.LOGGER.info("Registering Mod Screen Handlers for " + Main.MOD_ID);
    }
}
