package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Aguamenti extends Item implements SpellInterface {
    private static final String NAME = "Aguamenti";
    private static final int RANGE = 40;
    private static final int CASTING_COOLDOWN = 100;
    private static final SpellType SPELLTYPE = SpellType.CHARM;

    public Aguamenti(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Places a water source.";
    }

    @Override
    public int getRange() {
        return RANGE;
    }

    @Override
    public int getCastingCooldown() {
        return CASTING_COOLDOWN;
    }

    @Override
    public SpellType getSpellType() {
        return SPELLTYPE;
    }

    @Override
    public void execute(ServerPlayerEntity player) {
        // places a water source block on the block the player is looking at

        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, true);
        if (hitResult.getType() != HitResult.Type.MISS) {
            ServerWorld world = player.getWorld();
            BlockPos blockPos = null;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)hitResult;
                blockPos = new BlockPos(blockHitResult.getBlockPos());
                BlockState blockState = world.getBlockState(blockPos);

                if (!canPlaceWater(blockState, blockPos, world)) {
                    Direction direction = blockHitResult.getSide();
                    blockPos = new BlockPos(blockPos.offset(direction));
                }

            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                blockPos = new BlockPos(hitResult.getPos());
            }
            assert blockPos != null;

            // water evaporates in nether (see BucketItem)
            if (world.getDimension().ultrawarm()) {

                world.playSound(null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                        0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
                for (int l = 0; l < 8; ++l) {
                    world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                            blockPos.getX() + Math.random(), blockPos.getY() + Math.random(), blockPos.getZ() + Math.random(),
                            1, 0, 0, 0, 0);
                }
            } else {
                BlockState waterDefaultState = Blocks.WATER.getDefaultState();
                BlockState blockState = world.getBlockState(blockPos);

                if (blockState.isAir()) {
                    world.setBlockState(blockPos, waterDefaultState);
                }
                else if (blockState.getBlock() instanceof FluidFillable) {
                    ((FluidFillable) blockState.getBlock()).tryFillWithFluid(world, blockPos, blockState, waterDefaultState.getFluidState());
                }
                else {
                    world.breakBlock(blockPos, true);
                    world.setBlockState(blockPos, waterDefaultState);
                }
            }

            //set cooldown
            player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
        }
    }

    private boolean canPlaceWater(BlockState blockState, BlockPos blockPos, World world) {
        return blockState.canBucketPlace(Fluids.WATER) ||
                blockState.getBlock() instanceof FluidFillable &&
                        ((FluidFillable) blockState.getBlock()).canFillWithFluid(world, blockPos, blockState, Fluids.WATER);

    }
}