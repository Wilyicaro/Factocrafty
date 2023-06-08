package wily.factocrafty.compat;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.recipes.SolderingCraftingRecipe;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.ArrayList;
import java.util.List;

import static wily.factocrafty.client.screens.BasicMachineScreen.BACKGROUND_LOCATION;
import static wily.factocrafty.client.screens.FactocraftyMachineScreen.WIDGETS;
import static wily.factocrafty.compat.FactocraftyJeiUtils.fromProgress;
import static wily.factocrafty.util.ScreenUtil.renderScaled;
import static wily.factoryapi.util.StorageStringUtil.*;

public class FactocraftyProgressCategory<T extends Recipe<Container>> implements IRecipeCategory<T> {

    private final Component title;
    private final IDrawable background;

    private final LoadingCache<Integer, IDrawableAnimated> cachedProgressAnim;

    protected final IGuiHelper guiHelper;

    private IDrawableAnimated energyCell;

    private final RecipeType<T> recipeType;
    private final IFactoryDrawableType.DrawableProgress type;

    public FactocraftyProgressCategory(RecipeType<T> recipeType, IGuiHelper guiHelper) {
        this(recipeType,guiHelper, 139, 63);
    }
    public FactocraftyProgressCategory(ResourceLocation background, RecipeType<T> recipeType, IGuiHelper guiHelper, IFactoryDrawableType.DrawableProgress type){
        this(background,recipeType,guiHelper,type, 139, 63);
    }
    public FactocraftyProgressCategory(RecipeType<T> recipeType, IGuiHelper guiHelper, int width, int height) {
        this(BACKGROUND_LOCATION,recipeType,guiHelper, FactocraftyDrawables.MACHINE_PROGRESS, width, height);
    }


    public FactocraftyProgressCategory(ResourceLocation background, RecipeType<T> recipeType, IGuiHelper guiHelper, IFactoryDrawableType.DrawableProgress type, int width, int height) {
        this.recipeType = recipeType;
        this.type = type;
        this.title = Component.translatable("category.factocrafty.recipe." + getRecipeType().getUid().getPath());
        this.background = guiHelper.createDrawable(background, 18, 11, width, height);
        this.guiHelper = guiHelper;
        this.energyCell = guiHelper.createAnimatedDrawable(fromProgress(guiHelper, FactocraftyDrawables.ENERGY_CELL).build(),100, IDrawableAnimated.StartDirection.TOP,true);
        this.cachedProgressAnim = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return fromProgress(guiHelper,type).buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    @Override
    public List<Component> getTooltipStrings(T recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        List<Component> tooltips = new ArrayList<>();
        int energyConsume = recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getEnergyConsume() : 3;
        if (FactocraftyDrawables.ENERGY_CELL.inMouseLimit((int) mouseX, (int) mouseY,2,6)) tooltips.add(Component.translatable("tooltip.factocrafty.consuming",  getStorageAmount(energyConsume,false,"",kiloCY,CYMeasure)).withStyle(ChatFormatting.AQUA));
        return tooltips;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        boolean b = !(recipe instanceof AbstractFactocraftyProcessRecipe rcp) || rcp.getOtherResults().isEmpty();
        int max = recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getMaxProcess() : recipe instanceof AbstractCookingRecipe rcp ? rcp.getCookingTime():200;
        float exp = recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getExperience() : recipe instanceof AbstractCookingRecipe rcp ? rcp.getExperience():0;
        renderScaled(stack, (float) max / 20 + "s", 62, 52, 1f, 0x7E7E7E, false);
        if (exp > 0)renderScaled(stack,  Component.translatable("gui.jei.category.smelting.experience", exp).getString(), 62, 2, 1f, 0x7E7E7E, false);
        if (!b) recipeSlotsView.getSlotViews(RecipeIngredientRole.OUTPUT).forEach((r)-> r.getDisplayedItemStack().ifPresent((i-> {if (((AbstractFactocraftyProcessRecipe) recipe).getOtherResults().containsKey(i) && !i.isEmpty())  renderScaled(stack, Math.round(((AbstractFactocraftyProcessRecipe) recipe).getOtherResults().get(i) * 100) + "%",92,42,0.5F,0x7E7E7E,false);})));
        IDrawableAnimated cache = cachedProgressAnim.getUnchecked(max);
        cache.draw(stack, type== FactocraftyDrawables.PROGRESS ? 61 : 62, type.equals(FactocraftyDrawables.PROGRESS) ? 23 : 29);
        energyCell.draw(stack,2,6);
        IDrawableStatic slot = guiHelper.createDrawable(WIDGETS,178,0,18,18);
        guiHelper.createDrawable(WIDGETS,196,0,26,26).draw(stack,b ? 93 : 106, 19);
        if (recipe instanceof FactocraftyMachineRecipe rcp){
            if (!b)slot.draw(stack,88, 23);
            if (rcp.hasFluidIngredient() && !rcp.getFluidIngredient().isEmpty()) guiHelper.createDrawable(WIDGETS,178,107,18,21).draw(stack,37, 2);
                else slot.draw(stack,37, 5);
        }
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
        boolean b = !(recipe instanceof AbstractFactocraftyProcessRecipe rcp) || rcp.getOtherResults().isEmpty();
        builder.addSlot(RecipeIngredientRole.OUTPUT,  b ? 98 : 111, 24).addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
        IRecipeSlotBuilder input = null;
        if (recipe instanceof FactocraftyMachineRecipe rcp){
            if (!b) builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 24).addItemStacks(rcp.getOtherResults().keySet().stream().toList());
            if (rcp.hasFluidIngredient() && !rcp.getFluidIngredient().isEmpty()) input = builder.addSlot(RecipeIngredientRole.INPUT, 38, 3).addFluidStack(rcp.getFluidIngredient().getFluid(),rcp.getFluidIngredient().getAmount()).setFluidRenderer(6* FluidStack.bucketAmount(), true, 16, 19);
            if (rcp.hasFluidResult() && !rcp.getResultFluid().isEmpty())builder.addSlot(RecipeIngredientRole.OUTPUT,  120, 6).addFluidStack(rcp.getResultFluid().getFluid(),rcp.getResultFluid().getAmount()).setFluidRenderer(2* FluidStack.bucketAmount(), true, 24, 52);
        }
        if (input ==null) {
            input = builder.addSlot(RecipeIngredientRole.INPUT, 38, 6);
            if (recipe.getIngredients().stream().anyMatch((i)-> !i.isEmpty())) input.addItemStack(SolderingCraftingRecipe.getFactocraftyStack(recipe.getIngredients().get(0)).copyWithCount(recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getIngredientCount(): 1));
        }

    }
}
