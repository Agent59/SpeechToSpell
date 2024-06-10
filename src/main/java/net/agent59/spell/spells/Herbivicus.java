package net.agent59.spell.spells;

import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Herbivicus extends Item implements SpellInterface {
    private static final String NAME = "Herbivicus";
    private static final int RANGE = 20;
    private static final int CASTING_COOLDOWN = 50;
    private static final SpellType SPELLTYPE = SpellType.CHARM;

    public Herbivicus(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Lets you place a random flower.";
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
        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, false);
        if (hitResult.getType() != HitResult.Type.MISS && hitResult.getType() != HitResult.Type.ENTITY) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            ServerWorld world = player.getServerWorld();
            BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());

            ArrayList<FlowerBlock> flowerList = getListOfFlowers();
            FlowerBlock randomFlower = flowerList.get(ThreadLocalRandom.current().nextInt(0, flowerList.size()));

            BlockState blockState = world.getBlockState(blockPos);
            if (randomFlower.canPlaceAt(blockState, world, blockPos)) {
                world.setBlockState(blockPos, randomFlower.getDefaultState());
                //set cooldown
                player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
            }
        }
    }

    private ArrayList<FlowerBlock> getListOfFlowers() {
        ArrayList<FlowerBlock> list = new ArrayList<>();
        list.add((FlowerBlock) Blocks.DANDELION);
        list.add((FlowerBlock) Blocks.POPPY);
        list.add((FlowerBlock) Blocks.BLUE_ORCHID);
        list.add((FlowerBlock) Blocks.ALLIUM);
        list.add((FlowerBlock) Blocks.AZURE_BLUET);
        list.add((FlowerBlock) Blocks.RED_TULIP);
        list.add((FlowerBlock) Blocks.ORANGE_TULIP);
        list.add((FlowerBlock) Blocks.WHITE_TULIP);
        list.add((FlowerBlock) Blocks.PINK_TULIP);
        list.add((FlowerBlock) Blocks.OXEYE_DAISY);
        list.add((FlowerBlock) Blocks.CORNFLOWER);
        return list;
    }
}

