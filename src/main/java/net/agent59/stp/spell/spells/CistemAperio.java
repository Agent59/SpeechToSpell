package net.agent59.stp.spell.spells;

import net.agent59.stp.spell.SpellInterface;
import net.agent59.stp.spell.SpellType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class CistemAperio extends Item implements SpellInterface {
    private static final String NAME = "Cistem Aperio";
    private static final int RANGE = 10;
    private static final int CASTING_COOLDOWN = 100;
    private static final SpellType SPELLTYPE = SpellType.CHARM;

    public CistemAperio(Settings settings) {
        super(settings);
    }

    @Override
    public String getStringName() {
        return NAME;
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
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            ServerWorld world = player.getWorld();

            if (world.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof ChestBlock) {
                BlockPos blockPos = blockHitResult.getBlockPos();
                BlockState blockState = world.getBlockState(blockPos);

                NamedScreenHandlerFactory namedScreenHandlerFactory = ((ChestBlock) blockState.getBlock()).createScreenHandlerFactory(blockState, world, blockPos);
                if (namedScreenHandlerFactory != null) {
                    player.openHandledScreen(namedScreenHandlerFactory);
                }
                ChestBlockEntity chestEntity = (ChestBlockEntity) world.getBlockEntity(blockPos);
                assert chestEntity != null;
                chestEntity.onOpen(player);

                //set cooldown
                player.getItemCooldownManager().set(this.asItem(), getCastingCooldown());
            }
        }
    }
}
