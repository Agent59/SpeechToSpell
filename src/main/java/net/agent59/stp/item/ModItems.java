package net.agent59.stp.item;

import net.agent59.stp.Main;
import net.agent59.stp.item.custom.WandItem;
import net.agent59.stp.spell.spells.Aguamenti;
import net.agent59.stp.spell.spells.HomenumRevelio;
import net.agent59.stp.spell.spells.Protego;
import net.agent59.stp.spell.spells.Stupefy;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModItems {

    public static final Item WAND = registerItem("wand",
            new WandItem(new FabricItemSettings()));

    public static final Item CRIMSON_WAND = registerItem("crimson_wand",
            new WandItem(new FabricItemSettings().group(ItemGroup.TOOLS)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Main.MOD_ID, name), item);
    }

    // create a SpellItem for every spell: needed for the cooldown-system
    public static final Item AGUAMENTI = registerItem("aguamenti",
            new Aguamenti(new FabricItemSettings()));

    public static final Item STUPEFY = registerItem("stupefy",
            new Stupefy(new FabricItemSettings()));

    public static final Item PROTEGO = registerItem("protego",
            new Protego(new FabricItemSettings()));

    public static final Item HOMENUM_REVELIO = registerItem("homenum_revelio",
            new HomenumRevelio(new FabricItemSettings()));


    public static void registerModItems() {
        Main.LOGGER.info("Registering Mod Items for " + Main.MOD_ID);
    }
}