package net.agent59.stp.client.render;

import net.agent59.stp.Main;
import net.agent59.stp.client.render.entity.RayEntityRenderer;
import net.agent59.stp.client.render.entity.model.RayEntityModel;
import net.agent59.stp.entity.ModEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModEntityRenderer {

    public static final EntityModelLayer RAY_RECTANGLE_MODEL_LAYER = new EntityModelLayer(new Identifier(Main.MOD_ID, "ray_rectangle"), "main");
    public static final EntityModelLayer RAY_WALL_MODEL_LAYER = new EntityModelLayer(new Identifier(Main.MOD_ID, "ray_wall"), "main");
    public static final EntityModelLayer RAY_CUBE_MODEL_LAYER = new EntityModelLayer(new Identifier(Main.MOD_ID, "ray_cube"), "main");

    public static void registerModEntityRenderers() {
        Main.LOGGER.info("Registering Mod Entity Renderers for " + Main.MOD_ID);

        EntityModelLayerRegistry.registerModelLayer(RAY_RECTANGLE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.RAY));
        EntityModelLayerRegistry.registerModelLayer(RAY_WALL_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.WALL));
        EntityModelLayerRegistry.registerModelLayer(RAY_CUBE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.CUBE));

        EntityRendererRegistry.register(ModEntities.STUPEFY_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0F, 0F, 0.2F, 15));
        EntityRendererRegistry.register(ModEntities.EXPELLIARMUS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0F, 0F, 0.4F, 15));
        EntityRendererRegistry.register(ModEntities.PETRIFICUS_TOTALUS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 0.8F, 0.8F, 1F, 0.4F, 15));
        EntityRendererRegistry.register(ModEntities.ALARTE_ASCENDARE_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 1F, 1F, 0.1F, 12));
        EntityRendererRegistry.register(ModEntities.MELOFORS_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0.5F, 0F, 0.4F, 15));
        EntityRendererRegistry.register(ModEntities.FLIPENDO_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 0.7F, 0.7F, 1F, 0.4F, 15));
        EntityRendererRegistry.register(ModEntities.PROTEGO_WALL,
                (context) -> new RayEntityRenderer(context, RAY_WALL_MODEL_LAYER, 211, 211, 211, 0.2F, -1));
        EntityRendererRegistry.register(ModEntities.LUMOS_ORB,
                (context) -> new RayEntityRenderer(context, RAY_CUBE_MODEL_LAYER, 0, 0, 0, 0F, 15));
    }
}
