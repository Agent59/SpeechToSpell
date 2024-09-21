package net.agent59.block.entity;

import net.agent59.StSMain;
import net.agent59.block.StSBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class StSBlockEntities {

    public static final BlockEntityType<WandMakerBlockEntity> WANDMAKER_BLOCK_ENTITY = registerBlockEntity("wandmaker_block_entity",
            FabricBlockEntityTypeBuilder.create(WandMakerBlockEntity::new, StSBlocks.WANDMAKER_BLOCK).build());

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, StSMain.id(name), blockEntityType);
    }

    public static void registerModBlockEntities() {
        StSMain.LOGGER.info("Registering Mod Block Entities for " + StSMain.MOD_NAME);
    }
}
