package fuzs.enchantinginfuser.api.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface EnchantStatsProvider {
    String getSourceNamespace();

    default int getPriority() {
        return 10;
    }

    String[] getScalingNamespaces();

    default int getMaximumEnchantPower() {
        return -1;
    }

    float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos);

    default float getMaximumEnchantPowerScale(BlockState state, Level level, BlockPos pos) {
        return 1.0F;
    }

    default float getMaximumCostMultiplier() {
        return 1.0F;
    }

    Enchantment.Rarity getRarity(Enchantment enchantment);

    boolean isCompatibleWith(Enchantment enchantment, Enchantment other);

    int getMinLevel(Enchantment enchantment);

    int getMaxLevel(Enchantment enchantment);

    int getMinCost(Enchantment enchantment, int level);

    int getMaxCost(Enchantment enchantment, int level);

    boolean isTreasureOnly(Enchantment enchantment);

    boolean isCurse(Enchantment enchantment);

    boolean isTradeable(Enchantment enchantment);

    boolean isDiscoverable(Enchantment enchantment);
}
