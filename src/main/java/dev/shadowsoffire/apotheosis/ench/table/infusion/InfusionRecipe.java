package dev.shadowsoffire.apotheosis.ench.table.infusion;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface InfusionRecipe extends Recipe<Container> {
    boolean matches(ItemStack input, float eterna, float quanta, float arcana);
    ItemStack assemble(ItemStack input, float eterna, float quanta, float arcana);
    ItemStack getInput();
    ItemStack getOutput();
    float getRequiredEterna();
    float getMaxEterna();
    float getRequiredQuanta();
    float getMaxQuanta();
    float getRequiredArcana();
    float getMaxArcana();

    static RecipeType<InfusionRecipe> getRecipeType() {
        throw new UnsupportedOperationException("Stub");
    }

    static java.util.List<InfusionRecipe> getRecipes(Level level) {
        throw new UnsupportedOperationException("Stub");
    }
}
