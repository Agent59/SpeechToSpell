package net.agent59.stp.client;

import net.agent59.stp.Main;
import net.agent59.stp.client.gui.WandScreen;
import net.agent59.stp.client.gui.cottonguis.PortusGui;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ClientNetwork {

    public static final boolean PORTUS_SCREEN = ClientPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "portus_screen"), ((client, handler, buf, responseSender) -> {
                client.execute(() -> MinecraftClient.getInstance().setScreen(new WandScreen(new PortusGui(client.player))));
            }));

    public static void registerNetworkPackets() {
        Main.LOGGER.info("Registering ClientNetworkPackets for " + Main.MOD_ID);
    }
}
