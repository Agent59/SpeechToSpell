package net.agent59.stp.entity.custom;


import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayEntity extends ShulkerBulletEntity {

    public RayEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
        super.setNoGravity(true);
    }

    public void setVelocity(PlayerEntity player, float speed, float divergence) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        float roll = player.getRoll();

        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float g = -MathHelper.sin((pitch + roll) * 0.017453292F);
        float h = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        this.setVelocity(f, g, h, speed, divergence);
    }

    public void refreshPositionAndAngles(PlayerEntity player) {
        Vec3d eyePos = player.getEyePos();
        this.refreshPositionAndAngles(eyePos.getX(), eyePos.getY(), eyePos.getZ(), player.getYaw(), player.getPitch());
    }

    public void tick() {
        super.tick();
        ProjectileUtil.setRotationFromVelocity(this, 0.2F);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
    }

    @Override
    protected void initDataTracker() {
    }
}
