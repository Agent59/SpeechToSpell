package net.agent59.stp.item;

import net.agent59.stp.Main;
import net.agent59.stp.item.custom.GuideBookItem;
import net.agent59.stp.item.custom.WandItem;
import net.agent59.stp.spell.spells.*;
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

    public static final Item GUIDE_BOOK = registerItem("guide_book",
            new GuideBookItem(new FabricItemSettings().group(ItemGroup.MISC)));

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

    public static final Item APPARATE = registerItem("apparate",
            new Apparate(new FabricItemSettings()));

    public static final Item ASCENDIO = registerItem("ascendio",
            new Ascendio(new FabricItemSettings()));

    public static final Item EXPELLIARMUS = registerItem("expelliarmus",
            new Expelliarmus(new FabricItemSettings()));

    public static final Item PETRIFICUS_TOTALUS = registerItem("petrificus_totalus",
            new PetrificusTotalus(new FabricItemSettings()));

    public static final Item ACCIO = registerItem("accio",
            new Accio(new FabricItemSettings()));

    public static final Item ALARTE_ASCENDARE = registerItem("alarte_ascendare",
            new AlarteAscendare(new FabricItemSettings()));

    public static final Item MELOFORS = registerItem("melofors",
            new Melofors(new FabricItemSettings()));

    public static final Item FUMOS = registerItem("fumos",
            new Fumos(new FabricItemSettings()));

    public static final Item INCENDIO = registerItem("incendio",
            new Incendio(new FabricItemSettings()));

    public static final Item HERBIVICUS = registerItem("herbivicus",
            new Herbivicus(new FabricItemSettings()));

    public static final Item CISTEM_APERIO = registerItem("cistem_aperio",
            new CistemAperio(new FabricItemSettings()));

    public static final Item FLIPENDO = registerItem("flipendo",
            new Flipendo(new FabricItemSettings()));

    public static final Item LUMOS = registerItem("lumos",
            new Lumos(new FabricItemSettings()));

    public static final Item NOX = registerItem("nox",
            new Nox(new FabricItemSettings()));

    public static final Item TEMPEST = registerItem("tempest",
            new Tempest(new FabricItemSettings()));

    public static final Item PORTUS = registerItem("portus",
            new Portus(new FabricItemSettings()));



    public static void registerModItems() {
        Main.LOGGER.info("Registering Mod Items for " + Main.MOD_ID);
    }
}