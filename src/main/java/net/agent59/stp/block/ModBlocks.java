package net.agent59.stp.block;

import net.agent59.stp.Main;
import net.agent59.stp.block.custom.WandMakerBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block WANDMAKER_BLOCK = registerBlock("wand_maker",
            new WandMakerBlock(FabricBlockSettings.of(Material.WOOD).strength(2.5f).sounds(BlockSoundGroup.WOOD)),
            ItemGroups.FUNCTIONAL);

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registries.BLOCK, new Identifier(Main.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, ItemGroup group) {
        BlockItem blockItem = Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
        ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(blockItem));
    }

    public static void registerModBlocks() {
        Main.LOGGER.info("Registering Mod Items for " + Main.MOD_ID);
    }
}
