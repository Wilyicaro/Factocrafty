package wily.factocrafty.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import wily.factocrafty.Factocrafty;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FactocraftyRecipeUtil {
    public static <T extends Recipe<Container>> List<T> getRecipes(RecipeManager manager, RecipeType<?> type) {
        Collection<Recipe<?>> recipes = manager.getRecipes();
        return (List)recipes.stream().filter((iRecipe) -> iRecipe.getType() == type).collect(Collectors.toList());
    }
    public static ItemStack getFactocraftyStack(Ingredient i){
        for (int j = 0; j < i.getItems().length; j++) {
            ItemStack s = i.getItems()[j];
            if (s.getItem().arch$registryName().getNamespace().equals(Factocrafty.MOD_ID)) return s;
        }
        return i.getItems()[0];
    }
}
