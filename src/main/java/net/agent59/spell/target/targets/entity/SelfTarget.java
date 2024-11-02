package net.agent59.spell.target.targets.entity;

import com.mojang.serialization.Codec;
import net.agent59.spell.target.TargetTypes;
import net.agent59.spell.target.targets.Target;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

/**
 * A target, where the targeted entity or block position must always be the caster, or the casters position.
 * <p>It is advisable, that if the target type is set to this type and the spell makes the target area configurable,
 * to default to the {@link net.agent59.spell.target.areas.SelfArea}.
 * @see net.agent59.spell.target.areas.SelfArea
 */
public class SelfTarget implements Target.EntityTarget {
    public static final Codec<SelfTarget> CODEC = Codec.unit(new SelfTarget());

    @Override
    public TargetTypes.TargetType<? extends Target> getType() {
        return TargetTypes.SELF;
    }

    @Override
    public Predicate<Entity> getEntityPredicate(LivingEntity caster) {
        return e -> e == caster;
    }
}
