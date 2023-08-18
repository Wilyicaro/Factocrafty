package wily.factocrafty.compat;

import it.unimi.dsi.fastutil.Pair;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import wily.factocrafty.item.ScrapBoxItem;
import wily.factocrafty.recipes.EnricherRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.recipes.GasInfuserRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static wily.factocrafty.init.Registration.getModResource;

public class FactocraftyJeiRecipeTypes {
    public static Map<String,RecipeType<?>> recipeTypes = new HashMap<>();

    public static RecipeType<SmeltingRecipe> SMELTING = create("smelting", SmeltingRecipe.class);
    public static RecipeType<FactocraftyMachineRecipe> MACERATING = create("macerating", FactocraftyMachineRecipe.class);
    public static RecipeType<FactocraftyMachineRecipe> COMPRESSING = create("compressing", FactocraftyMachineRecipe.class);

    public static RecipeType<FactocraftyMachineRecipe> EXTRACTING = create("extracting", FactocraftyMachineRecipe.class);

    public static RecipeType<FactocraftyMachineRecipe> RECYCLING = create("recycling", FactocraftyMachineRecipe.class);
    public static RecipeType<FactocraftyMachineRecipe> REFINING = create("refining", FactocraftyMachineRecipe.class);
    public static RecipeType<FactocraftyMachineRecipe> SAWING = create("sawing", FactocraftyMachineRecipe.class);

    public static RecipeType<EnricherRecipe> ENRICHING = create("enriching", EnricherRecipe.class);

    public static RecipeType<GasInfuserRecipe> GAS_INFUSION = create("gaseous_infusion", GasInfuserRecipe.class);


    public static RecipeType<IScrapBoxRecipe> SCRAP_BOX_ITEMS = new RecipeType<>(getModResource("scrap_box"),IScrapBoxRecipe.class);

    public interface IScrapBoxRecipe {
        List<Pair<ItemStack,Float>> getItems();

    }
    public static<T extends Recipe<?>> RecipeType<T> create(String name, Class<T> recipeClass){
        RecipeType<T> recipeType = new RecipeType<>(getModResource(name),recipeClass);
        recipeTypes.put(name,recipeType);
        return recipeType;
    }

}
