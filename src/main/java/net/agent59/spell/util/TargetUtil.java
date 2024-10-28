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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * An enum that aids in creating spells that can be configured for different targets.
 */
public enum TargetUtil {
    BLOCK,
    ENTITY,
    LIVING_ENTITY,
    HOSTILE,
    PLAYER,
    HOSTILE_OR_PLAYER,
    SELF;

    public static final Codec<TargetUtil> CODEC = CodecUtil.getEnumCodec(TargetUtil.class);
    public static final Codec<TargetUtil> ENTITY_CODEC = CodecUtil.getExcludingEnumCodec(TargetUtil.class, BLOCK);

    /**
     * Returns a random block's position within the range, outside the clearance distance
     * and that adheres to the predicate.
     * @param needsClearSky Whether the block should have no other block above it.
     * @param caster The entity that is casting the spell.
     * @param clearance The minimal space that must be around the caster.
     * @param range The maximal distance between the block and the caster.
     * @param predicate A condition the block must fulfill.
     * @param tries The attempts that are made to find a valid block.
     * @return a random valid {@link BlockPos} or {@code null} if there is no valid one.
     */
    public static @Nullable BlockPos getRandomBlock(
            boolean needsClearSky, LivingEntity caster, int clearance,
            int range, Predicate<BlockPos> predicate, int tries
    ) {
        BlockPos casterPos = caster.getBlockPos();
        for (int i = 0; i < tries; i++) {
            BlockPos randomPos = casterPos.add(ShapeUtil.randomVec3i(clearance, range));
            if (needsClearSky) {
                randomPos = caster.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, randomPos);
                // Ensures the top position is within the range but not the clearance distance.
                if (randomPos.isWithinDistance(casterPos, clearance) || !randomPos.isWithinDistance(casterPos, range)) {
                    continue;
                }
            }
            if (!predicate.test(randomPos)) continue;
            return randomPos;
        }
        return null;
    }

    /**
     * Returns A random entity corresponding to the enums type, within the range, outside the clearance distance
     * and that adheres to the predicate.
     * @param caster The entity that is casting the spell.
     * @param clearance The minimal space that must be around the caster.
     * @param range The maximal distance between the entity and the caster.
     * @param predicate A condition the entity must fulfill.
     * @return A random valid entity or {@code null} if there is no valid one.
     */
    public @Nullable Entity getRandomEntity(
            LivingEntity caster, int clearance, int range, Predicate<Entity> predicate
    ) {
        if (this == SELF) {
            if (predicate.test(caster)) return caster;
            return null;
        }

        // Additionally ensures that the entity is inside the spherical range not just inside the expanded box.
        Predicate<Entity> pred = this.createEntityPredicate().and(e -> caster.isInRange(e, range));
        Entity except = null;
        if (clearance > 0) { // Further ensures adherence to the clearance distance.
            pred = pred.and(e -> !caster.isInRange(e, clearance));
            except = caster;
        }
        pred = pred.and(predicate);

        List<Entity> targets = caster.getWorld().getOtherEntities(except, caster.getBoundingBox().expand(range), pred);
        if (targets.isEmpty()) return null;
        return targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
    }

    /**
     * Returns a random target corresponding to the enums type, within the range, outside the clearance distance
     * and that adheres to the predicate.
     * <p>The {@link HitResult}'s type must be checked before using it with {@link HitResult#getType()}.<br>
     * Depending on the type it can then be cast to a {@link BlockHitResult} or {@link EntityHitResult}.
     * @param caster The entity that is casting the spell.
     * @param clearance The minimum distance that must be between the except entity and the random entity.
     * @param range The range in which the entity must be.
     * @param predicate A condition the entity must fulfill.
     * @param tries If the enums type is a {@link #BLOCK}, the attempts to find a valid one.
     * @return A {@link HitResult} that is either a {@link BlockHitResult}, {@link EntityHitResult}
     * or {@link HitResult.Type#MISS} if there is no valid target (check this with {@link HitResult#getType()}).
     */
    public HitResult getRandomTarget(
            boolean needsClearSky, LivingEntity caster, int clearance, int range,
            Predicate<HitResult> predicate, int tries
    ) {
        if (this == BLOCK) {
            BlockPos pos = TargetUtil.getRandomBlock(
                    needsClearSky, caster, clearance, range,
                    b -> predicate.test(new BlockHitResult(b.toCenterPos(), Direction.UP, b, true)), tries
            );
            if (pos == null) return TargetUtil.createMissed();
            return new BlockHitResult(pos.toCenterPos(), Direction.UP, pos, true);
        }

        // When this is reached, the entity must be some sort of entity.
        Predicate<HitResult> pred;
        if (needsClearSky) { // Ensures there is no block above the entity.
            pred = ((Predicate<HitResult>) h -> caster.getWorld().isSkyVisible(TargetUtil.getBlockPos(h)))
                    .and(predicate);
        } else pred = predicate;

        if (this == SELF) {
            EntityHitResult hitResult = new EntityHitResult(caster);
            if (pred.test(hitResult)) return hitResult;
            return TargetUtil.createMissed();
        }

        Entity entity = this.getRandomEntity(caster, clearance, range, e -> pred.test(new EntityHitResult(e)));
        if (entity == null) return TargetUtil.createMissed();
        return new EntityHitResult(entity);
    }

    /**
     * Returns a {@link HitResult} of what the caster looks at.<br>
     * <p>If the caster looks at something that doesn't correspond to the enum's type,
     * isn't in the range or doesn't adhere to the predicate a {@link HitResult.Type#MISS} is returned.<br>
     * The type of the result should be determined with {@link HitResult#getType()}.
     * @param caster      The entity that is casting the spell.
     * @param range       The range in which the entity must be.
     * @param predicate   A condition the entity must fulfill.
     * @param fluidsBlock Whether the raycast is blocked by fluids.
     * @return A {@link HitResult}, that is either a {@link BlockHitResult}, {@link EntityHitResult}
     * or a {@link HitResult.Type#MISS} if nothing valid is focused (check this with {@link HitResult#getType()}).
     * @see RaycastUtil
     */
    public HitResult getFocusedTarget(
            LivingEntity caster, int range, Predicate<HitResult> predicate, boolean fluidsBlock
    ) {
        if (this == SELF) {
            EntityHitResult hitResult = new EntityHitResult(caster);
            if (predicate.test(hitResult)) return hitResult;
            return TargetUtil.createMissed();
        }

        HitResult hitResult = RaycastUtil.raycast(caster, range, fluidsBlock);
        switch (hitResult.getType()) {
            case HitResult.Type.MISS -> {
                return hitResult;
            }
            case HitResult.Type.BLOCK -> {
                if (this == BLOCK && predicate.test(hitResult)) return hitResult;
                return TargetUtil.createMissed();
            }
            case HitResult.Type.ENTITY -> {
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                if (this.createEntityPredicate().test(entity) && predicate.test(hitResult)) return hitResult;
                return TargetUtil.createMissed();
            }
        }
        return TargetUtil.createMissed(); // This should not be reached (all types of HitResult are covered).
    }

    /**
     * Helps to ensure that the entity corresponds to the enum's type.
     */
    private Predicate<Entity> createEntityPredicate() {
        Predicate<Entity> pred = e -> false;
        switch (this) {
            case LIVING_ENTITY -> pred = e -> e instanceof LivingEntity;
            case HOSTILE -> pred = e -> e instanceof HostileEntity;
            case PLAYER -> pred = e -> e instanceof PlayerEntity;
            case HOSTILE_OR_PLAYER -> pred = e -> e instanceof PlayerEntity || e instanceof HostileEntity;
        }
        return pred;
    }

    private static HitResult createMissed() {
        return BlockHitResult.createMissed(null, null, null);
    }

    /**
     * Used to retrieve the {@link BlockPos} of a valid {@link HitResult}.
     * <p><b>The {@link HitResult} must not be of type {@link HitResult.Type#MISS},
     * otherwise an {@link IllegalArgumentException} is thrown.</b>
     */
    public static BlockPos getBlockPos(HitResult hitResult) {
        switch (hitResult.getType()) {
            case HitResult.Type.MISS -> throw new IllegalArgumentException("Cannot retrieve BlockPos of HitResult of type MISS");
            case HitResult.Type.BLOCK -> { return ((BlockHitResult) hitResult).getBlockPos(); }
            case HitResult.Type.ENTITY -> { return ((EntityHitResult) hitResult).getEntity().getBlockPos(); }
        }
        return new BlockPos(Vec3i.ZERO); // This should not be reached (all types of HitResult are covered).
    }
}
