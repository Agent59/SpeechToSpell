package net.agent59.stp.client.render.entity.model;

import net.agent59.stp.entity.custom.RayEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.Entity;

public class RayEntityModel<T extends Entity> extends ShulkerBulletEntityModel<RayEntity> {

    private static final String MAIN = "main";

    public RayEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(MAIN, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -1.0F, 8.0F, 2.0F, 2.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData getTexturedModelData2(Shape shape) {

        switch (shape) {
            default: // default is rectangle
                ModelData modelData = new ModelData();
                ModelPartData modelPartData = modelData.getRoot();
                modelPartData.addChild(MAIN, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -1.0F, 8.0F, 2.0F, 2.0F), ModelTransform.NONE);
                return TexturedModelData.of(modelData, 64, 32);
        }
    }

    public enum Shape {
        RECTANGLE
    }
}
