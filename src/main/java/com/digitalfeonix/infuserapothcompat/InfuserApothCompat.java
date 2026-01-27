package com.digitalfeonix.infuserapothcompat;

import fuzs.enchantinginfuser.api.EnchantingInfuserAPI;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InfuserApothCompat.MODID)
public class InfuserApothCompat {
    public static final String MODID = "infuserapothcompat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public InfuserApothCompat() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("apotheosis") && ModList.get().isLoaded("enchantinginfuser")) {
                try {
                    EnchantingInfuserAPI.setEnchantStatsProvider(EnhancedApothStatsProvider.INSTANCE);
                    LOGGER.info("InfuserApothCompat: Enhanced Apotheosis integration enabled");
                } catch (Exception e) {
                    LOGGER.error("InfuserApothCompat: Failed to register enhanced stats provider", e);
                }
            }
        });
    }
}
