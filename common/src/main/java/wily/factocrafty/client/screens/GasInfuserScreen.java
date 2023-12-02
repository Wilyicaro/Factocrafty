package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.GasInfuserBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class GasInfuserScreen extends BasicMachineScreen<GasInfuserBlockEntity> {


    public GasInfuserScreen(FactocraftyStorageMenu<GasInfuserBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    private DrawableStatic ioTankType;

    private DrawableStatic oiTankType;
    protected String getRecipeTypeName(){
        return Registration.RECIPE_TYPES.getRegistrar().getId(be.getRecipeType()).getPath();
    }
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generic_machine.png");
    @Override
    public ResourceLocation GUI() {
        return BACKGROUND_LOCATION;
    }
    @Override
    protected void init() {
        super.init();
        addNestedRenderable(new DrawableStatic( FactocraftyDrawables.MACHINE_BUTTON_LAYOUT,leftPos - 20,  topPos + 100));
    }
    public List<? extends Renderable> getNestedRenderables() {
        List<Renderable> list = new ArrayList<>(nestedRenderables);
        list.add(new FactoryDrawableButton(leftPos - 20 + 2, topPos + 102, FactocraftyDrawables.LARGE_BUTTON).tooltips(List.of(Component.translatable("tooltip.factocrafty.machine_config", title.getString()).append(":"),Component.translatable("tooltip.factocrafty.config.infusion_mode."+ be.getInfusionMode().getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())))).icon( FactocraftyDrawables.getButtonIcon(be.getInfusionMode().isMixer() ? 2 : 9)).onPress((b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(be.getBlockPos(), be.infusionMode.get() >= 1 ? 0 : 1, be.additionalSyncInt.indexOf(be.infusionMode)))));
        return list;
    }
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        fluidTankType = (be.getInfusionMode().isMixer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (be.getInfusionMode().isMixer() ? 44 : 112), topPos +  (be.getInfusionMode().isElectrolyzer() ? 17 : 16));
        ioTankType = (be.getInfusionMode().isMixer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (be.getInfusionMode().isMixer() ? 68 : 141), topPos +  (be.getInfusionMode().isElectrolyzer() ? 17 : 16));
        oiTankType = (be.getInfusionMode().isElectrolyzer() ? FactocraftyDrawables.MINI_FLUID_TANK : FactocraftyDrawables.FLUID_TANK).createStatic(leftPos + (be.getInfusionMode().isElectrolyzer() ? 56 : 112), topPos +  (be.getInfusionMode().isMixer() ? 17 : 16));
        super.render(graphics, i, j, f);
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics,i,j);
        if (ioTankType.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.ioTank),i, j);
        if (oiTankType.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.oiTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        ScreenUtil.drawGUIFluidSlot(graphics,fluidTankType.getX() - 1,fluidTankType.getY() - 1,fluidTankType.width() + 2,fluidTankType.height() + 2);
        ScreenUtil.drawGUIFluidSlot(graphics, ioTankType.getX() - 1, ioTankType.getY() - 1, ioTankType.width() + 2, ioTankType.height() + 2);
        ScreenUtil.drawGUIFluidSlot(graphics, oiTankType.getX() - 1, oiTankType.getY() - 1, oiTankType.width() + 2, oiTankType.height() + 2);
        super.renderStorageSprites(graphics, i, j);
        ioTankType.drawAsFluidTank(graphics, be.ioTank.getFluidStack(), be.ioTank.getMaxFluid(),true);
        oiTankType.drawAsFluidTank(graphics, be.oiTank.getFluidStack(), be.oiTank.getMaxFluid(),true);

    }
}
