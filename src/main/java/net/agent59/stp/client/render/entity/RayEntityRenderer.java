package net.agent59.stp.client.render.entity;

import net.agent59.stp.client.render.ModEntityRenderer;
import net.agent59.stp.client.render.entity.model.RayEntityModel;
import net.agent59.stp.entity.custom.RayEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;


public class RayEntityRenderer extends ShulkerBulletEntityRenderer {

    private static final Identifier TEXTURE = new Identifier("textures/entity/shulker/spark.png");
    private final RayEntityModel<RayEntity> model;

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public RayEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer modelLayer, float red, float green, float blue, float alpha) {
        super(context);
        this.model = new RayEntityModel<>(context.getPart(modelLayer));
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public void render(ShulkerBulletEntity shulkerBulletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, red, green, blue, alpha);
    }

    @Override
    public Identifier getTexture(ShulkerBulletEntity shulkerBulletEntity) {
        return TEXTURE;
    }
}
