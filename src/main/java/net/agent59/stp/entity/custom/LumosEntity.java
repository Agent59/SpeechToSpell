package net.agent59.stp.entity.custom;

import net.agent59.stp.item.ModItems;
import net.agent59.stp.spell.spells.Lumos;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LumosEntity extends RayEntity {
    private BlockPos oldBlockPos;

    public LumosEntity(EntityType<? extends RayEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick2() {
        World world1 = this.getWorld();
        Entity owner = this.getOwner();
        if (owner == null) {
            this.discard();
            if (oldBlockPos != null && (world1.getBlockState(oldBlockPos) == Blocks.LIGHT.getDefaultState())) {
                world1.setBlockState(oldBlockPos, Blocks.AIR.getDefaultState());
            }
            return;
        }
        this.setPosition(owner.getX(), owner.getEyeY(), owner.getZ());
        this.setVelocity(owner.getVelocity());

        if (oldBlockPos != null && oldBlockPos != this.getBlockPos()) {
            if (world1.getBlockState(oldBlockPos) == Blocks.LIGHT.getDefaultState()) {
                world1.setBlockState(oldBlockPos, Blocks.AIR.getDefaultState());
            }
            if (world1.getBlockState(this.getBlockPos()) == Blocks.AIR.getDefaultState()) {
                world1.setBlockState(this.getBlockPos(), Blocks.LIGHT.getDefaultState());
            }
        }

        this.oldBlockPos = this.getBlockPos();
        if (owner instanceof ServerPlayerEntity) {
            Lumos lumos = (Lumos) ModItems.LUMOS;
            //refreshes cooldown
            ((ServerPlayerEntity)owner).getItemCooldownManager().set(lumos.asItem(), lumos.getCastingCooldown());
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (oldBlockPos != null && this.getWorld().getBlockState(oldBlockPos) == Blocks.LIGHT.getDefaultState()) {
            this.getWorld().setBlockState(oldBlockPos, Blocks.AIR.getDefaultState());
        }
        super.remove(reason);
    }

    @Override
    public void writeCustomDataToNbt2(NbtCompound nbt) {
        nbt.put("oldBlockPos", this.toNbtList(this.getX(), this.getY(), this.getZ()));
    }

    @Override
    public void readCustomDataFromNbt2(NbtCompound nbt) {
        NbtList nbtList;
        if (nbt.contains("oldBlockPos", 9) && (nbtList = nbt.getList("oldBlockPos", 6)).size() == 3) {
            this.oldBlockPos = new BlockPos(nbtList.getDouble(0), nbtList.getDouble(1), nbtList.getDouble(2));
        }
    }
}
