package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Stupefy extends Item implements SpellInterface {
    private static final String NAME = "Stupefy";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 10;
    private static final float RAY_SPEED = 0.5F;
    private static final float DAMAGE = 2F;

    public Stupefy(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public int getCastingCooldown() {
        return CASTING_COOLDOWN;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        World world = player.getWorld();
        RayEntity stupefyRay = new RayEntity(ModEntities.STUPEFY_RAY, world) {
            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                boolean bl = entity.damage(DamageSource.MAGIC, DAMAGE);
                Direction direction = this.getMovementDirection().getOpposite();
                entity.addVelocity(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
            }
        };
        stupefyRay.setVelocity(player, RAY_SPEED, 0);
        stupefyRay.setRotationAndSpawnAndOwner(player);

        world.spawnEntity(stupefyRay);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
