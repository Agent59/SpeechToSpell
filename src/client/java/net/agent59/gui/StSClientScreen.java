package net.agent59.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Nullable;

/**
 * Makes the {@link CottonClientScreen} a little more configurable.
 */
public class StSClientScreen extends CottonClientScreen {
    private final boolean pauses;
    private final @Nullable KeyBinding openedByKey;

    public StSClientScreen(GuiDescription description, boolean pauses, @Nullable KeyBinding openedByKey) {
        super(description);
        this.pauses = pauses;
        this.openedByKey = openedByKey;
    }

    public StSClientScreen(GuiDescription description) {
        this(description, false, null);
    }

    /**
     * Closes the screen if the inventory key is pressed or the one that opened the gui.<br>
     * Otherwise, just passes on the key press to super.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert this.client != null;
        boolean bl = openedByKey != null && openedByKey.matchesKey(keyCode, scanCode);
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode) || bl) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return pauses;
    }
}
