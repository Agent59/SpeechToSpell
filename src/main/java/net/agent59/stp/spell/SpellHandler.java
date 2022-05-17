package net.agent59.stp.spell;

import net.agent59.stp.item.ModItems;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellHandler {

    // when adding spells don't forget to add them to the array
    public static ArrayList<SpellInterface> getSpellList() {
        ArrayList<SpellInterface> spellsArray = new ArrayList<>();
        spellsArray.add((SpellInterface) new ItemStack(ModItems.AGUAMENTI).getItem().asItem());

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
