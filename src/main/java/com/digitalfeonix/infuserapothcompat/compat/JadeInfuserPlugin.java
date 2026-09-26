package com.digitalfeonix.infuserapothcompat.compat;

import com.digitalfeonix.infuserapothcompat.InfuserApothCompat;
import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadeInfuserPlugin implements IWailaPlugin, IBlockComponentProvider {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(InfuserApothCompat.MODID, "infuser_stats");
    private static Class<?> infuserBlockClass = null;

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        try {
            infuserBlockClass = Class.forName("fuzs.enchantinginfuser.common.world.level.block.InfuserBlock");
            registration.registerBlockComponent(this, infuserBlockClass.asSubclass(Block.class));
            InfuserApothCompat.LOGGER.info("JadeInfuserPlugin: Registered for InfuserBlock class");
        } catch (ClassNotFoundException e) {
            InfuserApothCompat.LOGGER.warn("JadeInfuserPlugin: InfuserBlock class not found, registering for all blocks");
            registration.registerBlockComponent(this, Block.class);
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!InfuserApothCompat.enchModuleEnabled) {
            return;
        }

        Block block = accessor.getBlockState().getBlock();

        if (!isInfuserBlock(block)) {
            return;
        }

        Level level = accessor.getLevel();
        BlockPos tablePos = accessor.getPosition();

        float totalEterna = 0;
        float totalQuanta = 0;
        float totalArcana = 0;
        float maxEterna = 15.0F;

        try {
            for (BlockPos offset : net.minecraft.world.level.block.EnchantingTableBlock.BOOKSHELF_OFFSETS) {
                BlockPos betweenPos = tablePos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
                BlockState betweenState = level.getBlockState(betweenPos);
                if (betweenState.isSolidRender() && !betweenState.is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
                    continue;
                }
                BlockPos shelfPos = tablePos.offset(offset);
                BlockState shelfState = level.getBlockState(shelfPos);
                float eterna = EnchantingStatRegistry.getEterna(shelfState, level, shelfPos);
                float quanta = 0.0F;
                float arcana = 0.0F;
                float blockMaxEterna = 0.0F;
                // getQuanta/getArcana/getMaxEterna cast non-datapack blocks to EnchantmentStatBlock; a
                // vanilla bookshelf contributes eterna only, so skip the rest instead of aborting.
                try {
                    quanta = EnchantingStatRegistry.getQuanta(shelfState, level, shelfPos);
                    arcana = EnchantingStatRegistry.getArcana(shelfState, level, shelfPos);
                    blockMaxEterna = EnchantingStatRegistry.getMaxEterna(shelfState, level, shelfPos);
                } catch (ClassCastException ignored) {
                }
                if (eterna != 0 || quanta != 0 || arcana != 0) {
                    totalEterna += eterna;
                    totalQuanta += quanta;
                    totalArcana += arcana;
                    if (blockMaxEterna > maxEterna) {
                        maxEterna = blockMaxEterna;
                    }
                }
            }
        } catch (Exception e) {
            InfuserApothCompat.LOGGER.error("JadeInfuserPlugin: Error calculating stats", e);
        }

        totalEterna = Math.min(totalEterna, maxEterna);

        tooltip.add(Component.translatable("tooltip.infuserapothcompat.eterna")
            .append(": ")
            .append(Component.literal(String.format("%.1f / %.1f", totalEterna, maxEterna))
                .withStyle(ChatFormatting.GREEN)));

        if (totalQuanta != 0) {
            tooltip.add(Component.translatable("tooltip.infuserapothcompat.quanta")
                .append(": ")
                .append(Component.literal(String.format("%.1f%%", totalQuanta))
                    .withStyle(ChatFormatting.RED)));
        }

        if (totalArcana != 0) {
            tooltip.add(Component.translatable("tooltip.infuserapothcompat.arcana")
                .append(": ")
                .append(Component.literal(String.format("%.1f%%", totalArcana))
                    .withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }

    private boolean isInfuserBlock(Block block) {
        if (infuserBlockClass != null) {
            return infuserBlockClass.isInstance(block);
        }
        return block.getClass().getName().contains("InfuserBlock");
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return -300;
    }
}
