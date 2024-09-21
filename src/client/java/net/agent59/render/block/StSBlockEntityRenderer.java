package net.agent59.render.block;

import net.agent59.StSMain;
import net.agent59.block.entity.StSBlockEntities;
import net.agent59.render.block.entity.WandMakerBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class StSBlockEntityRenderer {

    public static void registerModBlockEntityRenderers() {
        StSMain.LOGGER.info("Registering Mod Block Entity Renderers for " + StSMain.MOD_NAME);

        BlockEntityRendererFactories.register(StSBlockEntities.WANDMAKER_BLOCK_ENTITY, WandMakerBlockEntityRenderer::new);
    }
}
