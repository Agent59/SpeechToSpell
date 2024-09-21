package net.agent59;

import net.agent59.gui.ModScreens;
import net.agent59.render.ModEntityRenderer;
import net.agent59.render.block.ModBlockEntityRenderer;
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
        StSClientNetwork.registerNetworkPackets();
        ModScreens.registerModScreens();
    }
}
