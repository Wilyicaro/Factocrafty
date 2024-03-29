package wily.factocrafty.compat;

import it.unimi.dsi.fastutil.Pair;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.ElectricFurnaceScreen;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.client.screens.RefinerScreen;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.ScrapBoxItem;
import wily.factocrafty.recipes.ShapedTagRecipe;
import wily.factocrafty.recipes.ShapelessTagRecipe;
import wily.factocrafty.recipes.SolderingCraftingRecipe;
import wily.factoryapi.base.ICraftyStorageItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static wily.factocrafty.util.FactocraftyRecipeUtil.getRecipes;


@JeiPlugin
public class FactocraftyJeiPlugin  implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Factocrafty.MOD_ID,"jei");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(FactocraftyStorageScreen.class,new FactocraftyMachineGuiHandler());

    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(SolderingCraftingRecipe.class,(s)-> (builder, craftingGridHelper, focuses) -> {
            craftingGridHelper.createAndSetInputs(builder, s.getIngredients().stream().map((i)->Arrays.stream(i.getItems()).peek((stack-> {if (stack.getItem() == Registration.SOLDERING_IRON.get() &&  stack.getItem() instanceof ICraftyStorageItem e) e.getEnergyStorage(stack).receiveEnergy(e.getEnergyStorage(stack).getMaxEnergyStored(),false);})).toList()).toList(),0,0);
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
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(ElectricFurnaceScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.SMELTING,registration.getJeiHelpers().getGuiHelper(), FactocraftyDrawables.PROGRESS));
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(FactocraftyJeiRecipeTypes.MACERATING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(FactocraftyJeiRecipeTypes.COMPRESSING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(FactocraftyJeiRecipeTypes.EXTRACTING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(FactocraftyJeiRecipeTypes.SAWING,registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RecyclerCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FactocraftyMachineCategory<>(RefinerScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.REFINING,registration.getJeiHelpers().getGuiHelper(), FactocraftyDrawables.MACHINE_PROGRESS, 145,63));
        registration.addRecipeCategories(new EnricherCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GasInfuserCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ScrapBoxCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level world = Minecraft.getInstance().level;
        RecipeManager recipeManager = world.getRecipeManager();
        registration.addRecipes(FactocraftyJeiRecipeTypes.SMELTING, getRecipes(recipeManager, RecipeType.SMELTING));
        registration.addRecipes(FactocraftyJeiRecipeTypes.MACERATING, getRecipes(recipeManager, Registration.MACERATOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.COMPRESSING, getRecipes(recipeManager, Registration.COMPRESSOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.EXTRACTING, getRecipes(recipeManager, Registration.EXTRACTOR_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.RECYCLING, getRecipes(recipeManager, Registration.RECYCLER_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.REFINING, getRecipes(recipeManager, Registration.REFINER_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.ENRICHING, getRecipes(recipeManager, Registration.ENRICHER_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.GAS_INFUSION, getRecipes(recipeManager, Registration.GASEOUS_INFUSION_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.SAWING, getRecipes(recipeManager, Registration.SAWMILL_RECIPE.get()));
        registration.addRecipes(FactocraftyJeiRecipeTypes.SCRAP_BOX_ITEMS,List.of(()-> ScrapBoxItem.SCRAP_ITEMS.stream().map(p-> Pair.of(p.first().get(),p.second())).toList()));

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
        registration.addRecipeCatalyst(Registration.RECYCLER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.RECYCLING);
        registration.addRecipeCatalyst(Registration.REFINER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.REFINING);
        registration.addRecipeCatalyst(Registration.ENRICHER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.ENRICHING);
        registration.addRecipeCatalyst(Registration.GAS_INFUSER.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.GAS_INFUSION);
        registration.addRecipeCatalyst(Registration.SAWMILL.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.SAWING);
        registration.addRecipeCatalyst(Registration.SCRAP_BOX.get().asItem().getDefaultInstance(),FactocraftyJeiRecipeTypes.SCRAP_BOX_ITEMS);


    }

}
