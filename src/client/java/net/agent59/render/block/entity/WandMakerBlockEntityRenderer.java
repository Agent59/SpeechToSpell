package net.agent59.render.block.entity;

import net.agent59.block.entity.WandMakerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class WandMakerBlockEntityRenderer implements BlockEntityRenderer<WandMakerBlockEntity> {
    private final ItemRenderer itemRenderer;

    public WandMakerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(WandMakerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack wand = entity.getStack(WandMakerBlockEntity.RESULT_SLOT);
        if (wand != ItemStack.EMPTY) {
            World world = entity.getWorld();

            matrices.push();
            matrices.translate(0.5, 1.25, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((world.getTime() + tickDelta) * 4));

            int light_above = WorldRenderer.getLightmapCoordinates(world, entity.getPos().up());
            this.itemRenderer.renderItem(wand, ModelTransformationMode.GROUND, light_above, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, (int) entity.getPos().asLong());

            matrices.pop();
        }
    }
}
