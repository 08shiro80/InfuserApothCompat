package com.digitalfeonix.infuserapothcompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class InfusionHelper {

    private static Class<?> infusionRecipeClass = null;
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;
        initialized = true;
        try {
            infusionRecipeClass = Class.forName("dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe");
            InfuserApothCompat.LOGGER.info("Found Apotheosis EnchantingRecipe class - infusion support enabled");
        } catch (ClassNotFoundException e) {
            InfuserApothCompat.LOGGER.debug("Apotheosis EnchantingRecipe not found - infusion support disabled");
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
            float eterna = calculateEterna(level, infuserPos);
            float quanta = calculateQuanta(level, infuserPos);
            float arcana = calculateArcana(level, infuserPos);

            return findMatchingInfusionRecipe(level, input, eterna, quanta, arcana);
        } catch (Exception e) {
            InfuserApothCompat.LOGGER.debug("Error checking infusion recipes", e);
            return Optional.empty();
        }
    }

    private static float calculateEterna(Level level, BlockPos pos) {
        float eterna = 0.0F;
        for (BlockPos offset : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos shelfPos = pos.offset(offset);
            if (isValidBookshelf(level, pos, offset)) {
                BlockState state = level.getBlockState(shelfPos);
                eterna += dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry.getEterna(state, level, shelfPos);
            }
        }
        return Math.min(eterna, 100.0F);
    }

    private static float calculateQuanta(Level level, BlockPos pos) {
        float quanta = 0.0F;
        for (BlockPos offset : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos shelfPos = pos.offset(offset);
            if (isValidBookshelf(level, pos, offset)) {
                BlockState state = level.getBlockState(shelfPos);
                quanta += dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry.getQuanta(state, level, shelfPos);
            }
        }
        return Math.min(quanta, 100.0F);
    }

    private static float calculateArcana(Level level, BlockPos pos) {
        float arcana = 0.0F;
        for (BlockPos offset : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
            BlockPos shelfPos = pos.offset(offset);
            if (isValidBookshelf(level, pos, offset)) {
                BlockState state = level.getBlockState(shelfPos);
                arcana += dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry.getArcana(state, level, shelfPos);
            }
        }
        return Math.min(arcana, 100.0F);
    }

    private static boolean isValidBookshelf(Level level, BlockPos tablePos, BlockPos offset) {
        BlockPos betweenPos = tablePos.offset(offset.getX() / 2, offset.getY(), offset.getZ() / 2);
        return level.getBlockState(betweenPos).isAir() &&
               !level.getBlockState(tablePos.offset(offset)).isAir();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Optional<InfusionResult> findMatchingInfusionRecipe(Level level, ItemStack input,
            float eterna, float quanta, float arcana) {
        try {
            var recipeManager = level.getRecipeManager();

            var apothClass = Class.forName("dev.shadowsoffire.apotheosis.Apoth$RecipeTypes");
            var infusionField = apothClass.getField("INFUSION");
            var recipeType = (net.minecraft.world.item.crafting.RecipeType) infusionField.get(null);

            var recipes = (java.util.List<?>) recipeManager.getAllRecipesFor(recipeType);

            var statsClass = Class.forName("dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry$Stats");
            var eternaMethod = statsClass.getMethod("eterna");
            var quantaMethod = statsClass.getMethod("quanta");
            var arcanaMethod = statsClass.getMethod("arcana");

            for (var recipeHolder : recipes) {
                Object recipe;
                try {
                    var valueMethod = recipeHolder.getClass().getMethod("value");
                    recipe = valueMethod.invoke(recipeHolder);
                } catch (NoSuchMethodException e) {
                    recipe = recipeHolder;
                }

                if (!infusionRecipeClass.isInstance(recipe)) {
                    continue;
                }

                var matchesMethod = recipe.getClass().getMethod("matches", ItemStack.class, float.class, float.class, float.class);
                var assembleMethod = recipe.getClass().getMethod("assemble", ItemStack.class, float.class, float.class, float.class);
                var getRequirementsMethod = recipe.getClass().getMethod("getRequirements");

                if ((boolean) matchesMethod.invoke(recipe, input, eterna, quanta, arcana)) {
                    ItemStack output = (ItemStack) assembleMethod.invoke(recipe, input, eterna, quanta, arcana);

                    var stats = getRequirementsMethod.invoke(recipe);
                    float reqEterna = (float) eternaMethod.invoke(stats);
                    float reqQuanta = (float) quantaMethod.invoke(stats);
                    float reqArcana = (float) arcanaMethod.invoke(stats);

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
                }
            }
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
