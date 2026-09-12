package com.digitalfeonix.infuserapothcompat.mixin;

import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// getRelativePowerForLevel divides by (maxPower - minPower); when an item has only a single applicable
// enchantment (or several with identical power) that denominator is 0 and Fraction.getFraction throws,
// which aborts menu setup so the infuser stops opening. Guard the degenerate case.
@Pseudo
@Mixin(targets = "fuzs.enchantinginfuser.common.util.EnchantmentPowerHelper", remap = false)
public class EnchantmentPowerHelperMixin {

    @Redirect(
        method = "getRelativePowerForLevel",
        at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/math/Fraction;getFraction(II)Lorg/apache/commons/lang3/math/Fraction;")
    )
    private static Fraction infuserapothcompat$guardZeroDenominator(int numerator, int denominator) {
        return denominator == 0 ? Fraction.ZERO : Fraction.getFraction(numerator, denominator);
    }
}
