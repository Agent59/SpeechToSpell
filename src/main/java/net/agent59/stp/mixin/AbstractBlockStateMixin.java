package net.agent59.stp.mixin;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.PortkeyEntity;
import net.agent59.stp.item.ModItems;
import net.agent59.stp.spell.spells.Portus;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "onUse", at = @At("HEAD"))
    private void portkeyCheck(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient()) {
            Box box1 = new Box(hit.getBlockPos());
            List<Entity> entities1 = world.getOtherEntities(null, box1);

            for (Entity entity1 : entities1) {
                if (entity1.getType() == ModEntities.PORTKEY) {
                    BlockPos targetBlockPos = ((PortkeyEntity) entity1).getTargetBlockPos();

                    Box box2 = player.getBoundingBox().expand(((Portus) ModItems.PORTUS).getRange());
                    List<Entity> entities2 = world.getOtherEntities(null, box2);

                    for (Entity entity2 : entities2) {
                        if (entity2 instanceof PlayerEntity)
                            entity2.teleport(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ());
                    }
                    entity1.kill();
                }
            }
        }
    }
}
