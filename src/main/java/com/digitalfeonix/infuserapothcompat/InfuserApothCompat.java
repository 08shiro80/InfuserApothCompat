package com.digitalfeonix.infuserapothcompat;

import fuzs.enchantinginfuser.world.item.enchantment.EnchantingBehavior;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InfuserApothCompat.MODID)
public class InfuserApothCompat {
    public static final String MODID = "infuserapothcompat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public InfuserApothCompat(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("apothic_enchanting") && ModList.get().isLoaded("enchantinginfuser")) {
                try {
                    EnchantingBehavior.set(EnhancedApothEnchantingBehavior.INSTANCE);
                    LOGGER.info("InfuserApothCompat: Enhanced Apotheosis integration enabled");
                } catch (Exception e) {
                    LOGGER.error("InfuserApothCompat: Failed to register enhanced enchanting behavior", e);
                }
            }
        });
    }
}
