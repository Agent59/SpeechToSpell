package net.agent59.stp.client.render.block.entity;

import net.agent59.stp.block.entity.WandMakerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class WandMakerBlockEntityRenderer implements BlockEntityRenderer<WandMakerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public WandMakerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(WandMakerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack wand = entity.getStack(WandMakerBlockEntity.RESULT_SLOT);
        if (wand != ItemStack.EMPTY) {
            matrices.push();

            matrices.translate(0.5, 1.25, 0.5);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4));
            int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
            this.itemRenderer.renderItem(wand, ModelTransformation.Mode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);

            matrices.pop();
        }
    }
}
