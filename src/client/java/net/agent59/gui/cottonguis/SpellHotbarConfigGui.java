package net.agent59.gui.cottonguis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.agent59.cardinal_component.ClientPlayerMagicComponent;
import net.agent59.gui.cottonwidgets.WGridListPanel;
import net.agent59.gui.cottonwidgets.WSpellIconButton;
import net.agent59.spell.SpellManager;
import net.agent59.spell.spells.Spell;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * The GUI in which the player can configure their spell-hotbar.
 */
public class SpellHotbarConfigGui extends LightweightGuiDescription {
    // The values of the following final fields are measured in grid cells.
    public static final int GRID_WIDTH = 15;
    public static final int GRID_HEIGHT = 14;
    public static final int PANEL_GAPS = 3; // The gaps the hotbar panel and the spell panel use.
    public static final int PANEL_INSETS = 2; // The insets the hotbar panel and the spell panel use.
    public static final int SPELL_PANEL_X = 5;

    private final AtomicReference<WSpellHotbarButton> selected = new AtomicReference<>(null);

    public SpellHotbarConfigGui(ClientPlayerEntity player) {
        WGridPanel root = new WGridPanel();
        root.setInsets(Insets.ROOT_PANEL);
        root.setSize(GRID_WIDTH * 18 + 7, GRID_HEIGHT * 18 + 7); // The +7 is for the root's insets.
        this.setRootPanel(root);

        ClientPlayerMagicComponent playerMagicComp = ClientPlayerMagicComponent.getInstance(player);

        root.add(this.addSpellHotbarList(playerMagicComp), 0, 1, 4, GRID_HEIGHT - 1);
        root.add(new WLabel(Text.translatable("gui.speech_to_spell.spell_hotbar_config.spell_hotbar")), 0, 0);

        root.add(this.addSpellList(playerMagicComp), SPELL_PANEL_X, 1, GRID_WIDTH - SPELL_PANEL_X, GRID_HEIGHT - 1);
        root.add(new WLabel(Text.translatable("gui.speech_to_spell.spell_hotbar_config.spells")), SPELL_PANEL_X, 0);

        root.validate(this);
    }

    /**
     * @return The panel for the spell-hotbar.
     */
    private WPanel addSpellHotbarList(ClientPlayerMagicComponent playerMagicComp) {
        WGridListPanel hotbarPanel = new WGridListPanel();
        hotbarPanel.setInsets(new Insets(PANEL_INSETS));
        hotbarPanel.setGaps(PANEL_GAPS, PANEL_GAPS);
        hotbarPanel.setSize(3 * 18, 0); // The height is determined automatically.

        int i = 0;
        for (@Nullable Spell spell : playerMagicComp.getSpellHotbar()) {

            WLabel slotNumber = new WLabel(Text.of(String.valueOf(i + 1)));
            slotNumber.setSize(18, 18);
            slotNumber.setHorizontalAlignment(HorizontalAlignment.CENTER);
            slotNumber.setVerticalAlignment(VerticalAlignment.CENTER);
            hotbarPanel.add(slotNumber);

            WSpellHotbarButton hotbarButton = new WSpellHotbarButton(i, spell, thisButton -> {
                thisButton.setSpell(null);
                playerMagicComp.setSpellHotbarSlot(null, ((WSpellHotbarButton) thisButton).getIndex());
                selected.set((WSpellHotbarButton) thisButton);
                this.requestFocus(thisButton);
            });
            hotbarButton.setFallBackTooltip(Text.translatable("gui.speech_to_spell.spell_hotbar_config.no_spell_selected"));

            hotbarPanel.add(hotbarButton);
            i++;
        }

        return new WScrollPanel(hotbarPanel);
    }

    /**
     * @return The panel for the list of spells.
     */
    private WPanel addSpellList(ClientPlayerMagicComponent playerMagicComp) {
        WGridListPanel spellPanel = new WGridListPanel();
        spellPanel.setInsets(new Insets(PANEL_INSETS));
        spellPanel.setGaps(PANEL_GAPS, PANEL_GAPS);
        spellPanel.setSize((GRID_WIDTH - SPELL_PANEL_X) * 18 - 8, 0); // The height is determined automatically.

        for (Spell spell : SpellManager.getSpellsArray()) {
            spellPanel.add(new WSpellIconButton(spell, thisButton -> {
                if (selected.get() == null) return;
                playerMagicComp.setSpellHotbarSlot(thisButton.getSpell(), selected.get().getIndex());
                selected.get().setSpell(thisButton.getSpell());
            }));
        }
        return new WScrollPanel(spellPanel);
    }

    /**
     * Just adds an index to {@link WSpellIconButton}, that represents the corresponding spell-hotbar slot.
     */
    private static class WSpellHotbarButton extends WSpellIconButton {
        private final int index;

        public WSpellHotbarButton(int index, @Nullable Spell spell, Consumer<WSpellIconButton> onClick) {
            super(spell, onClick);
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }
}
