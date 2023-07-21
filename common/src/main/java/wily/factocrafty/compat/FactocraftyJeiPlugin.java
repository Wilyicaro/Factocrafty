package wily.factocrafty.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.block.cable.CableTiers;
import wily.factocrafty.client.screens.ElectricFurnaceScreen;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.RefinerScreen;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.ShapedTagRecipe;
import wily.factocrafty.recipes.ShapelessTagRecipe;
import wily.factocrafty.recipes.SolderingCraftingRecipe;
import wily.factoryapi.base.ICraftyEnergyItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static wily.factocrafty.compat.FactocraftyJeiUtils.getRecipes;
import static wily.factocrafty.init.Registration.getModResource;

@JeiPlugin
public class FactocraftyJeiPlugin  implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Factocrafty.MOD_ID,"jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(FactocraftyMachineScreen.class,new FactocraftyMachineGuiHandler());

    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(SolderingCraftingRecipe.class,(s)-> (builder, craftingGridHelper, focuses) -> {
            craftingGridHelper.createAndSetInputs(builder, s.getIngredients().stream().map((i)->Arrays.stream(i.getItems()).peek((stack-> {if (stack.getItem() == Registration.SOLDERING_IRON.get() &&  stack.getItem() instanceof ICraftyEnergyItem<?> e) e.getCraftyEnergy(stack).receiveEnergy(e.getCraftyEnergy(stack).getMaxEnergyStored(),false);})).toList()).toList(),0,0);
            Ingredient ing = Ingredient.of(focuses.getItemStackFocuses(RecipeIngredientRole.INPUT).map(f-> f.getTypedValue().getIngredient()));
            List<Ingredient> inputs = new ArrayList<>(s.getAdditionalInputs());
            if (!ing.isEmpty())
                inputs.replaceAll((i)-> {
                    if (Arrays.stream(ing.getItems()).allMatch(i)) return ing;
                    return i;
                });
            craftingGridHelper.createAndSetOutputs(builder, Arrays.stream(s.getPrincipalInput().getItems()).map(i-> s.getResultItems(inputs,i)).flatMap(Collection::stream).collect(Collectors.toList()));
        });
        registration.getCraftingCategory().addCategoryExtension(ShapelessTagRecipe.class,(s)-> (builder, craftingGridHelper, focuses) -> {
            craftingGridHelper.createAndSetInputs(builder, s.getIngredientsStack(),0,0);
            craftingGridHelper.createAndSetOutputs(builder, List.of(s.getResultItem(RegistryAccess.EMPTY)));
        });
        registration.getCraftingCategory().addCategoryExtension(ShapedTagRecipe.class,(s)-> (builder, craftingGridHelper, focuses) -> {
            craftingGridHelper.createAndSetInputs(builder, s.getIngredientsStack(),s.getWidth(),s.getHeight());
            craftingGridHelper.createAndSetOutputs(builder, List.of(s.getResultItem(RegistryAccess.EMPTY)));
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FactocraftyProgressCategory<>(ElectricFurnaceScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.SMELTING,registration.getJeiHelpers().getGuiHelper(), FactocraftyDrawables.PROGRESS));
        registration.addRecipeCategories(new FactocraftyProgressCategory<>(FactocraftyJeiRecipeTypes.MACERATING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyProgressCategory<>(FactocraftyJeiRecipeTypes.COMPRESSING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyProgressCategory<>(FactocraftyJeiRecipeTypes.EXTRACTING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyProgressCategory<>(RefinerScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.REFINING,registration.getJeiHelpers().getGuiHelper(), FactocraftyDrawables.MACHINE_PROGRESS, 145,63));
        registration.addRecipeCategories(new EnricherCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GasInfuserCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        //registration.addRecipes(RecipeTypes.CRAFTING, List.of(new ShapelessRecipe(getModResource("soldering_board_recipe"),"", CraftingBookCategory.MISC,new ItemStack(Registration.CIRCUIT_BOARD.get()), NonNullList.of(Ingredient.EMPTY,Ingredient.of(Registration.SOLDERING_IRON.get()),Ingredient.of(Registration.CIRCUIT_BOARD.get()),Ingredient.of(CableTiers.TIN.getBlock())))));
        registration.addRecipes(FactocraftyJeiRecipeTypes.SMELTING, getRecipes(recipeManager, RecipeType.SMELTING));
        registration.addRecipes(FactocraftyJeiRecipeTypes.MACERATING, getRecipes(recipeManager, Registration.MACERATOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.COMPRESSING, getRecipes(recipeManager, Registration.COMPRESSOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.EXTRACTING, getRecipes(recipeManager, Registration.EXTRACTOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.REFINING, getRecipes(recipeManager, Registration.REFINER_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.ENRICHING, getRecipes(recipeManager, Registration.ENRICHER_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.GAS_INFUSION, getRecipes(recipeManager, Registration.GASEOUS_INFUSION_RECIPE.get()));

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Registration.IRON_FURNACE.get().asItem().getDefaultInstance(),RecipeTypes.FUELING);
        registration.addRecipeCatalyst(Registration.GENERATOR.get().asItem().getDefaultInstance(),RecipeTypes.FUELING);
        registration.addRecipeCatalyst(Registration.IRON_FURNACE.get().asItem().getDefaultInstance(),RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(Registration.ELECTRIC_FURNACE.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.SMELTING);
        registration.addRecipeCatalyst(Registration.MACERATOR.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.MACERATING);
        registration.addRecipeCatalyst(Registration.COMPRESSOR.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.COMPRESSING);
        registration.addRecipeCatalyst(Registration.EXTRACTOR.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.EXTRACTING);
        registration.addRecipeCatalyst(Registration.REFINER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.REFINING);
        registration.addRecipeCatalyst(Registration.ENRICHER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.ENRICHING);
        registration.addRecipeCatalyst(Registration.GAS_INFUSER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.GAS_INFUSION);


    }

}
