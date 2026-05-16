package com.github.toripan0310.nametags;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameTags implements ModInitializer {
	public static final String MOD_ID = "nametags";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Player Name Tag Glowing!!!!!");
	}
}