package net.agent59.stp.spell.spells;

import net.agent59.stp.util.BlockPlayerIsFacing;
import net.agent59.stp.Main;
import net.agent59.stp.spell.SpellHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Aguamenti implements SpellHandler.SpellInterface {

    int RANGE = 40;

    @Override
    public String getName() {
        return "Aguamenti";
    }

    @Override
    public Identifier getIconIdentifier() {
        return new Identifier(Main.MOD_ID, "textures/spell/aguamenti.png");
    }

    // TODO add waterlogging and add particles, when trying to place in the nether
    @Override
    public void execute(PlayerEntity player) {
        // places a water source block on the block the player is looking at
        // the block can't be further than 40 blocks away
        BlockPos blockPos = BlockPlayerIsFacing.getBlockInFront(player, RANGE);
        if (blockPos != null) {
            World world = player.getWorld();

            // water evaporates in nether (see BucketItem)
            if (world.getDimension().isUltrawarm()) {

                world.playSound(null, blockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                        0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);

                // can't spawn particles like that
                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPos.getX() + Math.random(),
                            (double)blockPos.getY() + Math.random(), (double)blockPos.getZ() + Math.random(),
                            0.0, 0.0, 0.0);
                }
            } else {
                BlockState blockState = Blocks.WATER.getDefaultState();
                world.setBlockState(blockPos, blockState);

                //set cooldown
                Item item = player.getActiveItem().getItem();
                player.getItemCooldownManager().set(item, castingCooldown);
            }
        }
    }
}