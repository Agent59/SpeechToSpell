package net.agent59.render.block;

import net.agent59.Main;
import net.agent59.block.entity.ModBlockEntities;
import net.agent59.render.block.entity.WandMakerBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class StSBlockEntityRenderer {

    public static void registerModBlockEntityRenderers() {
        Main.LOGGER.info("Registering Mod Block Entity Renderers for " + Main.MOD_NAME);

        BlockEntityRendererFactories.register(ModBlockEntities.WANDMAKER_BLOCK_ENTITY, WandMakerBlockEntityRenderer::new);
    }
}
