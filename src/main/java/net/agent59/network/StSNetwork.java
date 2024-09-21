package net.agent59.network;

import net.agent59.StSMain;
import net.agent59.cardinal_component.player_magic_comp.ServerPlayerMagicComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class StSNetwork {
    public static final Identifier PLAYER_MAGIC_COMP_C2S_MESSAGE_ID = StSMain.id("player_magic_component_c2s_message");
    public static final boolean PLAYER_MAGIC_COMP_C2S_MESSAGE = ServerPlayNetworking.registerGlobalReceiver(
            PLAYER_MAGIC_COMP_C2S_MESSAGE_ID,
            (server, player, handler, buf, responseSender) -> {
                // The buffer needs to be copied, because the original is freed when moving it to the main thread.
                PacketByteBuf bufCopy = PacketByteBufs.copy(buf);
                server.execute(() -> ServerPlayerMagicComponent.getInstance(player).handleC2SMessage(bufCopy));
            }
    );

    public static void registerNetworkPackets() {
        StSMain.LOGGER.info("Registering NetworkPackets for " + StSMain.MOD_NAME);
    }
}
