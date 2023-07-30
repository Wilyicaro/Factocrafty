package wily.factocrafty.compat;

import it.unimi.dsi.fastutil.Pair;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.List;

import static wily.factocrafty.Factocrafty.MOD_ID;
import static wily.factocrafty.util.ScreenUtil.renderScaled;

public class ScrapBoxCategory implements IRecipeCategory<FactocraftyJeiRecipeTypes.IScrapBoxRecipe> {



    private final Component title;
    private final IDrawable background;


    protected final IGuiHelper guiHelper;



    public ScrapBoxCategory( IGuiHelper guiHelper) {
        this.title = Component.translatable("category.factocrafty.recipe." + getRecipeType().getUid().getPath());
        this.background = guiHelper.drawableBuilder(new ResourceLocation( MOD_ID,"textures/gui/container/scrap_box_category.png"), 0, 0, 39, 39).setTextureSize(39,39).build();
        this.guiHelper = guiHelper;

    }


    @Override
    public void draw(FactocraftyJeiRecipeTypes.IScrapBoxRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        recipeSlotsView.findSlotByName("output").ifPresent((s->s.getDisplayedItemStack().ifPresent(i-> {
            List<Pair<ItemStack,Float>> list = recipe.getItems().stream().filter(p-> ItemStack.matches(p.first(),i)).toList();
            NumberFormat n = NumberFormat.getPercentInstance();
            n.setMinimumFractionDigits(0);
            n.setMaximumFractionDigits(3);
            if (!i.isEmpty() && !list.isEmpty()) renderScaled(graphics.pose(), n.format(list.get(0).second()),16,30,0.5F,0x7E7E7E,false);
        })));

    }

    public RecipeType<FactocraftyJeiRecipeTypes.IScrapBoxRecipe> getRecipeType() {
        return FactocraftyJeiRecipeTypes.SCRAP_BOX_ITEMS;
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
    public void setRecipe(IRecipeLayoutBuilder builder, FactocraftyJeiRecipeTypes.IScrapBoxRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT,12,12).addIngredients(VanillaTypes.ITEM_STACK,recipe.getItems().stream().map(Pair::first).toList()).setSlotName("output");
    }

}
