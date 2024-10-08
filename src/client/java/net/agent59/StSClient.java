package net.agent59;

import net.agent59.gui.MagicHud;
import net.agent59.gui.StSScreens;
import net.agent59.render.StSEntityRenderer;
import net.agent59.render.block.StSBlockEntityRenderer;
import net.agent59.speech.SpeechRecognizer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StSClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CustomKeybindings.registerCustomKeybindings();
        StSEntityRenderer.registerModEntityRenderers();
        StSBlockEntityRenderer.registerModBlockEntityRenderers();
        StSClientNetwork.registerNetworkPackets();
        StSScreens.registerModScreens();
        MagicHud.initialize();
        SpeechRecognizer.initialize();
    }
}
