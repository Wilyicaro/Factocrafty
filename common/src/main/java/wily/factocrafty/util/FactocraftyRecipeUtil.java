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
import java.util.stream.Stream;

public class FactocraftyRecipeUtil {
    public static <T extends Recipe<? extends Container>> List<T> getRecipes(RecipeManager manager, RecipeType<T> type) {
        return getRecipesStream(manager,type).toList();
    }
    public static <T extends Recipe<? extends Container>> Stream<T> getRecipesStream(RecipeManager manager, RecipeType<T> type) {
        Collection<Recipe<?>> recipes = manager.getRecipes();
        return (Stream<T>) recipes.stream().filter((iRecipe) -> iRecipe.getType() == type);
    }
    public static ItemStack getFactocraftyStack(Ingredient i){
        for (int j = 0; j < i.getItems().length; j++) {
            ItemStack s = i.getItems()[j];
            if (s.getItem().arch$registryName().getNamespace().equals(Factocrafty.MOD_ID)) return s;
        }
        return i.getItems()[0];
    }
}
