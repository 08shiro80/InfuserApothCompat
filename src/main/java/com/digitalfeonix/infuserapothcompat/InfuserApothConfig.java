package com.digitalfeonix.infuserapothcompat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class InfuserApothConfig {
    public static final ModConfigSpec CONFIG_SPEC;
    public static final ModConfigSpec.BooleanValue ALLOW_CURSED_ENCHANTMENTS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("general");
        ALLOW_CURSED_ENCHANTMENTS = builder
            .comment("Allow cursed enchantments in the Enchanting Infuser without requiring a Treasure Shelf")
            .define("allowCursedEnchantments", false);
        builder.pop();
        CONFIG_SPEC = builder.build();
    }
}
