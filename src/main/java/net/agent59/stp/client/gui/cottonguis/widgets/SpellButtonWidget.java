package net.agent59.stp.client.gui.cottonguis.widgets;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpellButtonWidget extends WPlainPanel {
    public WButton button;

    public SpellButtonWidget() {
        button = new WButton();
        this.add(button, 1, 2, 150, 20);
    }
}