package net.agent59.stp.entity.custom;


import net.agent59.stp.ModParticles;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.EntityType;
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
    private int maxLifetime = 1000;
    private SpellType spellType;
    private String spellName;

    // TODO add second constructor (reduce usage of setters after creating the RayEntity)
    public RayEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
        super.setNoGravity(true);
    }

    public void setVelocity(PlayerEntity player, float speed, float divergence) {
        float yaw = player.getYaw();
        float pitch = player.getPitch();
        float roll = player.getRoll();

        float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = -MathHelper.sin((pitch + roll) * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);

        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.random.nextGaussian() * 0.007499999832361937 * (double)divergence, this.random.nextGaussian() * 0.007499999832361937 * (double)divergence, this.random.nextGaussian() * 0.007499999832361937 * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            this.prevPitch = this.getPitch();
            this.prevYaw = this.getYaw();
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.tick2();

        ++this.age;
        if (this.age >= this.maxLifetime) {
            this.kill();
        }
    }

    public void tick2() {
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

    public int getMaxLifetime() {
        return this.maxLifetime;
    }

    public void setMaxLifetime(int maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    public SpellType getSpellType() {
        return this.spellType;
    }

    public void setSpellType(SpellType spellType) {
        this.spellType = spellType;
    }

    public String getSpellName() {
        return this.spellName;
    }

    public void setSpellName(String name) {
        this.spellName = name;
    }
}
