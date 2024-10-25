package net.agent59;

import net.agent59.gui.WandScreen;
import net.agent59.gui.cottonguis.PortusGui;
import net.agent59.item.custom.GuideBookItem;
import net.agent59.spell.SpellManager;
import net.agent59.spell_school.SpellSchoolManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class StSClientNetwork {

    public static final boolean PORTUS_SCREEN = ClientPlayNetworking.registerGlobalReceiver(
            StSMain.id("portus_screen"), ((client, handler, buf, responseSender) -> {
                client.execute(() -> MinecraftClient.getInstance().setScreen(new WandScreen(new PortusGui(client.player))));
            }));

    public static final boolean BOOK_SCREEN = ClientPlayNetworking.registerGlobalReceiver(
            StSMain.id("book_screen"), ((client, handler, buf, responseSender) -> {
                client.execute(() -> {
                    MinecraftClient clientInstance = MinecraftClient.getInstance();
                    ItemStack itemStack = clientInstance.player.getStackInHand(clientInstance.player.getActiveHand());
                    if (itemStack.getItem() instanceof WrittenBookItem || itemStack.getItem() instanceof GuideBookItem) {
                        clientInstance.setScreen(new BookScreen(new BookScreen.WrittenBookContents(itemStack)));
                    }
                });
            }));

    /**
     * @see SpellSchoolManager#syncToClient(ServerPlayerEntity)
     */
    public static final boolean SPELL_SCHOOL_MANAGER_SYNC = ClientPlayNetworking.registerGlobalReceiver(
            SpellSchoolManager.SYNC_CHANNEL_NAME, (client, handler, buf, responseSender) -> {
                // The buffer needs to be copied, because the original is freed when moving it to the main thread.
                PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
                client.execute(() -> SpellSchoolManager.syncReceiver(bufCopy));
            }
    );

    /**
     * @see SpellManager#syncToClient(ServerPlayerEntity)
     */
    public static final boolean SPELL_MANAGER_SYNC = ClientPlayNetworking.registerGlobalReceiver(
            SpellManager.SYNC_CHANNEL_NAME,
            (client, handler, buf, responseSender) -> {
                // The buffer needs to be copied, because the original is freed when moving it to the main thread.
                PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
                client.execute(() -> SpellManager.syncReceiver(bufCopy));
            }
    );

    public static void registerNetworkPackets() {
        StSMain.LOGGER.info("Registering ClientNetworkPackets for " + StSMain.MOD_NAME);
    }
}
