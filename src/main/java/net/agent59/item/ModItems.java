package net.agent59.item;

import net.agent59.Main;
import net.agent59.item.custom.GuideBookItem;
import net.agent59.item.custom.WandItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;


public class ModItems {

    public static final Item WAND = registerItem("wand",
            new WandItem(new FabricItemSettings()));

    public static final Item CRIMSON_WAND = registerItem("crimson_wand",
            new WandItem(new FabricItemSettings()), ItemGroups.TOOLS);

    public static final Item GUIDE_BOOK = registerItem("guide_book",
            new GuideBookItem(new FabricItemSettings()), ItemGroups.TOOLS);

    private static Item registerItem(String name, Item item, @Nullable RegistryKey<ItemGroup> group) {
        Item item1 = Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), item);
        if (group != null) ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.add(item1));
        return item1;
    }

    private static Item registerItem(String name, Item item) {
        return registerItem(name, item, null);
    }

    /**
     * Registers an item whose purpose is only to be used for displaying an icon for a spell.
     */
    public static Item registerDisplayIconItem(Identifier id) {
        return Registry.register(Registries.ITEM, id, new Item(new FabricItemSettings()));
    }

    private static Item registerDisplayIconItem(String path) {
        return registerDisplayIconItem(Main.id(path));
    }

    public static void registerModItems() {
        Main.LOGGER.info("Registering Mod Items for " + Main.MOD_ID);
    }
}
