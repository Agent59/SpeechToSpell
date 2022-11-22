package net.agent59.stp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.agent59.stp.screen.WandMakerScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WandMakerScreen extends CottonInventoryScreen<WandMakerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("speech_to_spell:textures/gui/wand_maker.png");
    public WandMakerScreen(WandMakerScreenHandler gui, PlayerInventory inventory, Text title) {
        super(gui, inventory.player, title);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(matrices, partialTicks, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, partialTicks);

    }

    @Override
    protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = this.x + 81;
        int y = this.y + 40;
        this.drawTexture(matrices, x + 2, y, 0, 0, 8, 29);
        float c = this.handler.getCraftingProgress();
        this.drawTexture(matrices, x, (int) (y + (29 - c * 29)), 10, (int) (29 - c * 29), 14, (int) (c * 29));
    }
}
