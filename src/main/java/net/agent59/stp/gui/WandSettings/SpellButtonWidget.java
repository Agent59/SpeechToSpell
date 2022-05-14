package net.agent59.stp.gui.WandSettings;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;

public class SpellButtonWidget extends WPlainPanel {
    WButton button;

    public SpellButtonWidget() {
        button = new WButton();
        this.add(button, 1, 2, 150, 20);
    }
}