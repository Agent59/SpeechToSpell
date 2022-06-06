package net.agent59.stp.mixin;

import com.mojang.authlib.GameProfile;
import net.agent59.stp.item.custom.WandItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// prevents the slowing down effect when using a wand

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", opcode = Opcodes.GETFIELD))
    private boolean preventSlowingDown(ClientPlayerEntity clientPlayerEntity) {
        if (clientPlayerEntity.getActiveItem().getItem() instanceof WandItem) {
            return false;
        } else {
            return clientPlayerEntity.isUsingItem();
        }
    }
}
