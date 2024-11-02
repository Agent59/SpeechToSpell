package net.agent59.spell.target.areas;

import net.agent59.spell.target.TargetAreaTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The base interface for all target areas.
 * The classes that implement this interface can all be used to define areas a
 * {@link net.agent59.spell.spells.Spell} may target.
 * <p>Every TargetArea should be registered in the {@link TargetAreaTypes} registry.<br>
 * The {@link TargetAreaTypes#CODEC} can be used in the codecs of spells
 * to allow for the configuration of the target area.
 * <p>TargetAreas need to return their corresponding type from {@link TargetAreaTypes} in {@link #getType()}.
 * <p><b>This is not to be confused with {@link net.agent59.spell.target.targets.Target}.</b>
 * @see TargetAreaTypes
 */
public interface TargetArea {
    /**
     * @return The corresponding type from {@link TargetAreaTypes}.
     */
    TargetAreaTypes.TargetAreaType<? extends TargetArea> getType();

    /**
     * Returns a list of block-positions in the target's area that fit the predicate.<br>
     * If the length of the list exceeds maxBlocks the list is shortened by <u>random</u> selection.
     * @param maxBlocks The maximal length of the returned list.
     * @param needClearSky If set to {@code true}, no block must be over any of the returned block-positions.
     * @param predicate A condition each block position must fulfill.
     * @param referencePos The position which the target area uses as a reference point (e.g. center of a circle).
     * @param caster The entity for which the target area is calculated (e.g. used for raycasts).
     * @param tries The attempts that should be made to find a valid block.
     * @return A list of block-positions in the target's area that fit the predicate.
     */
    List<BlockPos> getBlocks(
            int maxBlocks,
            boolean needClearSky,
            Predicate<BlockPos> predicate,
            BlockPos referencePos,
            Entity caster,
            int tries
    );

    /**
     * Calls the consumer for each block in the target area that fulfills the predicate.
     * @param needClearSky If set to {@code true}, no block must be over any of the block-positions given to the consumer.
     * @param referencePos The position which the target area uses as a reference point (e.g. center of a circle).
     * @param caster The entity for which the target area is calculated (e.g. used for raycasts).
     * @param predicate A condition each block position must fulfill.
     * @param consumer The function that is called for each of the valid block-positions in the area.
     */
    void forAllBlocks(
            boolean needClearSky,
            BlockPos referencePos,
            Entity caster,
            Predicate<BlockPos> predicate,
            Consumer<BlockPos> consumer
    );

    /**
     * Returns a list of entities in the target's area that fit the predicate.<br>
     * If the length of the list exceeds maxEntities the list is shortened by <u>random</u> selection.
     * @param maxEntities The maximal length of the returned list.
     * @param predicate A condition each entity must fulfill.
     * @param referencePos The position which the target area uses as a reference point (e.g. center of a circle).
     * @param caster The entity for which the target area is calculated (e.g. used for raycasts).
     * @param tries The attempts that should be made to find a valid entity.
     * @return A list of entities in the target's area that fit the predicate.
     */
    List<Entity> getEntities(
            int maxEntities,
            Predicate<Entity> predicate,
            Vec3d referencePos,
            Entity caster,
            int tries
    );
}
