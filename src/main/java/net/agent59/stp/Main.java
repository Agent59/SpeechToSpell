package net.agent59.stp;

import net.agent59.stp.block.ModBlocks;
import net.agent59.stp.item.ModItems;
import net.agent59.stp.util.FileHandler;
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
		Network.registerNetworkPackets();

		LOGGER.info(Main.MOD_NAME + " mod in: " + FileHandler.PROJECT_DIRECTORY);
	}
}
