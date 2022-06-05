package net.agent59.stp.spell;

import net.agent59.stp.Main;
import net.agent59.stp.item.ModItems;
import net.agent59.stp.item.custom.WandItem;
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
        spellsArray.add((SpellInterface) new ItemStack(ModItems.AGUAMENTI).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.STUPEFY).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.PROTEGO).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.HOMENUM_REVELIO).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.APPARATE).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.ASCENDIO).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.EXPELLIARMUS).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.PETRIFICUS_TOTALUS).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.ACCIO).getItem().asItem());
        spellsArray.add((SpellInterface) new ItemStack(ModItems.ALARTE_ASCENDARE).getItem().asItem());

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
