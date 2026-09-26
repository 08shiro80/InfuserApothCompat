package com.digitalfeonix.infuserapothcompat.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Enchanting Infuser only fills the enchantment list from a server sync, never when the screen is rebuilt.
// After viewing a JEI recipe (U) and returning, the screen is re-initialized with a fresh, empty list but no
// new sync arrives, so every enchantment vanishes until the GUI is reopened. Repopulate from the (unchanged)
// menu state on init.
@Mixin(targets = "fuzs.enchantinginfuser.common.client.gui.screens.inventory.InfuserScreen", remap = false)
public abstract class InfuserScreenMixin {

    @Shadow
    public abstract void refreshSearchResults();

    @Inject(method = "init", at = @At("RETURN"), remap = false)
    private void infuserapothcompat$refreshEnchantsOnInit(CallbackInfo ci) {
        this.refreshSearchResults();
    }
}
