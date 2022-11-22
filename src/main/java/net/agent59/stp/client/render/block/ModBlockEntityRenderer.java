package net.agent59.stp.client.render.block;

import net.agent59.stp.Main;
import net.agent59.stp.block.entity.ModBlockEntities;
import net.agent59.stp.client.render.block.entity.WandMakerBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

@Environment(value=EnvType.CLIENT)
public class ModBlockEntityRenderer {

    public static void registerModBlockEntityRenderers() {
        Main.LOGGER.info("Registering Mod Block Entity Renderers for " + Main.MOD_ID);

        BlockEntityRendererRegistry.register(ModBlockEntities.WANDMAKER_BLOCK_ENTITY, WandMakerBlockEntityRenderer::new);
    }
}
