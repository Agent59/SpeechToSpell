package net.agent59.stp.mixin;

import net.agent59.stp.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;canUse(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private boolean canUse(ScreenHandler screenHandler, PlayerEntity playerEntity) {
        if (playerEntity.getItemCooldownManager().isCoolingDown(ModItems.CISTEM_APERIO)) {
            return true;
        }
        return screenHandler.canUse(playerEntity);
    }
}
