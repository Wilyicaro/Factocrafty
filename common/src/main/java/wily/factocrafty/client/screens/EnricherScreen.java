package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.EnricherBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.DrawableStaticProgress;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.util.ScreenUtil;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class EnricherScreen extends ChangeableInputMachineScreen<EnricherBlockEntity>{
    public EnricherScreen(FactocraftyStorageMenu<EnricherBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        imageHeight = 171;
        inventoryLabelY += 7;
    }
    private DrawableStaticProgress matterProgress;
    private DrawableStatic resultTank;

    @Override
    protected void init() {
        super.init();

        addNestedRenderable(new FactoryDrawableButton(leftPos + 157,topPos + 71,FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(2)).tooltip( Component.translatable("tooltip.factocrafty.config.eject")).onPress((b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncProgressPacket(be.getBlockPos(), be.getProgresses().indexOf(be.matterAmount), be.matterAmount.getValues()))));
        matterProgress = FactocraftyDrawables.MATTER_PROGRESS.createStatic(leftPos + 20, topPos + 74);
        resultTank = FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 138, topPos + 17);
    }

    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/enricher.png");
    @Override
    public ResourceLocation GUI() {
        return BACKGROUND_LOCATION;
    }
    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics,i,j);
        if (resultTank.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.resultTank),i, j);
        if (matterProgress.inMouseLimit(i,j)) graphics.renderTooltip(font, be.getMatterMaterial().isEmpty() ?   be.getMatterMaterial().getComponent() : Component.translatable("tooltip.factocrafty.matter", be.getMatterMaterial().getComponent().getString(), be.matterAmount.first().get()),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        int c = be.getMatterMaterial().getColor().col;
        RenderSystem.setShaderColor(ScreenUtil.getRed(c),ScreenUtil.getGreen(c),ScreenUtil.getBlue(c),1.0F);
        matterProgress.drawProgress(graphics, be.matterAmount.first().get(), be.matterAmount.first().maxProgress);
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        super.renderStorageSprites(graphics, i, j);
        resultTank.drawAsFluidTank(graphics, be.resultTank.getFluidStack(),(int) be.resultTank.getMaxFluid(), true);
    }
}
