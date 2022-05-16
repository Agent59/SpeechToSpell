package net.agent59.stp.spell;

import net.agent59.stp.spell.spells.Aguamenti;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellHandler {

    // TODO redo cooldown system (cooldown only accounted for every single spell, not for the wand or all spells)

    public static void executeSpell(String spellString, PlayerEntity player) {
        spellString = spellString.toUpperCase();

        ArrayList<SpellInterface> spells = getSpellList();

        for (SpellInterface spell : spells) {
            if (spell.getName().toUpperCase().equals(spellString)) {
                spell.execute(player);
                break;
            }
        }
    }

    // when adding spells don't forget to add them to the array
    public static ArrayList<SpellInterface> getSpellList() {
        ArrayList<SpellInterface> spellsArray = new ArrayList<>();
        spellsArray.add(new Aguamenti());

        return spellsArray;
    }

    public static HashMap<String, SpellInterface> getSpellNameHashmap() {
        HashMap<String, SpellInterface> spellsHashmap = new HashMap<>();
        for (SpellInterface spell : getSpellList()) {
            spellsHashmap.put(spell.getName(), spell);
        }
        return spellsHashmap;
    }

}
