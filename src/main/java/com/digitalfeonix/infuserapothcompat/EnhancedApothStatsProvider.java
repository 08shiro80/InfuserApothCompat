package com.digitalfeonix.infuserapothcompat;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
import fuzs.enchantinginfuser.api.world.item.enchantment.EnchantStatsProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class EnhancedApothStatsProvider implements EnchantStatsProvider {
    public static final EnchantStatsProvider INSTANCE = new EnhancedApothStatsProvider();

    private EnhancedApothStatsProvider() {}

    @Override
    public String getSourceNamespace() {
        return "infuserapothcompat";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String[] getScalingNamespaces() {
        return new String[]{"minecraft", "apotheosis"};
    }

    @Override
    public int getMaximumEnchantPower() {
        return 50;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos) {
        return EnchantingStatRegistry.getEterna(state, level, pos);
    }

    @Override
    public float getMaximumEnchantPowerScale(BlockState state, Level level, BlockPos pos) {
        float maxEterna = EnchantingStatRegistry.getMaxEterna(state, level, pos);
        return maxEterna / 15.0F;
    }

    @Override
    public float getMaximumCostMultiplier() {
        return 2.5F;
    }

    @Override
    public Enchantment.Rarity getRarity(Enchantment enchantment) {
        return enchantment.getRarity();
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment, Enchantment other) {
        return enchantment.isCompatibleWith(other);
    }

    @Override
    public int getMinLevel(Enchantment enchantment) {
        return enchantment.getMinLevel();
    }

    @Override
    public int getMaxLevel(Enchantment enchantment) {
        return EnchModule.getEnchInfo(enchantment).getMaxLevel();
    }

    @Override
    public int getMinCost(Enchantment enchantment, int level) {
        return EnchModule.getEnchInfo(enchantment).getMinPower(level);
    }

    @Override
    public int getMaxCost(Enchantment enchantment, int level) {
        return EnchModule.getEnchInfo(enchantment).getMaxPower(level);
    }

    @Override
    public boolean isTreasureOnly(Enchantment enchantment) {
        return EnchModule.getEnchInfo(enchantment).isTreasure();
    }

    @Override
    public boolean isCurse(Enchantment enchantment) {
        return enchantment.isCurse();
    }

    @Override
    public boolean isTradeable(Enchantment enchantment) {
        return EnchModule.getEnchInfo(enchantment).isTradeable();
    }

    @Override
    public boolean isDiscoverable(Enchantment enchantment) {
        return EnchModule.getEnchInfo(enchantment).isDiscoverable();
    }
}
