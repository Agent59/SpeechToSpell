package net.agent59.spell;

import net.agent59.Main;
import net.agent59.item.ModItems;
import net.agent59.item.custom.WandItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellHandler {

    public static void executeSpellIfAllowed(ServerPlayerEntity player, String spellString) {

        // get the spell by the string
        SpellInterface spell = SpellHandler.getSpellNameHashmap().get(spellString);

        ItemStack wand;
        if ((((wand = player.getMainHandStack()).getItem() instanceof WandItem) || // checks if the player holds a wand
                ((wand = player.getOffHandStack()).getItem() instanceof WandItem)) && spell != null) {

            // executes the spell if the player is holding rightclick with a wand or has the spell selected in his Spell-hotbar
            // and if the wand has no cooldown
            assert wand.getNbt() != null;
            int selectedSlot = wand.getNbt().getInt(Main.MOD_ID + ".spellHotbarSelectedSlot");
            String selectedHotbarSpellName = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + selectedSlot);

            // check if cooling down
            boolean coolingDown = player.getItemCooldownManager().isCoolingDown(spell.asItem());

            if ((player.getActiveItem() == wand || spellString.equals(selectedHotbarSpellName)) && !coolingDown) {
                spell.execute(player);
            }
        }
    }


    // when adding spells don't forget to add them to the array
    public static ArrayList<SpellInterface> getSpellList() {
        ArrayList<SpellInterface> spellsArray = new ArrayList<>();
        spellsArray.add((SpellInterface) ModItems.AGUAMENTI);
        spellsArray.add((SpellInterface) ModItems.STUPEFY);
        spellsArray.add((SpellInterface) ModItems.PROTEGO);
        spellsArray.add((SpellInterface) ModItems.HOMENUM_REVELIO);
        spellsArray.add((SpellInterface) ModItems.APPARATE);
        spellsArray.add((SpellInterface) ModItems.ASCENDIO);
        spellsArray.add((SpellInterface) ModItems.EXPELLIARMUS);
        spellsArray.add((SpellInterface) ModItems.PETRIFICUS_TOTALUS);
        spellsArray.add((SpellInterface) ModItems.ACCIO);
        spellsArray.add((SpellInterface) ModItems.ALARTE_ASCENDARE);
        spellsArray.add((SpellInterface) ModItems.MELOFORS);
        spellsArray.add((SpellInterface) ModItems.FUMOS);
        spellsArray.add((SpellInterface) ModItems.INCENDIO);
        spellsArray.add((SpellInterface) ModItems.HERBIVICUS);
        spellsArray.add((SpellInterface) ModItems.CISTEM_APERIO);
        spellsArray.add((SpellInterface) ModItems.FLIPENDO);
        spellsArray.add((SpellInterface) ModItems.LUMOS);
        spellsArray.add((SpellInterface) ModItems.NOX);
        spellsArray.add((SpellInterface) ModItems.TEMPEST);
        spellsArray.add((SpellInterface) ModItems.PORTUS);

        return spellsArray;
    }

    public static HashMap<String, SpellInterface> getSpellNameHashmap() {
        HashMap<String, SpellInterface> spellsHashmap = new HashMap<>();
        for (SpellInterface spell : getSpellList()) {
            spellsHashmap.put(spell.getStringName(), spell);
        }
        return spellsHashmap;
    }

}
