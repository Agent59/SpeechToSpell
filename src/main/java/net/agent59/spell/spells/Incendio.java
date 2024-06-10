package net.agent59.spell.spells;

import net.agent59.spell.SpellInterface;
import net.agent59.spell.SpellType;
import net.minecraft.block.*;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class Incendio extends Item implements SpellInterface {
    private static final String NAME = "Incendio";
    private static final int RANGE = 10;
    private static final int CASTING_COOLDOWN = 100;
    private static final SpellType SPELLTYPE = SpellType.CHARM;

    public Incendio(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Lets you create a fire.";
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
        // see FlintAndStellItem

        HitResult hitResult = player.getCameraEntity().raycast(RANGE, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            ServerWorld world = player.getServerWorld();
            BlockPos blockPos = blockHitResult.getBlockPos();

            BlockState blockState = world.getBlockState(blockPos);
            if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
                world.setBlockState(blockPos, blockState.with(Properties.LIT, true));
                //set cooldown
                player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
                return;
            }
            BlockPos blockPos2 = blockPos.offset(blockHitResult.getSide());
            if (AbstractFireBlock.canPlaceAt(world, blockPos2, blockHitResult.getSide())) {
                BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
                world.setBlockState(blockPos2, blockState2);
                //set cooldown
                player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
            }
        }
    }
}
