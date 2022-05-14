package net.agent59.stp.util;

import net.agent59.stp.Main;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class UpdateNbt {
    public static void updateWandNbtFromClient(String key, String stringValue, int intValue) {
        // either the stringValue has to be null or the intValue has to be 0

        PacketByteBuf buf = PacketByteBufs.create();
        NbtCompound nbt = new NbtCompound();

        if (stringValue != null && intValue == 0) {
            nbt.putString(Main.MOD_ID + key, stringValue);
        } else if (stringValue == null && intValue != 0) {
            nbt.putInt(Main.MOD_ID + key, intValue);
        } else {
            throw new IllegalArgumentException();
        }
        buf.writeNbt(nbt);
        ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "update_wand_nbt"), buf);
    }
}
