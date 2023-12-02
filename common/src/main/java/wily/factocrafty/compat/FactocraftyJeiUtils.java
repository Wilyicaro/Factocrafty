package wily.factocrafty.compat;


import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FactocraftyJeiUtils {
    public static IDrawableBuilder fromProgress(IGuiHelper helper, IFactoryDrawableType type){
        return helper.drawableBuilder(type.texture(), type.uvX(), type.uvY(), type.width(), type.height());
    }
    public  static mezz.jei.api.recipe.RecipeType<?> fromVanillaRecipeType(RecipeType<?> recipeType){
        return FactocraftyJeiRecipeTypes.recipeTypes.get(Registration.RECIPE_TYPES.getRegistrar().getId(recipeType).getPath());
    }
}
