package net.agent59.stp.entity.custom;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayEntity extends ShulkerBulletEntity {

    public RayEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setVelocity(PlayerEntity player, float speed, float divergence) {
        Vec3d eyePos = player.getEyePos();

        super.setVelocity(eyePos.getX(), eyePos.getY(), eyePos.getZ(), speed, divergence);
    }

    public void refreshPositionAndAngles(PlayerEntity player) {
        Vec3d eyePos = player.getEyePos();
        this.refreshPositionAndAngles(eyePos.getX(), eyePos.getY(), eyePos.getZ(), player.getYaw(), player.getPitch());
    }


    @Override
    protected void initDataTracker() {
    }
}
