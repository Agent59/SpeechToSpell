package net.agent59.spell.util;

import com.mojang.serialization.Codec;
import net.agent59.codecs.CodecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

/**
 * An enum that aids in creating spells that can be configured for different targets, which must be position based.
 */
public enum PositionTarget {
    BLOCK,
    ENTITY,
    LIVING_ENTITY,
    HOSTILE,
    PLAYER,
    HOSTILE_OR_PLAYER,
    SELF;

    public static final Codec<PositionTarget> CODEC = CodecUtil.getEnumCodec(PositionTarget.class);

    /**
     * Returns the position of a random valid target or {@code null} if there is none, based on the type of this enum.
     * @param caster The entity that is casting the spell.
     * @param except An entity that can't be a targeted and will get the given clearance
     *               (most times the caster, but this is ignored if {@link #SELF}).
     * @param clearance the minimum distance that must be between the except entity and the random target.
     * @param range The range in which the target must be.
     * @param posPredicate A condition the target must fulfill.
     * @param tries The attempts to find a valid target block (only relevant for {@link #BLOCK}).
     * @return The position of a random valid target
     * or {@code null} if there is no valid target or the posPredicate fails.
     */
    public @Nullable BlockPos getRandomPos(
            LivingEntity caster, @Nullable Entity except, int clearance, int range,
            Predicate<BlockPos> posPredicate, int tries
    ) {
        if (this == SELF) {
            BlockPos pos = caster.getBlockPos();
            if (posPredicate.test(pos)) return pos;
            return null;
        }

        Box box = caster.getBoundingBox().expand(range);
        World world = caster.getWorld();
        Random random = new Random();

        // Returns the topmost block of a random column of blocks,
        // whereas the block is not the one the player is standing on.
        // If after the given tries no block is found, null is returned.
        if (this == BLOCK) {
            for (int i = 0; i < tries; i++) {
                BlockPos randomTopPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING,
                        new BlockPos(
                                random.nextInt((int) box.minX, (int) box.maxX + 1), 0,
                                random.nextInt((int) box.minZ, (int) box.maxZ + 1)
                        )
                ); // If there is not enough clearance or the pred fails, we try again.
                if (except != null && PositionTarget.noClearance(except.getBlockPos(), randomTopPos, clearance)
                        || !posPredicate.test(randomTopPos)) continue;
                return randomTopPos;
            }
        }

        // When this is reached, the target must be some sort of entity.
        Predicate<Entity> pred = this.createEntityPredicate(posPredicate);
        if (except != null) { // Adds the clearance to the entityPredicate.
            pred = pred.and(e -> !PositionTarget.noClearance(except.getBlockPos(), e.getBlockPos(), clearance));
        }
        List<Entity> targets = world.getOtherEntities(except, box, pred);
        if (targets.isEmpty()) return null;

        Entity randomTarget = targets.get(random.nextInt(targets.size()));
        return randomTarget.getBlockPos();
    }

    /**
     * Returns the position of what the caster looks at or {@code null} if it doesn't correspond to this enum's type.
     * @param caster The entity that is casting the spell.
     * @param range The range in which the target must be.
     * @param predicate A condition the target must fulfill.
     * @param fluidsBlock Whether the raycast is blocked by fluids.
     * @return The position the raycast hits
     * or {@code null} if the posPredicate fails or this enum's type doesn't fit the hit.
     */
    public @Nullable BlockPos getFocusedPos(
            LivingEntity caster, int range, Predicate<BlockPos> predicate, boolean fluidsBlock
    ) {
        if (this == SELF) {
            BlockPos pos = caster.getBlockPos();
            if (predicate.test(pos)) return pos;
            return null;
        }

        HitResult hitResult = RaycastUtil.raycast(caster, range, fluidsBlock);
        switch (hitResult.getType()) {
            case HitResult.Type.MISS -> { return null; }
            case HitResult.Type.BLOCK -> {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                if (this == BLOCK && predicate.test(pos)) return pos;
                return null;
            }
            case HitResult.Type.ENTITY -> {
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                // If the entity is not a valid target.
                if (!this.createEntityPredicate(predicate).test(entity)) return null;
                return entity.getBlockPos();
            }
        }
        // This should not be reached (all types of HitResult are covered), but should also not pose a problem.
        return null;
    }

    /**
     * Helps to ensure that the target entity corresponds to this enum's type.
     */
    private Predicate<Entity> createEntityPredicate(Predicate<BlockPos> posPredicate) {
        Predicate<Entity> pred = entity -> posPredicate.test(entity.getBlockPos());
        switch (this) {
            case LIVING_ENTITY -> pred = pred.and(e -> e instanceof LivingEntity);
            case HOSTILE -> pred = pred.and(e -> e instanceof HostileEntity);
            case PLAYER -> pred = pred.and(e -> e instanceof PlayerEntity);
            case HOSTILE_OR_PLAYER -> pred = pred.and(e -> e instanceof PlayerEntity || e instanceof HostileEntity);
        }
        return pred;
    }

    /**
     * Helps checking whether there is enough distance between a specified target and clearance position.
     * @param clearancePos The position that must be within distance of the target position.
     * @param targetPos The position that is targeted.
     * @param clearance The distance that must b
     * @return {@code true} if there is not enough distance between the target and the clearance position.
     */
    public static boolean noClearance(BlockPos clearancePos, BlockPos targetPos, int clearance) {
        return targetPos.isWithinDistance(clearancePos, clearance);
    }
}
