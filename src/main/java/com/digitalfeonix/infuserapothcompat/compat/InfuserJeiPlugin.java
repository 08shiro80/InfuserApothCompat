package com.digitalfeonix.infuserapothcompat.compat;

import com.digitalfeonix.infuserapothcompat.InfuserApothCompat;
import dev.shadowsoffire.apothic_enchanting.compat.InfusionRecipeCategory;
import fuzs.enchantinginfuser.common.init.ModRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class InfuserJeiPlugin implements IModPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(InfuserApothCompat.MODID, "jei");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Advertise both infuser tiers as valid stations for Apotheosis' infusion recipes, since the
        // tick handler performs those infusions in the infuser just like the Apotheosis table.
        registration.addRecipeCatalyst(ModRegistry.INFUSER_ITEM.value(), InfusionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(ModRegistry.ADVANCED_INFUSER_ITEM.value(), InfusionRecipeCategory.TYPE);
    }
}
