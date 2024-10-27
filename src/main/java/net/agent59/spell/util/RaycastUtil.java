package net.agent59.spell.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Utility functions to find out what an entity is looking at.
 */
public class RaycastUtil {

    /**
     * Checks what other entity the given entity is looking at within the maximum distance.
     * @param entity The entity from whom the raycast is started.
     * @param maxDistance The maximal distance in which a hit is checked for.
     * @param predicate A condition a hit entity must fulfill.
     * @return An {@link EntityHitResult}, containing the entity which the given entity is looking at,
     * or {@code null} if no entity is looked at.
     */
    public static @Nullable EntityHitResult raycastEntity(Entity entity, double maxDistance, Predicate<Entity> predicate) {
        Vec3d minPos = entity.getCameraPosVec(0);
        Vec3d connect = entity.getRotationVec(0).multiply(maxDistance);
        Vec3d maxPos = minPos.add(connect);
        Box box = entity.getBoundingBox().stretch(connect).expand(1);
        return ProjectileUtil.raycast(entity, minPos, maxPos, box, predicate, maxDistance * maxDistance);
    }

    /**
     * The same as {@link #raycastEntity(Entity, double, Predicate)}, but sets the predicate to always be true.
     */
    public static @Nullable EntityHitResult raycastEntity(Entity entity, double maxDistance) {
        return RaycastUtil.raycastEntity(entity, maxDistance, e -> true);
    }

    /**
     * Checks what block the given entity is looking at within the maximum distance.
     * @param entity The entity from whom the raycast is started.
     * @param maxDistance The maximal distance in which a hit is checked for.
     * @param fluidsBlock Whether fluids should stop the raycast.
     * @return A {@link BlockHitResult}, which, if not of type {@code MISS},
     * contains the block the entity is looking at.
     * @see HitResult#getType()
     */
    public static BlockHitResult raycastBlock(Entity entity, double maxDistance, boolean fluidsBlock) {
        return (BlockHitResult) entity.raycast(maxDistance, 0, fluidsBlock); // Always returns a BlockHitResult.
    }

    /**
     * Checks what an entity is looking at within the maximum distance.
     * @param entity The entity from whom the raycast is started.
     * @param maxDistance The maximal distance in which a hit is checked for.
     * @param fluidsBlock Whether fluids should stop the raycast.
     * @return The {@link HitResult} of the raycast.
     * @see HitResult#getType()
     * @see EntityHitResult
     * @see BlockHitResult
     */
    public static HitResult raycast(Entity entity, double maxDistance, boolean fluidsBlock) {
        HitResult blockHit = RaycastUtil.raycastBlock(entity, maxDistance, fluidsBlock);
        // Reduces the max distance to raycast for an entity to the distance of the block that was already hit.
        if (blockHit.getType() != HitResult.Type.MISS) maxDistance = Math.sqrt(blockHit.squaredDistanceTo(entity));

        EntityHitResult entityHitRes = RaycastUtil.raycastEntity(entity, maxDistance);
        if (entityHitRes != null) return entityHitRes;

        return blockHit;
    }
}
