package net.agent59.spell.target.targets;

import net.agent59.spell.target.TargetTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

/**
 * The base interface for all targets, but <u>this interface should not be implemented directly,<br>
 * implement</u> {@link BlockTarget} <u>or</u> {@link EntityTarget} <u>instead.</u>
 * <p>The classes that implement this interface can all be used to define a target,
 * which {@link net.agent59.spell.spells.Spell}s may make configurable.
 * <p>Every target should be registered in the {@link TargetTypes} registry.<br>
 * The {@link TargetTypes#CODEC} can be used in the codecs of spells
 * to allow for the configuration of the target.
 * <p>Targets need to return their corresponding type from {@link TargetTypes} in {@link #getType()}.
 * <p><b>This is not to be confused with {@link net.agent59.spell.target.areas.TargetArea}.</b>
 * @see TargetTypes
 */
public interface Target {
    /**
     * @return The corresponding type from {@link TargetTypes}.
     */
    TargetTypes.TargetType<? extends Target> getType();

    /**
     * Returns a condition the {@link HitResult} must fulfill, for a target, that the hit represents, to be valid.<br>
     * <p><u>The supplied</u> {@link HitResult} <u>should never be a</u> {@link HitResult.Type#MISS}.
     * @param caster The entity that is casting the spell.
     * @return A condition the {@link HitResult} must fulfill, for a target, that the hit represents, to be valid.
     */
    Predicate<HitResult> getHitPredicate(LivingEntity caster);

    /**
     * The base interface for all block-targets.
     */
    interface BlockTarget extends Target {
        Predicate<BlockPos> getBlockPredicate(LivingEntity caster);

        @Override
        default Predicate<HitResult> getHitPredicate(LivingEntity caster) {
            return hit -> this.getBlockPredicate(caster)
                    .test(new BlockPos((int) hit.getPos().x, (int) hit.getPos().y, (int) hit.getPos().z));
        }
    }

    /**
     * The base interface for all entity-targets.
     * <p>Some spells, will use the position of the entity for targeting.
     */
    interface EntityTarget extends Target {
        Predicate<Entity> getEntityPredicate(LivingEntity caster);

        @Override
        default Predicate<HitResult> getHitPredicate(LivingEntity caster) {
            return hit -> hit.getType() != HitResult.Type.ENTITY
                    && this.getEntityPredicate(caster).test(((EntityHitResult) hit).getEntity());
        }
    }
}
