package com.digitalfeonix.infuserapothcompat;

import dev.shadowsoffire.apothic_enchanting.api.EnchantmentStatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class TreasureShelfHelper {

    public static boolean treasureShelfPresent = false;
    public static Set<Holder<Enchantment>> blacklistedEnchantments = Set.of();

    public static void scanShelves(Level level, BlockPos infuserPos) {
        boolean treasure = false;
        Set<Holder<Enchantment>> blacklist = new HashSet<>();
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos betweenPos = infuserPos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
            if (!level.getBlockState(betweenPos).isAir()) continue;

            BlockPos shelfPos = infuserPos.offset(offset);
            BlockState state = level.getBlockState(shelfPos);
            if (!(state.getBlock() instanceof EnchantmentStatBlock shelf)) continue;

            if (shelf.allowsTreasure(state, level, shelfPos)) {
                treasure = true;
            }
            blacklist.addAll(shelf.getBlacklistedEnchantments(state, level, shelfPos));
        }
        treasureShelfPresent = treasure;
        blacklistedEnchantments = blacklist.isEmpty() ? Set.of() : blacklist;
    }

    public static void reset() {
        treasureShelfPresent = false;
        blacklistedEnchantments = Set.of();
    }
}
