package com.example.anvilenchantment;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnvilEnchantmentMod implements ModInitializer {
    public static final String MOD_ID = "anvilenchantment";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("AnvilEnchantmentMod has been initialized!");
        LOGGER.info("Cross-item-type enchanting is now enabled!");
    }
}
