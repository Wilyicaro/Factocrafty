package wily.factocrafty.compat;


import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import wily.factoryapi.base.IFactoryDrawableType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FactocraftyJeiUtils {
    public static <T extends Recipe<Container>> List<T> getRecipes(RecipeManager manager, RecipeType<?> type) {
        Collection<Recipe<?>> recipes = manager.getRecipes();
        return (List)recipes.stream().filter((iRecipe) -> iRecipe.getType() == type).collect(Collectors.toList());
    }
    public static IDrawableBuilder fromProgress(IGuiHelper helper, IFactoryDrawableType type){
        return helper.drawableBuilder(type.texture(), type.uvX(), type.uvY(), type.width(), type.height());
    }
}
