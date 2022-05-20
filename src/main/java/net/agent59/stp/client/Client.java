package net.agent59.stp.client;

import net.agent59.stp.Main;
import net.agent59.stp.client.render.ModEntityRenderer;
import net.agent59.stp.speech.Sphinx4Conf;
import net.agent59.stp.util.FileHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomKeybindings.registerCustomKeybindings();
        ModEntityRenderer.registerModEntityRenderers();

        // create a folder with resources and configs in the mod folder
        FileHandler.createFolderIfNonexistent(Main.MOD_NAME, FileHandler.MODS_DIRECTORY);
        FileHandler.createFolderIfNonexistent("resources", FileHandler.THIS_MOD_DIRECTORY);
        Sphinx4Conf.createSpeechResources();
    }
}
