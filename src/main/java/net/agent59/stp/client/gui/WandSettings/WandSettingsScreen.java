package net.agent59.stp.client.gui.WandSettings;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.agent59.stp.client.CustomKeybindings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WandSettingsScreen extends CottonClientScreen {
    public WandSettingsScreen(GuiDescription description) {
        super(description);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert this.client != null;
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode) || CustomKeybindings.WANDSETTINGS.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
