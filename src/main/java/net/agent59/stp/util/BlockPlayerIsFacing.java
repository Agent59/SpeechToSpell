package net.agent59.stp.util;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


// TODO its usages with Entity.raycast

public class BlockPlayerIsFacing {
    // calculates the block the player is facing at
    // this is Based on the raycast method of the Item class

    public static BlockPos getBlock(PlayerEntity player, int range) {
        World world = player.getWorld();

        // checks for a maximum distance of the range if there is a block other than air

        float f = player.getPitch();
        float g = player.getYaw();
        Vec3d vec3d = player.getEyePos();
        float h = MathHelper.cos(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float i = MathHelper.sin(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float j = -MathHelper.cos(-f * ((float) Math.PI / 180));
        float k = MathHelper.sin(-f * ((float) Math.PI / 180));
        float l = i * j;
        float n = h * j;

        for (double d = 1; d < range; d++) {
            Vec3d vec3d2 = vec3d.add((double) l * d, (double) k * d, (double) n * d);

            BlockPos blockPos = new BlockPos(vec3d2);

            if (world.getBlockState(blockPos) != Blocks.AIR.getDefaultState()) {
                return blockPos;
            }
        }
        return null;
    }

    public static BlockPos getBlockInFront(PlayerEntity player, int range) {
        World world = player.getWorld();

        // checks for a maximum distance of the range if there is a block other than air
        // it returns the block in front of the block the player is facing

        float f = player.getPitch();
        float g = player.getYaw();
        Vec3d vec3d = player.getEyePos();
        float h = MathHelper.cos(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float i = MathHelper.sin(-g * ((float) Math.PI / 180) - (float) Math.PI);
        float j = -MathHelper.cos(-f * ((float) Math.PI / 180));
        float k = MathHelper.sin(-f * ((float) Math.PI / 180));
        float l = i * j;
        float n = h * j;

        for (double d = 1; d < range; d++) {
            Vec3d vec3d2 = vec3d.add((double) l * d, (double) k * d, (double) n * d);

            BlockPos blockPos = new BlockPos((vec3d2));

            if (world.getBlockState(blockPos) != Blocks.AIR.getDefaultState()) {

                Vec3d vec3d2_last = vec3d.add((double) l * (d-1), (double) k * (d-1), (double) n * (d-1));

                return new BlockPos(vec3d2_last);
            }
        }
        return null;
    }
}
