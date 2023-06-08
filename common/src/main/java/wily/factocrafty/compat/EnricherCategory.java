package wily.factocrafty.compat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import wily.factocrafty.client.screens.EnricherScreen;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.item.FactocraftyOre;
import wily.factocrafty.recipes.EnricherRecipe;
import wily.factocrafty.util.ScreenUtil;

import java.util.List;

import static wily.factocrafty.client.screens.FactocraftyMachineScreen.getProgressScaled;
import static wily.factocrafty.compat.FactocraftyJeiUtils.fromProgress;

public class EnricherCategory extends FactocraftyProgressCategory<EnricherRecipe>{

    public EnricherCategory(IGuiHelper guiHelper) {
        super(EnricherScreen.BACKGROUND_LOCATION,FactocraftyJeiRecipeTypes.ENRICHING, guiHelper,FactocraftyDrawables.MACHINE_PROGRESS, 145, 67);

    }

    @Override
    public void draw(EnricherRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, stack, mouseX, mouseY);
        int c = recipe.getMatter().first.getColor().col;
        RenderSystem.setShaderColor(ScreenUtil.getRed(c),ScreenUtil.getGreen(c),ScreenUtil.getBlue(c),1.0F);
        FactocraftyDrawables.MATTER_PROGRESS.drawProgress(stack, 2, 63, getProgressScaled(recipe.getMatter().second, 200,135));
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnricherRecipe recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        builder.addSlot(RecipeIngredientRole.INPUT, 38, 24).addIngredients(FactocraftyOre.Material.ingCache.getUnchecked(recipe.getMatter().first));
    }

    @Override
    public List<Component> getTooltipStrings(EnricherRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
        if (FactocraftyDrawables.MATTER_PROGRESS.inMouseLimit((int) mouseX, (int) mouseY,2,63)) tooltips.add(Component.translatable("tooltip.factocrafty.matter",recipe.getMatter().first.getComponent().getString(),recipe.getMatter().second));
        return tooltips;
    }
}
