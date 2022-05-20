package net.agent59.stp.client.gui.WandSettings;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WandSettingsScreen extends CottonClientScreen {
    public WandSettingsScreen(GuiDescription description) {
        super(description);
    }
}
