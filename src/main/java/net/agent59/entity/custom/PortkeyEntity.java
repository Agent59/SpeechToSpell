package net.agent59.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortkeyEntity extends RayEntity {

    private BlockPos targetBlockPos;

    public PortkeyEntity(EntityType<? extends RayEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setTargetBlockPos(BlockPos blockPos) {
        this.targetBlockPos = blockPos;
    }

    public BlockPos getTargetBlockPos() {
        return this.targetBlockPos;
    }


    @Override
    public void tick() {
        World world = this.getWorld();
        if (!this.getWorld().isClient()) {
            BlockPos pos = this.getBlockPos();
            if (world.getBlockState(pos) == Blocks.AIR.getDefaultState()) {
                this.kill();
            }
            boolean bl1 = world.getBlockState(this.targetBlockPos).blocksMovement();
            boolean bl2 = world.getBlockState(this.targetBlockPos.withY(this.targetBlockPos.getY() + 1)).blocksMovement();
            if (bl1 || bl2) {
                this.kill();
            }
        }
    }

    @Override
    public void writeCustomDataToNbt2(NbtCompound nbt) {
        nbt.put("targetBlockPos", this.toNbtList(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()));
    }

    @Override
    public void readCustomDataFromNbt2(NbtCompound nbt) {
        NbtList nbtList;
        if (nbt.contains("targetBlockPos", 9) && (nbtList = nbt.getList("targetBlockPos", 5)).size() == 3) {
            this.targetBlockPos = BlockPos.ofFloored(nbtList.getFloat(0), nbtList.getFloat(1), nbtList.getFloat(2));
        }
    }
}
