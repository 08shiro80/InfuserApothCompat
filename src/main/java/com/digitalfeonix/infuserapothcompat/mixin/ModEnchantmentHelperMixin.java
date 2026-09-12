package com.digitalfeonix.infuserapothcompat.mixin;

import com.digitalfeonix.infuserapothcompat.InfuserApothConfig;
import com.digitalfeonix.infuserapothcompat.TreasureShelfHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

// Enchanting Infuser only offers enchantments from its own candidate tag; enchantments that other mods
// keep out of #minecraft:in_enchanting_table (e.g. Corail Tombstone / Ars soulbound, Ars Nouveau reactive)
// are therefore never shown, even on items that support them. Restore the pre-1.21 behaviour: scan the whole
// enchantment registry and offer everything the item actually supports (isPrimaryItemFor), still gating
// treasure enchantments behind a treasure shelf, curses behind the config toggle, and honoring the
// treasure-shelf blacklist.
@Pseudo
@Mixin(targets = "fuzs.enchantinginfuser.common.util.ModEnchantmentHelper", remap = false)
public class ModEnchantmentHelperMixin {

    @Inject(method = "getEnchantmentsForItem", at = @At("RETURN"), cancellable = true)
    private static void infuserapothcompat$offerAllSupported(
            RegistryAccess registryAccess, ItemStack itemStack, TagKey<Enchantment> availableEnchantments,
            boolean primaryOnly, CallbackInfoReturnable<Collection<Holder<Enchantment>>> cir) {

        boolean allowTreasure = TreasureShelfHelper.treasureShelfPresent;
        boolean allowCurses = InfuserApothConfig.ALLOW_CURSED_ENCHANTMENTS.get();
        Set<Holder<Enchantment>> blacklist = TreasureShelfHelper.blacklistedEnchantments;

        LinkedHashSet<Holder<Enchantment>> merged = new LinkedHashSet<>(cir.getReturnValue());
        registryAccess.lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(holder -> {
            if (!allowCurses && holder.is(EnchantmentTags.CURSE)) return;
            if (!allowTreasure && holder.is(EnchantmentTags.TREASURE)) return;
            if (itemStack.isPrimaryItemFor(holder)) merged.add(holder);
        });
        if (!blacklist.isEmpty()) merged.removeAll(blacklist);

        cir.setReturnValue(new ArrayList<>(merged));
    }
}
