package net.agent59.stp.block.entity;

import net.agent59.stp.Main;
import net.agent59.stp.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntities {

    public static final BlockEntityType<WandMakerBlockEntity> WANDMAKER_BLOCK_ENTITY = registerBlockEntity("wandmaker_block_entity",
            FabricBlockEntityTypeBuilder.create(WandMakerBlockEntity::new, ModBlocks.WANDMAKER_BLOCK).build());

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntityType) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Main.MOD_ID, name), blockEntityType);
    }

    public static void registerModBlockEntities() {
        Main.LOGGER.info("Registering Mod Block Entities for " + Main.MOD_ID);
    }
}
