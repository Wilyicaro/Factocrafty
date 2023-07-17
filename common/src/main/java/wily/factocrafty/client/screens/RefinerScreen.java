package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.List;
import java.util.Map;

import static wily.factocrafty.util.ScreenUtil.renderScaled;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class RefinerScreen extends ChangeableInputMachineScreen {


    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/refiner.png");

    public RefinerScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    RefinerBlockEntity rBe = (RefinerBlockEntity) getMenu().be;

    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}
    private IFactoryDrawableType.DrawableStatic<IFactoryDrawableType.DrawableImage> resultTank;

    @Override
    protected void init() {
        super.init();
        resultTank = FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 138, topPos + 17);
    }

    @Override
    public List<FactocraftyDrawableButton> addButtons(List<FactocraftyDrawableButton> list) {
        list.add(new FactocraftyDrawableButton(leftPos + 85,topPos + 26,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(rBe.getBlockPos(),Math.min(rBe.recipeIndex.get() + 1,rBe.recipeSize.get() - 1),rBe.additionalSyncInt.indexOf(rBe.recipeIndex))),Component.translatable("gui.factocrafty.heat_up"),FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(3)));
        list.add(new FactocraftyDrawableButton( leftPos + 85,topPos + 48,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(rBe.getBlockPos(),Math.max(rBe.recipeIndex.get() - 1,0),rBe.additionalSyncInt.indexOf(rBe.recipeIndex))),Component.translatable("gui.factocrafty.heat_down"),FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(4)));
        return super.addButtons(list);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int i, int j) {
        super.renderLabels(graphics,i,j);
        String s = I18n.get("gui.factocrafty.heat",rBe.recipeHeat.get());
        renderScaled(graphics.pose(),s,  (imageWidth - (font.width(s) / 2)) / 2 + 4, 18,0.5F,0xFF9933,true);
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics,i,j);
        if (resultTank.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.resultTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        resultTank.drawAsFluidTank(graphics, rBe.resultTank.getFluidStack(), (int) rBe.resultTank.getMaxFluid(),true);
    }
}
