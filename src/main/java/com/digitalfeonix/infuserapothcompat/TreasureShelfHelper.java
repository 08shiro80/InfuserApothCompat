package com.digitalfeonix.infuserapothcompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TreasureShelfHelper {

    public static boolean treasureShelfPresent = false;

    public static void scanForTreasureShelf(Level level, BlockPos infuserPos) {
        treasureShelfPresent = false;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos betweenPos = infuserPos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
            if (!level.getBlockState(betweenPos).isAir()) continue;

            BlockState state = level.getBlockState(infuserPos.offset(offset));
            if (state.isAir()) continue;
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if ("treasure_shelf".equals(blockId.getPath())) {
                treasureShelfPresent = true;
                return;
            }
        }
    }
}
