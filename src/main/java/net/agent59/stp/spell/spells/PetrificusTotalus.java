package net.agent59.stp.spell.spells;

import net.agent59.stp.entity.ModEntities;
import net.agent59.stp.entity.custom.RayEntity;
import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class PetrificusTotalus extends Item implements SpellInterface {
    private static final String NAME = "Petrificus Totalus";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CURSE;
    private static final float RAY_SPEED = 2F;
    private static final int MAX_LIFETIME = 500;
    private static final int DURATION = 500;

    public PetrificusTotalus(Item.Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Shoots a magical ray that gives maximum slowness to an entity.";
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
        RayEntity ray = new RayEntity(ModEntities.PETRIFICUS_TOTALUS_RAY, world) {
            @Override
            protected void onEntityHit(EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof LivingEntity) {
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, DURATION, 255), player);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, DURATION, 255), player);
                    ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, DURATION, 255), player);
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
