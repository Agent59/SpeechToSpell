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

    public static void registerModEntityRenderers() {
        Main.LOGGER.info("Registering Mod Entity Renderers for " + Main.MOD_ID);

        EntityModelLayerRegistry.registerModelLayer(RAY_RECTANGLE_MODEL_LAYER, () -> RayEntityModel.getTexturedModelData(RayEntityModel.Shape.RECTANGLE));

        EntityRendererRegistry.register(ModEntities.RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 1F, 1F, 0.2F));
        EntityRendererRegistry.register(ModEntities.STUPEFY_RAY,
                (context) -> new RayEntityRenderer(context, RAY_RECTANGLE_MODEL_LAYER, 1F, 0F, 0F, 0.2F));
    }
}
