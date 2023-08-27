package wily.factocrafty.client.screens;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.client.FactoryDrawableButton;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class ChangeableInputMachineScreen<T extends ChangeableInputMachineBlockEntity> extends BasicMachineScreen<T> {


    public ChangeableInputMachineScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    public static MenuRegistry.ScreenFactory<FactocraftyStorageMenu<ChangeableInputMachineBlockEntity>,ChangeableInputMachineScreen<ChangeableInputMachineBlockEntity>> extractor(){
        return ChangeableInputMachineScreen::new;
    }
    protected String getRecipeTypeName(){
        String s = Registration.RECIPE_TYPES.getRegistrar().getId(be.getRecipeType()).getPath();
        return s.contains("_") ? s.split("_")[0] : s;
    }
    @Override
    protected void init() {
        super.init();
        addWidget(new FactocraftyInfoWidget(leftPos - 20,  topPos + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", be.getBlockState().getBlock().getName().getString()), null)).button =
                (x,y)-> new FactoryDrawableButton(x + 2, y + 2,(b)-> Factocrafty.NETWORK.sendToServer( new FactocraftySyncIntegerBearerPacket(be.getBlockPos(), be.inputType.get() >= 1 ? 0: 1, be.additionalSyncInt.indexOf(be.inputType))), Component.translatable("tooltip.factocrafty.config.input_type."+ be.getInputType().getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())), FactocraftyDrawables.LARGE_BUTTON).icon( FactocraftyDrawables.getButtonIcon(be.getInputType().isItem() ? 8 : 2));
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        if (energyCellType.inMouseLimit(i,j)) graphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),be.energyStorage),i, j);
        else if (fluidTankType.inMouseLimit(i,j) && be.getInputType().isFluid()) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.fluidTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        if (be.getInputType().isFluid())
            ScreenUtil.drawGUIFluidSlot(graphics,leftPos + 55, topPos + 13,18,21);
        super.renderStorageSprites(graphics, i, j);
    }
}
