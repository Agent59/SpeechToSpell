package net.agent59.stp.gui.WandSettings;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.agent59.stp.Main;
import net.agent59.stp.util.UpdateNbt;
import net.agent59.stp.spell.SpellHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class WandSettingsGui extends LightweightGuiDescription {
    public WandSettingsGui(ItemStack wand) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(256, 240);
        root.setInsets(Insets.ROOT_PANEL);

        AtomicInteger selectedHotbarSpellButton = new AtomicInteger();
        selectedHotbarSpellButton.set(0);

        AtomicReference<WButton> selectedHotbarSpellButtonObj = new AtomicReference<>(null);



        //left panel
        WPlainPanel hotbarPanel = new WPlainPanel();

        WLabel hotbarLabel = new WLabel(new TranslatableText("Spell-Hotbar"));
        hotbarPanel.add(hotbarLabel, 2, 0);

        // hotbarSpellButtons
        for (int i = 1; i <= 5 ; i++) {
            WButton hotbarSpellButton = new WButton();

            String spellNameButton = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + i);
            if (spellNameButton == "") {
                hotbarSpellButton.setLabel(new LiteralText("No spell selected"));
            } else {
                hotbarSpellButton.setLabel(new LiteralText(spellNameButton));
            }

            int finalI = i;
            hotbarSpellButton.setOnClick(() -> {
                hotbarSpellButton.setLabel(new LiteralText("No spell selected"));
                UpdateNbt.updateWandNbtFromClient(".hotbarSpell" + finalI, "", 0);

                selectedHotbarSpellButton.set(finalI);
                selectedHotbarSpellButtonObj.set(hotbarSpellButton);
            });
            hotbarPanel.add(hotbarSpellButton, 2, i * 50, 150, 20);
        }

        // shows which hotbarSpellButton is currently selected
        WDynamicLabel selectedHotbarSpellLabel = new WDynamicLabel(() -> ((
                selectedHotbarSpellButton.get() == 0) ? "No Spell-Hotbar-slot selected" : "selected Spell-Hotbar-slot: " + selectedHotbarSpellButton.get()
        ));
        hotbarPanel.add(selectedHotbarSpellLabel, 2, 300, 150, 20);


        //hotbarPanel.setBackgroundPainter(BackgroundPainter.createColorful(0xFF0000));
        root.add(hotbarPanel, 0, 0, 10, 20);



        //right panel
        WPlainPanel spellPanel = new WPlainPanel();

        WLabel spellsLabel = new WLabel(new TranslatableText("Spells"));
        spellPanel.add(spellsLabel, 6, 0);

        ArrayList<SpellHandler.SpellInterface> spells = SpellHandler.getSpellList();
        ArrayList<String> spellNames = new ArrayList<String>();
        for (SpellHandler.SpellInterface spell : spells) {
            spellNames.add(spell.getName());
        }

        BiConsumer<String, SpellButtonWidget> configurator = (String spellName, SpellButtonWidget spellButton) -> {
            spellButton.button.setLabel(new LiteralText(spellName));
            // TODO add spell icons
            // spellButton.button.setIcon(new ItemIcon());
            spellButton.button.setOnClick(() -> {
                if (selectedHotbarSpellButton.get() != 0) {
                    UpdateNbt.updateWandNbtFromClient(".hotbarSpell" + selectedHotbarSpellButton.get(), spellName, 0);
                    selectedHotbarSpellButtonObj.get().setLabel(new LiteralText(spellName));
                }
            });
        };

        WListPanel<String, SpellButtonWidget> spellListPanel = new WListPanel<>(spellNames, SpellButtonWidget::new, configurator);
        spellListPanel.setListItemHeight(2*12);

        //spellListPanel.setBackgroundPainter(BackgroundPainter.createColorful(0xFF0000));

        spellPanel.add(spellListPanel, 1, 8,  175, 350);

        //spellPanel.setBackgroundPainter(BackgroundPainter.createColorful(0xFF0000));
        root.add(spellPanel, 10, 0, 10, 20);


        root.validate(this);
    }
}
