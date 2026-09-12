package com.digitalfeonix.infuserapothcompat.compat;

import com.digitalfeonix.infuserapothcompat.InfuserApothCompat;
import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
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
            registration.registerBlockComponent(this, (Class<? extends Block>) infuserBlockClass);
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
                if (!isValidBookShelf(level, tablePos, offset)) {
                    continue;
                }
                BlockPos shelfPos = tablePos.offset(offset);
                BlockState shelfState = level.getBlockState(shelfPos);
                float eterna = EnchantingStatRegistry.getEterna(shelfState, level, shelfPos);
                float quanta = EnchantingStatRegistry.getQuanta(shelfState, level, shelfPos);
                float arcana = EnchantingStatRegistry.getArcana(shelfState, level, shelfPos);
                if (eterna != 0 || quanta != 0 || arcana != 0) {
                    totalEterna += eterna;
                    totalQuanta += quanta;
                    totalArcana += arcana;
                    float blockMaxEterna = EnchantingStatRegistry.getMaxEterna(shelfState, level, shelfPos);
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

    private boolean isValidBookShelf(Level level, BlockPos tablePos, BlockPos offset) {
        BlockPos shelfPos = tablePos.offset(offset);
        BlockState shelfState = level.getBlockState(shelfPos);
        float eterna = EnchantingStatRegistry.getEterna(shelfState, level, shelfPos);
        float quanta = EnchantingStatRegistry.getQuanta(shelfState, level, shelfPos);
        float arcana = EnchantingStatRegistry.getArcana(shelfState, level, shelfPos);
        if (eterna == 0 && quanta == 0 && arcana == 0) {
            return false;
        }
        BlockPos inBetweenPos = tablePos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
        return level.getBlockState(inBetweenPos).getCollisionShape(level, inBetweenPos) != Shapes.block();
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
