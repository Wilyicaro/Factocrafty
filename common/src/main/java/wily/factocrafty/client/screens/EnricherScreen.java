package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.EnricherBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.List;
import java.util.Map;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class EnricherScreen extends ChangeableInputMachineScreen{
    public EnricherScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        imageHeight = 171;
        inventoryLabelY += 7;
    }
    private IFactoryDrawableType.DrawableStaticProgress matterProgress;
    private IFactoryDrawableType.DrawableStatic<IFactoryDrawableType.DrawableImage> resultTank;

    @Override
    protected void init() {
        super.init();
        matterProgress = FactocraftyDrawables.MATTER_PROGRESS.createStatic(leftPos + 20, topPos + 74);
        resultTank = FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 138, topPos + 17);
    }

    @Override
    public List<FactocraftyDrawableButton> addButtons(List<FactocraftyDrawableButton> list) {
        list.add(new FactocraftyDrawableButton(leftPos + 157,topPos + 71,(i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncProgressPacket(eBe.getBlockPos(), eBe.getProgresses().indexOf(eBe.matterAmount),new int[1],eBe.matterAmount.maxProgress)), Component.translatable("tooltip.factocrafty.config.eject"),FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(2)));
        return super.addButtons(list);
    }


    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/enricher.png");
    @Override
    public ResourceLocation GUI() {
        return BACKGROUND_LOCATION;
    }
    EnricherBlockEntity eBe = (EnricherBlockEntity) getMenu().be;
    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics,i,j);
        if (resultTank.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.resultTank),i, j);
        if (matterProgress.inMouseLimit(i,j)) graphics.renderTooltip(font, eBe.getMatterMaterial().isEmpty() ?   eBe.getMatterMaterial().getComponent() : Component.translatable("tooltip.factocrafty.matter", eBe.getMatterMaterial().getComponent().getString(),eBe.matterAmount.get()[0]),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        int c = eBe.getMatterMaterial().getColor().col;
        RenderSystem.setShaderColor(ScreenUtil.getRed(c),ScreenUtil.getGreen(c),ScreenUtil.getBlue(c),1.0F);
        matterProgress.drawProgress(graphics,eBe.matterAmount.get()[0], eBe.matterAmount.maxProgress);
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        super.renderStorageSprites(graphics, i, j);
        resultTank.drawAsFluidTank(graphics, rBe.resultTank.getFluidStack(),(int) rBe.resultTank.getMaxFluid(), true);
    }
}
