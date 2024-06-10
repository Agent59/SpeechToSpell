package net.agent59;

import net.agent59.block.ModBlocks;
import net.agent59.block.entity.ModBlockEntities;
import net.agent59.entity.ModEntities;
import net.agent59.item.ModItems;
import net.agent59.recipe.ModRecipes;
import net.agent59.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO README.md
public class Main implements ModInitializer {

	public static final String MOD_ID = "speech_to_spell";
	public static final String MOD_NAME = "SpeechToSpell"; // used for filenames

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerModBlockEntities();
		ModScreenHandlers.registerModScreenHandlers();
		ModRecipes.registerModRecipes();
		Network.registerNetworkPackets();
		ModEntities.registerModEntities();
		ModParticles.registerModParticles();
	}
}
