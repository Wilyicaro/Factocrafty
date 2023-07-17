package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.GasInfuserBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.IFactoryDrawableType;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class GasInfuserScreen extends BasicMachineScreen {


    public GasInfuserScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    GasInfuserBlockEntity gasBe = (GasInfuserBlockEntity) getMenu().be;

    private IFactoryDrawableType.DrawableStatic<IFactoryDrawableType.DrawableImage> ioTankType;

    private IFactoryDrawableType.DrawableStatic<IFactoryDrawableType.DrawableImage> oiTankType;
    protected String getRecipeTypeName(){
        return Registration.RECIPE_TYPES.getRegistrar().getId(gasBe.getRecipeType()).getPath();
    }
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generic_machine.png");
    @Override
    public ResourceLocation GUI() {
        return BACKGROUND_LOCATION;
    }
    @Override
    protected void init() {
        super.init();
        addWidget(new FactocraftyInfoWidget(leftPos - 20,  topPos + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", gasBe.getBlockState().getBlock().getName().getString()), null)).button =
                (x,y)->new FactocraftyDrawableButton(x + 2, y + 2,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(gasBe.getBlockPos(), gasBe.infusionMode.get() >= 1 ? 0 : 1, gasBe.additionalSyncInt.indexOf(gasBe.infusionMode))), Component.translatable("tooltip.factocrafty.config.infusion_mode."+ gasBe.getInfusionMode().getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())), FactocraftyDrawables.LARGE_BUTTON).icon( FactocraftyDrawables.getButtonIcon(gasBe.getInfusionMode().isMixer() ? 2 : 9));
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        fluidTankType = (gasBe.getInfusionMode().isMixer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (gasBe.getInfusionMode().isMixer() ? 44 : 112), topPos +  (gasBe.getInfusionMode().isElectrolyzer() ? 17 : 16));
        ioTankType = (gasBe.getInfusionMode().isMixer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (gasBe.getInfusionMode().isMixer() ? 68 : 141), topPos +  (gasBe.getInfusionMode().isElectrolyzer() ? 17 : 16));
        oiTankType = (gasBe.getInfusionMode().isElectrolyzer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (gasBe.getInfusionMode().isElectrolyzer() ? 56 : 112), topPos +  (gasBe.getInfusionMode().isMixer() ? 17 : 16));
        super.render(graphics, i, j, f);
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics,i,j);
        if (ioTankType.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", gasBe.ioTank),i, j);
        if (oiTankType.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", gasBe.oiTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        ScreenUtil.drawGUIFluidSlot(graphics,fluidTankType.posX - 1,fluidTankType.posY - 1,fluidTankType.width() + 2,fluidTankType.height() + 2);
        ScreenUtil.drawGUIFluidSlot(graphics, ioTankType.posX - 1, ioTankType.posY - 1, ioTankType.width() + 2, ioTankType.height() + 2);
        ScreenUtil.drawGUIFluidSlot(graphics, oiTankType.posX - 1, oiTankType.posY - 1, oiTankType.width() + 2, oiTankType.height() + 2);
        super.renderStorageSprites(graphics, i, j);
        ioTankType.drawAsFluidTank(graphics, gasBe.ioTank.getFluidStack(), gasBe.ioTank.getMaxFluid(),true);
        oiTankType.drawAsFluidTank(graphics, gasBe.oiTank.getFluidStack(), gasBe.oiTank.getMaxFluid(),true);

    }
}
