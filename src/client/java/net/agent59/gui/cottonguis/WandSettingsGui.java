package net.agent59.gui.cottonguis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.agent59.Main;
import net.agent59.gui.cottonguis.widgets.SpellButtonWidget;
import net.agent59.spell.SpellHandler;
import net.agent59.spell.SpellInterface;
import net.agent59.util.UpdateNbt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
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

        WLabel hotbarLabel = new WLabel(Text.translatable("Spell-Hotbar"));
        hotbarPanel.add(hotbarLabel, 2, 0);

        // hotbarSpellButtons
        for (int i = 1; i <= 5 ; i++) {
            WButton hotbarSpellButton = new WButton();

            assert wand.getNbt() != null;
            String spellNameButton = wand.getNbt().getString(Main.MOD_ID + ".hotbarSpell" + i);
            if (Objects.equals(spellNameButton, "")) {
                hotbarSpellButton.setLabel(Text.literal("No spell selected"));
            } else {
                hotbarSpellButton.setLabel(Text.literal(spellNameButton));
                hotbarSpellButton.setIcon(new ItemIcon(SpellHandler.getSpellNameHashmap().get(spellNameButton).asItem()));
            }

            int finalI = i;
            hotbarSpellButton.setOnClick(() -> {
                hotbarSpellButton.setLabel(Text.literal("No spell selected"));
                hotbarSpellButton.setIcon(null);
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

        WLabel spellsLabel = new WLabel(Text.translatable("Spells"));
        spellPanel.add(spellsLabel, 6, 0);

        ArrayList<SpellInterface> spells = SpellHandler.getSpellList();
        ArrayList<String> spellNames = new ArrayList<>();
        for (SpellInterface spell : spells) {
            spellNames.add(spell.getStringName());
        }

        BiConsumer<String, SpellButtonWidget> configurator = (String spellName, SpellButtonWidget spellButton) -> {
            spellButton.button.setLabel(Text.translatable(spellName));
            spellButton.button.setIcon(new ItemIcon(SpellHandler.getSpellNameHashmap().get(spellName).asItem()));
            spellButton.button.setOnClick(() -> {
                if (selectedHotbarSpellButton.get() != 0) {
                    UpdateNbt.updateWandNbtFromClient(".hotbarSpell" + selectedHotbarSpellButton.get(), spellName, 0);
                    selectedHotbarSpellButtonObj.get().setLabel(Text.translatable(spellName));
                    selectedHotbarSpellButtonObj.get().setIcon(new ItemIcon(SpellHandler.getSpellNameHashmap().get(spellName).asItem()));
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
