package net.agent59;

import net.agent59.gui.ModScreens;
import net.agent59.render.ModEntityRenderer;
import net.agent59.render.block.ModBlockEntityRenderer;
import net.agent59.speech.Sphinx4Conf;
import net.agent59.util.FileHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomKeybindings.registerCustomKeybindings();
        ModEntityRenderer.registerModEntityRenderers();
        ModBlockEntityRenderer.registerModBlockEntityRenderers();
        ClientNetwork.registerNetworkPackets();
        ModScreens.registerModScreens();

        // create a folder with resources and configs in the mod folder
        FileHandler.createFolderIfNonexistent(Main.MOD_NAME, FileHandler.MODS_DIRECTORY);
        FileHandler.createFolderIfNonexistent("resources", FileHandler.THIS_MOD_DIRECTORY);
        Sphinx4Conf.createSpeechResources();
    }
}
