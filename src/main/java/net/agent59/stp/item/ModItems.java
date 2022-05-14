package net.agent59.stp.item;

import net.agent59.stp.Main;
import net.agent59.stp.item.custom.WandItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModItems {

    public static final Item WAND = registerItem("wand",
            new WandItem(new FabricItemSettings().group(ItemGroup.TOOLS)));

    public static final Item CRIMSON_WAND = registerItem("crimson_wand",
            new WandItem(new FabricItemSettings().group(ItemGroup.TOOLS)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Main.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Main.LOGGER.info("Registering Mod Items for " + Main.MOD_ID);
    }
}