package net.agent59.stp.client.gui.cottonguis;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.agent59.stp.Main;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.NumberUtils;

@Environment(EnvType.CLIENT)
public class PortusGui extends LightweightGuiDescription {

    public PortusGui(ClientPlayerEntity player) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(256, 230);
        root.setInsets(Insets.ROOT_PANEL);

        WLabel guiLabel = new WLabel(Text.translatable("Portus target location"));
        root.add(guiLabel, 0, 0);


        WLabel xLabel = new WLabel(Text.translatable("X:"));
        root.add(xLabel, 0  , 1);
        WTextField xTextField = new WTextField();
        root.add(xTextField, 0, 2, 13, 2);

        WLabel yLabel = new WLabel(Text.translatable("Y:"));
        root.add(yLabel, 0, 4);
        WTextField yTextField = new WTextField();
        root.add(yTextField, 0, 5, 13, 2);

        WLabel zLabel = new WLabel(Text.translatable("Z:"));
        root.add(zLabel, 0, 7);
        WTextField zTextField = new WTextField();
        root.add(zTextField, 0, 8, 13, 2);


        WLabel errorLabel = new WLabel(Text.translatable(""));
        root.add(errorLabel, 0, 10);

        WButton doneButton = new WButton(Text.translatable("Done"));
        doneButton.setOnClick(() -> {
            String errorMessage = "";
            boolean isIntConvertible = true;

            if (!(NumberUtils.isParsable(xTextField.getText())) || xTextField.getText().contains(".")) {
                errorMessage = errorMessage + " invalid X";
                isIntConvertible = false;
            }
            if (!(NumberUtils.isParsable(yTextField.getText()))  || yTextField.getText().contains(".")) {
                errorMessage = errorMessage + " invalid Y";
                isIntConvertible = false;
            }
            if (!(NumberUtils.isParsable(zTextField.getText()))  || zTextField.getText().contains(".")) {
                errorMessage = errorMessage + " invalid Z";
                isIntConvertible = false;
            }
            errorLabel.setText(Text.translatable(errorMessage));

            if (isIntConvertible) {
                try {
                    int x = Integer.parseInt(xTextField.getText());
                    int y = Integer.parseInt(yTextField.getText());
                    int z = Integer.parseInt(zTextField.getText());
                    int[] coords = {x, y, z};
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeIntArray(coords);
                    ClientPlayNetworking.send(new Identifier(Main.MOD_ID, "portus_entry_coordinates"), buf);
                    player.closeHandledScreen();
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        root.add(doneButton, 8, 10, 5, 2);
    }
}
