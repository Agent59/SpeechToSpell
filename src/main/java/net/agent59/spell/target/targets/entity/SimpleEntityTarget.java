package net.agent59.spell.target.targets.entity;

import com.mojang.serialization.Codec;
import net.agent59.spell.target.TargetTypes;
import net.agent59.spell.target.targets.Target;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public record SimpleEntityTarget(boolean includeCaster) implements Target.EntityTarget {
    public static final Codec<SimpleEntityTarget> CODEC = Codec.BOOL
            .optionalFieldOf("include_caster", true)
            .xmap(SimpleEntityTarget::new, t -> t.includeCaster).codec();

    @Override
    public TargetTypes.TargetType<? extends Target> getType() {
        return TargetTypes.ENTITY;
    }

    @Override
    public Predicate<Entity> getEntityPredicate(LivingEntity caster) {
        return this.includeCaster ? e -> true : e -> e != caster;
    }
}
