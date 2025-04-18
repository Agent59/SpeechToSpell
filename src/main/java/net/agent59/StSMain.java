package net.agent59;

import net.agent59.block.StSBlocks;
import net.agent59.block.entity.StSBlockEntities;
import net.agent59.command.StSGameRules;
import net.agent59.entity.StSEntities;
import net.agent59.item.StSItems;
import net.agent59.network.StSNetwork;
import net.agent59.recipe.StSRecipes;
import net.agent59.screen.StSScreenHandlers;
import net.agent59.spell.SpellManager;
import net.agent59.spell.SpellTypes;
import net.agent59.spell.component.SpellStateComponentTypes;
import net.agent59.spell_school.SpellSchoolManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO README.md
public class StSMain implements ModInitializer {

	public static final String MOD_ID = "speech_to_spell";
	public static final String MOD_NAME = "SpeechToSpell";

	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {

		StSItems.registerModItems();
		StSBlocks.registerModBlocks();
		StSBlockEntities.registerModBlockEntities();
		StSScreenHandlers.registerModScreenHandlers();
		StSRecipes.registerModRecipes();
		StSNetwork.registerNetworkPackets();
		StSEntities.registerModEntities();
		StSParticles.registerModParticles();

		StSGameRules.initialize();

		SpellTypes.initialize();
		SpellStateComponentTypes.initialize();

		StSEventListeners.registerListeners();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SpellSchoolManager());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SpellManager());
	}

	/**
	 * @param path The path the returned identifier should have.
	 * @return An identifier with the namespace of the SpeechToSpell mod.
	 */
	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
