package com.digitalfeonix.infuserapothcompat;

import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Method;
import java.util.Optional;

public class InfusionHelper {

    private static Class<?> infusionRecipeClass = null;
    private static Method findMatchMethod = null;
    private static Method assembleMethod = null;
    private static Method getRequirementsMethod = null;
    private static Method statsEternaMethod = null;
    private static Method statsQuantaMethod = null;
    private static Method statsArcanaMethod = null;
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;
        initialized = true;
        try {
            infusionRecipeClass = Class.forName("dev.shadowsoffire.apothic_enchanting.table.infusion.InfusionRecipe");
            findMatchMethod = infusionRecipeClass.getMethod("findMatch",
                    Level.class, ItemStack.class, float.class, float.class, float.class);
            assembleMethod = infusionRecipeClass.getMethod("assemble",
                    ItemStack.class, float.class, float.class, float.class);
            getRequirementsMethod = infusionRecipeClass.getMethod("getRequirements");

            Class<?> statsClass = Class.forName("dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry$Stats");
            statsEternaMethod = statsClass.getMethod("eterna");
            statsQuantaMethod = statsClass.getMethod("quanta");
            statsArcanaMethod = statsClass.getMethod("arcana");

            InfuserApothCompat.LOGGER.info("Found ApothicEnchanting InfusionRecipe class - infusion support enabled");
        } catch (ReflectiveOperationException e) {
            infusionRecipeClass = null;
            InfuserApothCompat.LOGGER.debug("ApothicEnchanting InfusionRecipe not available - infusion support disabled: {}", e.getMessage());
        }
    }

    public static boolean isInfusionAvailable() {
        init();
        return infusionRecipeClass != null;
    }

    public static Optional<InfusionResult> findMatchingInfusion(Level level, BlockPos infuserPos, ItemStack input) {
        if (!isInfusionAvailable() || input.isEmpty()) {
            return Optional.empty();
        }

        try {
            ShelfStats stats = calculateStats(level, infuserPos);

            return findMatchingInfusionRecipe(level, input, stats.eterna(), stats.quanta(), stats.arcana());
        } catch (Exception e) {
            InfuserApothCompat.LOGGER.debug("Error checking infusion recipes", e);
            return Optional.empty();
        }
    }

    private record ShelfStats(float eterna, float quanta, float arcana) {}

    private static ShelfStats calculateStats(Level level, BlockPos pos) {
        float eterna = 0.0F;
        float quanta = 0.0F;
        float arcana = 0.0F;
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (!isValidBookshelf(level, pos, offset)) continue;
            BlockPos shelfPos = pos.offset(offset);
            BlockState state = level.getBlockState(shelfPos);
            eterna += EnchantingStatRegistry.getEterna(state, level, shelfPos);
            // getQuanta/getArcana cast non-datapack blocks to EnchantmentStatBlock; a vanilla bookshelf
            // is neither, so skip its quanta/arcana instead of aborting the whole shelf scan.
            try {
                float shelfQuanta = EnchantingStatRegistry.getQuanta(state, level, shelfPos);
                float shelfArcana = EnchantingStatRegistry.getArcana(state, level, shelfPos);
                quanta += shelfQuanta;
                arcana += shelfArcana;
            } catch (ClassCastException ignored) {
            }
        }
        return new ShelfStats(Math.min(eterna, 100.0F), Math.min(quanta, 100.0F), Math.min(arcana, 100.0F));
    }

    private static boolean isValidBookshelf(Level level, BlockPos tablePos, BlockPos offset) {
        BlockPos betweenPos = tablePos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
        BlockState betweenState = level.getBlockState(betweenPos);
        return (!betweenState.isSolidRender() || betweenState.is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) &&
               !level.getBlockState(tablePos.offset(offset)).isAir();
    }

    private static Optional<InfusionResult> findMatchingInfusionRecipe(Level level, ItemStack input,
            float eterna, float quanta, float arcana) {
        try {
            Object recipe = findMatchMethod.invoke(null, level, input, eterna, quanta, arcana);
            if (recipe == null) {
                return Optional.empty();
            }

            ItemStack output = (ItemStack) assembleMethod.invoke(recipe, input, eterna, quanta, arcana);
            var stats = getRequirementsMethod.invoke(recipe);

            float reqEterna = (float) statsEternaMethod.invoke(stats);
            float reqQuanta = (float) statsQuantaMethod.invoke(stats);
            float reqArcana = (float) statsArcanaMethod.invoke(stats);

            InfuserApothCompat.LOGGER.debug("Found matching infusion recipe for {}, requires: E={} Q={} A={}, has: E={} Q={} A={}",
                    input.getItem(), reqEterna, reqQuanta, reqArcana, eterna, quanta, arcana);

            return Optional.of(new InfusionResult(
                    output,
                    eterna >= reqEterna,
                    quanta >= reqQuanta,
                    arcana >= reqArcana,
                    reqEterna, reqQuanta, reqArcana,
                    eterna, quanta, arcana
            ));
        } catch (Exception e) {
            InfuserApothCompat.LOGGER.debug("Error finding infusion recipe: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public record InfusionResult(
            ItemStack output,
            boolean eternaMet,
            boolean quantaMet,
            boolean arcanaMet,
            float requiredEterna,
            float requiredQuanta,
            float requiredArcana,
            float currentEterna,
            float currentQuanta,
            float currentArcana
    ) {
        public boolean canInfuse() {
            return eternaMet && quantaMet && arcanaMet;
        }
    }
}
