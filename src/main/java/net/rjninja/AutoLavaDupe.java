package net.rjninja;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLavaDupe implements ModInitializer {
	public static final String MOD_ID = "autolavadupe";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("AutoLavaDupe loaded — made with love <3 by RJNinja9 for the Glorious Imperials.");
	}
}