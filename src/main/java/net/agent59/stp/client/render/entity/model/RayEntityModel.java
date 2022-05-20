package net.agent59.stp.client.render.entity.model;

import net.agent59.stp.entity.custom.RayEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class RayEntityModel<T extends Entity> extends ShulkerBulletEntityModel<RayEntity> {

    private static final String MAIN = "main";
    private final ModelPart root;
    private final ModelPart rectangle;

    public RayEntityModel(ModelPart root) {
        super(root);
        this.root = root;
        this.rectangle = root.getChild(MAIN);
    }

    public static TexturedModelData getTexturedModelData(Shape shape) {

        switch (shape) {
            default: // default is rectangle
                ModelData modelData = new ModelData();
                ModelPartData modelPartData = modelData.getRoot();
                modelPartData.addChild(MAIN, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -4.0F, -4.0F, 2.0F, 2.0F, 8.0F), ModelTransform.NONE);
                return TexturedModelData.of(modelData, 64, 32);
        }
    }

    public void setRectangleRotation(float yaw, float pitch) {
        this.rectangle.yaw = yaw * 0.017453292F;
        this.rectangle.pitch = pitch * 0.017453292F;
    }

    public enum Shape {
        RECTANGLE
    }
}
