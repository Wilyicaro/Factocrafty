package wily.factocrafty.compat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

import static wily.factocrafty.util.ScreenUtil.renderScaled;

public class RecyclerCategory extends FactocraftyMachineCategory<FactocraftyMachineRecipe> {
    public RecyclerCategory(IGuiHelper guiHelper) {
        super(FactocraftyJeiRecipeTypes.RECYCLING, guiHelper);
    }

    @Override
    public void draw(FactocraftyMachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        recipeSlotsView.findSlotByName("output").ifPresent((s->s.getDisplayedItemStack().ifPresent(i-> {if (!i.isEmpty()) renderScaled(graphics.pose(), Math.round(recipe.getResultChance() * 100) + "%",102,49,0.5F,0x7E7E7E,false);})));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FactocraftyMachineRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT,98 , 24).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY)).setSlotName("output");

        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 38, 6);
        for (Ingredient i : recipe.getIngredients()) {
            if (!i.isEmpty())input.addIngredients(i);
            else input.addItemStacks(BuiltInRegistries.ITEM.stream().map(Item::getDefaultInstance).filter(stack-> !stack.isEmpty()).toList());
        }
    }

}

