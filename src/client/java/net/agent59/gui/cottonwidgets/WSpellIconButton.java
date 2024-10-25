package net.agent59.gui.cottonwidgets;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.agent59.spell.spells.Spell;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Displays a {@link Spell} in an Itemslot, but behaves more like a {@link WButton}.
 * <p>The {@link #fallBackIcon} and {@link #fallBackTooltip} are used if the {@link #spell} is {@code null}.
 */
public class WSpellIconButton extends WWidget {
    public static final int DEFAULT_SIZE = 18;
    /**
     * The style that is used in the tooltip for the parts, of the spell's description, that are not styled .
     */
    public static final Style DESCRIPTION_FALLBACK_STYLE = Style.EMPTY.withFormatting(Formatting.DARK_GRAY);
    public static final int FOCUS_AND_HOVER_OUTLINE_COLOR = 0xFFFFFFFF;

    @Nullable private Spell spell;
    @Nullable private BackgroundPainter backgroundPainter = BackgroundPainter.SLOT;
    @Nullable private Consumer<WSpellIconButton> onClick;

    private ItemIcon fallBackIcon = new ItemIcon(ItemStack.EMPTY);
    private Text fallBackTooltip = Text.empty();

    public WSpellIconButton() {
        this.setSize(DEFAULT_SIZE);
    }

    public WSpellIconButton(@Nullable Spell spell) {
        this();
        this.spell = spell;
    }

    public WSpellIconButton(@Nullable Spell spell, @Nullable Consumer<WSpellIconButton> onClick) {
        this(spell);
        this.setOnClick(onClick);
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (this.backgroundPainter != null) this.backgroundPainter.paintBackground(context, x, y, this);

        if (this.spell != null) this.spell.getIcon().paint(context, x, y, this.getSize());
        else this.fallBackIcon.paint(context, x, y, this.getSize());

        if (this.isHovered() || this.isFocused()) { // Draws an outline.
            int s = this.getSize() + 1;
            int x2 = x - 1;
            int y2 = y - 1;
            ScreenDrawing.coloredRect(context, x2, y2, s, 1, FOCUS_AND_HOVER_OUTLINE_COLOR);
            ScreenDrawing.coloredRect(context, x2, y2, 1, s, FOCUS_AND_HOVER_OUTLINE_COLOR);
            ScreenDrawing.coloredRect(context, x2 + s, y2, 1, s, FOCUS_AND_HOVER_OUTLINE_COLOR);
            ScreenDrawing.coloredRect(context, x2, y2 + s, s, 1, FOCUS_AND_HOVER_OUTLINE_COLOR);
        }
    }

    @Override
    public void addTooltip(TooltipBuilder information) {
        if (this.spell == null) {
            if (!this.fallBackTooltip.toString().equals("empty")) information.add(this.fallBackTooltip);
            return;
        }

        information.add(this.spell.getName());
        Text description = this.spell.getDescription();
        if (!description.toString().equals("empty")) {
            information.add(description.copy().setStyle(description.getStyle().withParent(DESCRIPTION_FALLBACK_STYLE)));
        }
    }

    @Override
    public InputResult onClick(int x, int y, int button) {
        super.onClick(x, y, button);

        if (isWithinBounds(x, y)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            if (this.onClick != null) onClick.accept(this);
            return InputResult.PROCESSED;
        }
        return InputResult.IGNORED;
    }

    @Override
    public InputResult onKeyPressed(int ch, int key, int modifiers) {
        if (isActivationKey(ch)) {
            this.onClick(0, 0, 0);
            return InputResult.PROCESSED;
        }
        return InputResult.IGNORED;
    }

    public @Nullable Consumer<WSpellIconButton> getOnClick() {
        return onClick;
    }

    public WSpellIconButton setOnClick(@Nullable Consumer<WSpellIconButton> onClick) {
        this.onClick = onClick;
        return this;
    }

    public @Nullable Spell getSpell() {
        return this.spell;
    }

    public WSpellIconButton setSpell(@Nullable Spell spell) {
        this.spell = spell;
        return this;
    }

    public ItemIcon getFallBackIcon() {
        return this.fallBackIcon;
    }

    public WSpellIconButton setFallBackIcon(ItemIcon icon) {
        this.fallBackIcon = icon;
        return this;
    }

    public Text getFallBackTooltip() {
        return this.fallBackTooltip;
    }

    public WSpellIconButton setFallBackTooltip(Text tooltip) {
        this.fallBackTooltip = tooltip;
        return this;
    }

    /**
     * The width and height must always be equal, as this widget is rectangular.
     */
    public int getSize() {
        return this.width;
    }

    public WSpellIconButton setSize(int size) {
        this.setSize(size, 0);
        return this;
    }

    /**
     * The width and height must always be equal, as this widget is rectangular.
     */
    @Override
    public void setSize(int size, int ignored) {
        super.setSize(size, size);
    }

    public @Nullable BackgroundPainter getBackgroundPainter() {
        return this.backgroundPainter;
    }

    public WSpellIconButton setBackgroundPainter(@Nullable BackgroundPainter painter) {
        this.backgroundPainter = painter;
        return this;
    }

    @Override
    public void addNarrations(NarrationMessageBuilder builder) {
        if (this.spell != null) {
            builder.put(NarrationPart.TITLE, ClickableWidget.getNarrationMessage(this.spell.getName()));
        } else {
            builder.put(NarrationPart.TITLE, ClickableWidget.getNarrationMessage(this.fallBackTooltip));
        }

        if (isFocused()) builder.put(NarrationPart.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_FOCUSED);
        else if (isHovered()) builder.put(NarrationPart.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_HOVERED);
    }
}
