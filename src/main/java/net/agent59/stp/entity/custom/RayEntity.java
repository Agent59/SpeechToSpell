package net.agent59.stp.entity.custom;


import net.agent59.stp.ModParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayEntity extends ExplosiveProjectileEntity {

    private boolean burning = false;
    private ParticleEffect particleType = ModParticles.EMPTY_PARTICLE;
    private float drag = 1; // set to 1 for no drag
    private boolean collides = false;

    public RayEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
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

    public void setRotationAndSpawnAndOwner(LivingEntity entity) {
        this.setOwner(entity);

        float yaw = entity.getYaw();
        //float yaw2 = (yaw >= ) ? yaw + 180 : yaw
        float yaw3 = (0 - yaw);
        float pitch = entity.getPitch();

        this.setRotation(yaw3, pitch);
        this.setPosition(entity.getPos());

    }

    @Override
    public void tick() {
        //super.tick();
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected boolean isBurning() {
        return this.burning;
    }

    public void setBurning(boolean bl) {
        this.burning = bl;
    }

    @Override
    protected ParticleEffect getParticleType() {
        return this.particleType;
    }

    public void setParticeType(ParticleEffect particeType) {
        this.particleType = particeType;
    }

    @Override
    protected float getDrag() {
        return this.drag;
    }

    public void setDrag(float drag) {
        this.drag = drag;
    }

    @Override
    public boolean collides() {
        return this.collides;
    }

    public void setCollides(boolean collides) {
        this.collides = collides;
    }
}
