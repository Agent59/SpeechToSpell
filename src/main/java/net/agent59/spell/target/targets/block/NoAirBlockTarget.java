package net.agent59.spell.target.targets.block;

import com.mojang.serialization.Codec;
import net.agent59.spell.target.TargetTypes;
import net.agent59.spell.target.targets.Target;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;

public class NoAirBlockTarget implements Target.BlockTarget {
    public static final Codec<NoAirBlockTarget> CODEC = Codec.unit(NoAirBlockTarget::new);

    @Override
    public TargetTypes.TargetType<? extends Target> getType() {
        return TargetTypes.NO_AIR_BLOCK;
    }

    @Override
    public Predicate<BlockPos> getBlockPredicate(LivingEntity caster) {
        return pos -> !caster.getWorld().getBlockState(pos).isAir();
    }
}
