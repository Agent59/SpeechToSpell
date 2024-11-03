package net.agent59.spell.target.areas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.agent59.spell.target.TargetAreaTypes;
import net.agent59.spell.util.RaycastUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Returns the focused entity or block.
 * @param range The maximal range a target may be away.
 * @param clearance The minimal distance a target must be away from the reference position
 * @param fluidsBlock Whether fluids block the raycast,
 *                    meaning that for a block target the fluids position is returned.
 * @param entitiesBlock Whether the entity blocks the raycast for a block,
 *                      resulting in the entities position being returned.
 * @see RaycastUtil
 */
public record FocusedArea(int range, int clearance, boolean fluidsBlock, boolean entitiesBlock) implements TargetArea {

    public static final Codec<FocusedArea> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.NONNEGATIVE_INT.fieldOf("radius").forGetter(area -> area.range),
            Codecs.NONNEGATIVE_INT.optionalFieldOf("clearance", 0).forGetter(area -> area.clearance),
            Codec.BOOL.optionalFieldOf("fluids_block", true).forGetter(area -> area.fluidsBlock),
            Codec.BOOL.optionalFieldOf("entities_block", true).forGetter(area -> area.entitiesBlock)
    ).apply(instance, FocusedArea::new));

    @Override
    public TargetAreaTypes.TargetAreaType<? extends TargetArea> getType() {
        return TargetAreaTypes.FOCUSED;
    }

    @Override
    public List<BlockPos> getBlocks(int maxBlocks, boolean needClearSky, Predicate<BlockPos> predicate, BlockPos referencePos, Entity caster, int tries) {
        HitResult hit = this.entitiesBlock ?
                RaycastUtil.raycast(caster, this.range, this.fluidsBlock) :
                RaycastUtil.raycastBlock(caster, this.range, this.fluidsBlock);

        BlockPos pos = null;
        switch (hit.getType()) {
            case MISS -> { return List.of(); }
            case BLOCK -> pos = ((BlockHitResult) hit).getBlockPos();
            case ENTITY -> pos = ((EntityHitResult) hit).getEntity().getBlockPos();
        }
        boolean invalidSky = needClearSky && !caster.getWorld().isSkyVisible(pos.up());
        return (invalidSky || pos.isWithinDistance(referencePos, this.clearance) || !predicate.test(pos)) ?
                List.of() : List.of(pos);
    }

    @Override
    public void forAllBlocks(boolean needClearSky, BlockPos referencePos, Entity caster, Predicate<BlockPos> predicate, Consumer<BlockPos> consumer) {
        this.getBlocks(Integer.MAX_VALUE, needClearSky, predicate, referencePos, caster, 1).forEach(consumer);
    }

    @Override
    public List<Entity> getEntities(int maxEntities, Predicate<Entity> predicate, Vec3d referencePos, Entity caster, int tries) {
        EntityHitResult hit = RaycastUtil.raycastEntity(caster, this.range, predicate);
        if (hit == null) return List.of();
        Entity entity = hit.getEntity();
        return (!entity.getPos().isInRange(referencePos, this.clearance) || predicate.test(entity)) ?
                List.of(entity) : List.of();
    }
}
