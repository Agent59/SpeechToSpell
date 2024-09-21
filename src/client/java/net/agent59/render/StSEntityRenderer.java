package net.agent59.render;

import net.agent59.StSMain;
import net.agent59.entity.StSEntities;
import net.agent59.render.entity.RayEntityRenderer;
import net.agent59.render.entity.model.RayEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public class StSEntityRenderer {

    public static final EntityModelLayer RAY_RECTANGLE_MODEL_LAYER = new EntityModelLayer(StSMain.id("ray_rectangle"), "main");
    public static final EntityModelLayer RAY_WALL_MODEL_LAYER = new EntityModelLayer(StSMain.id("ray_wall"), "main");
    public static final EntityModelLayer RAY_CUBE_MODEL_LAYER = new EntityModelLayer(StSMain.id("ray_cube"), "main");
    public static final EntityModelLayer RAY_INVISIBLE_MODEL_LAYER = new EntityModelLayer(StSMain.id("ray_invisible"), "main");

    public static void registerModEntityRenderers() {
        StSMain.LOGGER.info("Registering Mod Entity Renderers for " + StSMain.MOD_NAME);

        EntityModelLayerRegistry.registerModelLayer(RAY_RECTANGLE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.RAY));
        EntityModelLayerRegistry.registerModelLayer(RAY_WALL_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.WALL));
        EntityModelLayerRegistry.registerModelLayer(RAY_CUBE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.CUBE));
        EntityModelLayerRegistry.registerModelLayer(RAY_INVISIBLE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.INVISIBLE));

        EntityRendererRegistry.register(StSEntities.STUPEFY_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0F, 0F, 0.2F, 15));
        EntityRendererRegistry.register(StSEntities.EXPELLIARMUS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0F, 0F, 0.4F, 15));
        EntityRendererRegistry.register(StSEntities.PETRIFICUS_TOTALUS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 0.8F, 0.8F, 1F, 0.4F, 15));
        EntityRendererRegistry.register(StSEntities.ALARTE_ASCENDARE_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 1F, 1F, 0.1F, 12));
        EntityRendererRegistry.register(StSEntities.MELOFORS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0.5F, 0F, 0.4F, 15));
        EntityRendererRegistry.register(StSEntities.FLIPENDO_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 0.7F, 0.7F, 1F, 0.4F, 15));
        EntityRendererRegistry.register(StSEntities.PROTEGO_WALL,
                (context) -> new RayEntityRenderer(context, RAY_WALL_MODEL_LAYER, 211, 211, 211, 0.2F, -1));
        EntityRendererRegistry.register(StSEntities.LUMOS_ORB,
                (context) -> new RayEntityRenderer(context, RAY_INVISIBLE_MODEL_LAYER, 0, 0, 0, 0F, 15));
        EntityRendererRegistry.register(StSEntities.PORTKEY,
                (context) -> new RayEntityRenderer(context, RAY_INVISIBLE_MODEL_LAYER, 0, 0, 0, 0F, 15));
    }
}
