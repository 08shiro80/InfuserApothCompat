package fuzs.enchantinginfuser.api;

import fuzs.enchantinginfuser.api.world.item.enchantment.EnchantStatsProvider;

public class EnchantingInfuserAPI {
    private static EnchantStatsProvider provider;

    public static synchronized boolean setEnchantStatsProvider(EnchantStatsProvider provider) {
        throw new UnsupportedOperationException("Stub");
    }

    public static EnchantStatsProvider getEnchantStatsProvider() {
        throw new UnsupportedOperationException("Stub");
    }
}
