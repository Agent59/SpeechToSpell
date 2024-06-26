package net.agent59.spell.spells;

import net.agent59.entity.ModEntities;
import net.agent59.entity.custom.RayEntity;
import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Stupefy extends Item implements SpellInterface {
    private static final String NAME = "Stupefy";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CHARM;
    private static final float RAY_SPEED = 2F;
    private static final float DAMAGE = 2F;
    private static final float KNOCKBACK_STRENGTH = 1.0F;
    private static final int MAX_LIFETIME = 1000;

    public Stupefy(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Shoots a magical ray that gives little damage and knockback.";
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
    public SpellType getSpellType() {
        return SPELLTYPE;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        World world = player.getWorld();
        RayEntity ray = new RayEntity(ModEntities.STUPEFY_RAY, world) {
            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                entity.damage(world.getDamageSources().magic(), DAMAGE);
                if (entity instanceof LivingEntity) {
                    ((LivingEntity)entity).takeKnockback(KNOCKBACK_STRENGTH, MathHelper.sin((this.getYaw() + 180) * ((float)Math.PI / 180)), -MathHelper.cos((this.getYaw() + 180) * ((float)Math.PI / 180)));
                } else {
                    entity.addVelocity(-MathHelper.sin((this.getYaw() + 180) * ((float) Math.PI / 180)) * KNOCKBACK_STRENGTH, 0.1, MathHelper.cos((this.getYaw() + 180) * ((float) Math.PI / 180)) * KNOCKBACK_STRENGTH);
                }
                this.remove(RemovalReason.DISCARDED);
            }
        };
        ray.configureEntity(player, MAX_LIFETIME, SPELLTYPE, NAME);
        ray.updatePositionAndAngles(player);
        ray.setVelocity(player, RAY_SPEED, 0);
        world.spawnEntity(ray);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
