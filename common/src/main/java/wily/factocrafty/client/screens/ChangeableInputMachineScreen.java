package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncInputTypePacket;
import wily.factocrafty.util.ScreenUtil;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class ChangeableInputMachineScreen extends BasicMachineScreen {


    public ChangeableInputMachineScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    ChangeableInputMachineBlockEntity rBe = (ChangeableInputMachineBlockEntity) getMenu().be;

    protected String getRecipeTypeName(){
        String s = Registration.RECIPE_TYPES.getRegistrar().getId(rBe.recipeType).getPath();
        return s.contains("_") ? s.split("_")[0] : s;
    }
    @Override
    protected void init() {
        super.init();
        addWidget(new FactocraftyInfoWidget(leftPos - 20,  topPos + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", rBe.getBlockState().getBlock().getName().getString()), null)).button =
                (x,y)->new FactocraftyDrawableButton(x + 2, y + 2,(b)-> Factocrafty.NETWORK.sendToServer( new FactocraftySyncInputTypePacket( rBe.getBlockPos(),rBe.inputType = ChangeableInputMachineBlockEntity.InputType.values()[rBe.inputType.ordinal() >= 1 ? 0: 1] )), Component.translatable("tooltip.factocrafty.config.input_type."+ rBe.inputType.getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())), FactocraftyDrawables.LARGE_BUTTON).icon( FactocraftyDrawables.getButtonIcon(rBe.inputType.isItem() ? 8 : 2));
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        if (energyCellType.inMouseLimit(i,j)) graphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", rBe.energyStorage),i, j);
        else if (fluidTankType.inMouseLimit(i,j) && rBe.inputType.isFluid()) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.fluidTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        if (rBe.inputType.isFluid())
            ScreenUtil.drawGUIFluidSlot(graphics,leftPos + 55, topPos + 13,18,21);
        super.renderStorageSprites(graphics, i, j);
    }
}
