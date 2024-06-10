package net.agent59.render.entity;

import net.agent59.Main;
import net.agent59.entity.custom.RayEntity;
import net.agent59.render.entity.model.RayEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RayEntityRenderer extends EntityRenderer<RayEntity> {

    private static final Identifier TEXTURE = new Identifier(Main.MOD_ID, "textures/entity/white_plane.png");
    private final RayEntityModel<RayEntity> model;

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final int blockLight; // set to -1 for default

    public RayEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer modelLayer, float red, float green, float blue, float alpha, int blockLight) {
        super(context);
        this.model = new RayEntityModel<>(context.getPart(modelLayer));
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.blockLight = blockLight;
    }

    @Override
    public void render(RayEntity rayEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        float h = MathHelper.lerpAngleDegrees(rayEntity.prevYaw, rayEntity.getYaw(), g);
        float j = MathHelper.lerp(g, rayEntity.prevPitch, rayEntity.getPitch());
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(getTexture(rayEntity)));
        this.model.setRectangleRotation(h, j);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(RayEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(RayEntity rayEntity, BlockPos blockPos) {
        return this.blockLight == -1 ? super.getBlockLight(rayEntity, blockPos) : this.blockLight;
    }
}
