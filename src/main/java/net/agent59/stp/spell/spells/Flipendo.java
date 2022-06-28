package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Flipendo extends Item implements SpellInterface {
    private static final String NAME = "Flipendo";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.JINX;
    private static final float RAY_SPEED = 2F;
    private static final float KNOCKBACK_STRENGTH = 2.0F;
    private static final int MAX_LIFETIME = 1000;

    public Flipendo(Settings settings) {
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
    public SpellType getSpellType() {
        return SPELLTYPE;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        World world = player.getWorld();
        RayEntity ray = new RayEntity(ModEntities.FLIPENDO_RAY, world) {
            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
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
