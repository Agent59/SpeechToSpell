package net.agent59.spell.target.areas;

import com.mojang.serialization.Codec;
import net.agent59.spell.target.TargetAreaTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An area that always returns the caster as an entity or the reference position as the block position.
 * <p>It is advisable to default to this area if a spells target is
 * {@link net.agent59.spell.target.targets.entity.SelfTarget},
 * because this is much faster than other areas having to check every entity or block in their area,
 * while the only ones that could fulfill the target predicate are the caster and the reference position.
 * @see net.agent59.spell.target.targets.entity.SelfTarget
 */
public class SelfArea implements TargetArea {
    public static final Codec<SelfArea> CODEC = Codec.unit(SelfArea::new);

    @Override
    public TargetAreaTypes.TargetAreaType<? extends TargetArea> getType() {
        return TargetAreaTypes.SELF;
    }

    @Override
    public List<BlockPos> getBlocks(int maxBlocks, boolean needClearSky, Predicate<BlockPos> predicate, BlockPos referencePos, Entity caster, int tries) {
        return predicate.test(referencePos) ? List.of(referencePos) : List.of();
    }

    @Override
    public void forAllBlocks(boolean needClearSky, BlockPos referencePos, Entity caster, Predicate<BlockPos> predicate, Consumer<BlockPos> consumer) {
        if (predicate.test(referencePos)) consumer.accept(referencePos);
    }

    @Override
    public List<Entity> getEntities(int maxEntities, Predicate<Entity> predicate, Vec3d referencePos, Entity caster, int tries) {
        return predicate.test(caster) ? List.of(caster) : List.of();
    }
}
