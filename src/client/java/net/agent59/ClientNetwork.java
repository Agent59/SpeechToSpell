package net.agent59;

import net.agent59.gui.WandScreen;
import net.agent59.gui.cottonguis.PortusGui;
import net.agent59.item.custom.GuideBookItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.Identifier;

public class ClientNetwork {

    public static final boolean PORTUS_SCREEN = ClientPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "portus_screen"), ((client, handler, buf, responseSender) -> {
                client.execute(() -> MinecraftClient.getInstance().setScreen(new WandScreen(new PortusGui(client.player))));
            }));

    public static final boolean BOOK_SCREEN = ClientPlayNetworking.registerGlobalReceiver(
            new Identifier(Main.MOD_ID, "book_screen"), ((client, handler, buf, responseSender) -> {
                client.execute(() -> {
                    MinecraftClient clientInstance = MinecraftClient.getInstance();
                    ItemStack itemStack = clientInstance.player.getStackInHand(clientInstance.player.getActiveHand());
                    if (itemStack.getItem() instanceof WrittenBookItem || itemStack.getItem() instanceof GuideBookItem) {
                        clientInstance.setScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
                    }
                });
            }));

    public static void registerNetworkPackets() {
        Main.LOGGER.info("Registering ClientNetworkPackets for " + Main.MOD_ID);
    }
}
