package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
        RayEntity stupefyRay = new RayEntity(ModEntities.STUPEFY_RAY, world) {
            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                entity.damage(DamageSource.MAGIC, DAMAGE);
                if (entity instanceof LivingEntity) {
                    ((LivingEntity)entity).takeKnockback(KNOCKBACK_STRENGTH, MathHelper.sin((this.getYaw() + 180) * ((float)Math.PI / 180)), -MathHelper.cos((this.getYaw() + 180) * ((float)Math.PI / 180)));
                } else {
                    entity.addVelocity(-MathHelper.sin((this.getYaw() + 180) * ((float) Math.PI / 180)) * KNOCKBACK_STRENGTH, 0.1, MathHelper.cos((this.getYaw() + 180) * ((float) Math.PI / 180)) * KNOCKBACK_STRENGTH);
                }
                this.remove(RemovalReason.DISCARDED);
            }
        };
        stupefyRay.setOwner(player);
        stupefyRay.setMaxLifetime(MAX_LIFETIME);
        stupefyRay.setSpellType(SPELLTYPE);
        stupefyRay.setSpellName(NAME);
        stupefyRay.updatePositionAndAngles(player.getX(), player.getEyeY(), player.getZ(), player.getYaw() + 180, player.getPitch() * -1);
        stupefyRay.setVelocity(player, RAY_SPEED, 0);
        world.spawnEntity(stupefyRay);

        //set cooldown
        player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
    }
}
