package com.digitalfeonix.infuserapothcompat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import fuzs.enchantinginfuser.common.world.item.enchantment.EnchantingBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InfuserApothCompat.MODID)
public class InfuserApothCompat {
    public static final String MODID = "infuserapothcompat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static boolean enchModuleEnabled = false;

    public InfuserApothCompat(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, InfuserApothConfig.CONFIG_SPEC);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("apothic_enchanting") && ModList.get().isLoaded("enchantinginfuser")) {
                // Enchanting Infuser 26.1.x ships its Apotheosis integration commented out, so the
                // infuser ignores Apotheosis eterna and only counts vanilla bookshelves. Install the
                // eterna-aware behavior through EI's own extension point to restore power/cost scaling.
                EnchantingBehavior.set(ApotheosisEnchantingBehavior.INSTANCE);
                enchModuleEnabled = true;
                LOGGER.info("InfuserApothCompat: Apotheosis integration enabled");
            }
        });
    }
}
