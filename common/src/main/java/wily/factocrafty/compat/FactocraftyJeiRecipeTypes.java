package wily.factocrafty.compat;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.EnricherRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

import static wily.factocrafty.init.Registration.getModResource;

public class FactocraftyJeiRecipeTypes {

    public static RecipeType<SmeltingRecipe> SMELTING = new RecipeType<>(getModResource("smelting"), SmeltingRecipe.class);
    public static RecipeType<AbstractFactocraftyProcessRecipe> MACERATING = new RecipeType<>(getModResource("macerating"), FactocraftyMachineRecipe.class);
    public static RecipeType<AbstractFactocraftyProcessRecipe> COMPRESSING = new RecipeType<>(getModResource("compressing"), FactocraftyMachineRecipe.class);

    public static RecipeType<AbstractFactocraftyProcessRecipe> EXTRACTING = new RecipeType<>(getModResource("extracting"), FactocraftyMachineRecipe.class);
    public static RecipeType<AbstractFactocraftyProcessRecipe> REFINING = new RecipeType<>(getModResource("refining"), FactocraftyMachineRecipe.class);
    public static RecipeType<EnricherRecipe> ENRICHING = new RecipeType<>(getModResource("enriching"), EnricherRecipe.class);

}
