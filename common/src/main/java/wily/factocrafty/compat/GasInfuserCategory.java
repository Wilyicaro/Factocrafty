package wily.factocrafty.compat;

import dev.architectury.fluid.FluidStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.GasInfuserScreen;
import wily.factocrafty.recipes.GasInfuserRecipe;
import wily.factocrafty.util.ScreenUtil;

import static wily.factocrafty.util.ScreenUtil.renderScaled;

public class GasInfuserCategory extends FactocraftyProgressCategory<GasInfuserRecipe>{

    public GasInfuserCategory(IGuiHelper guiHelper) {
        super(GasInfuserScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.GAS_INFUSION, guiHelper,FactocraftyDrawables.MACHINE_PROGRESS, 145, 63);
    }

    @Override
    public void draw(GasInfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);

    }
    public void drawExp(GasInfuserRecipe recipe, GuiGraphics graphics){
        if (recipe.getExperience() > 0)renderScaled(graphics.pose(),  I18n.get("gui.jei.category.smelting.experience", recipe.getExperience()), 62, -4, 0.5f, 0x7E7E7E, false);
    }
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GasInfuserRecipe recipe, IFocusGroup focuses) {
        boolean isElectrolyzing = recipe.getDiff() == 1;
        builder.addSlot(RecipeIngredientRole.INPUT, isElectrolyzing ? 38 : 29, 3).addFluidStack(recipe.getFluidIngredient().getFluid(),recipe.getFluidIngredient().getAmount()).setFluidRenderer(2* FluidStack.bucketAmount(), true, 16, 19);
        if(!isElectrolyzing) builder.addSlot(RecipeIngredientRole.INPUT, 47, 3).addFluidStack(recipe.getOtherFluid().getFluid(),recipe.getOtherFluid().getAmount()).setFluidRenderer(2* FluidStack.bucketAmount(), true, 16, 19);
        builder.addSlot(RecipeIngredientRole.OUTPUT,  isElectrolyzing ? 94 : 99, 6).addFluidStack(recipe.getResultFluid().getFluid(),recipe.getResultFluid().getAmount()).setFluidRenderer(4* FluidStack.bucketAmount(), true, 24, 52);
        if(isElectrolyzing)  builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 6).addFluidStack(recipe.getOtherFluid().getFluid(),recipe.getOtherFluid().getAmount()).setFluidRenderer(4* FluidStack.bucketAmount(), true, 24, 52);

    }

    @Override
    protected void drawSlots(GasInfuserRecipe recipe, GuiGraphics graphics){
        boolean isElectrolyzing = recipe.getDiff() == 1;
        ScreenUtil.drawGUIFluidSlot(graphics, isElectrolyzing ? 93 : 98, 5,26,54);
        if(isElectrolyzing) ScreenUtil.drawGUIFluidSlot(graphics, 119, 5,26,54);

        ScreenUtil.drawGUIFluidSlot(graphics, isElectrolyzing ? 37 : 28,2,18,21);
        if(!isElectrolyzing) ScreenUtil.drawGUIFluidSlot(graphics, 46,2,18,21);

    }

}
