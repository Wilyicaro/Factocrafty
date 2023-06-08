package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.windows.SlotsWindow;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public class EnergyCellScreen extends FactocraftyMachineScreen<FactocraftyEnergyStorageBlockEntity> {
    public EnergyCellScreen(FactocraftyProcessMenu<FactocraftyEnergyStorageBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        energyCellPosX = 91;
        energyCellType = FactocraftyDrawables.BIG_ENERGY_CELL;

    }

    @Override
    protected void init() {
        super.init();
        this.addConfigToGui(new FactocraftyConfigWidget(relX() + imageWidth,  relY() + 46, true,Component.translatable("gui.factocrafty.window.equipment"), new FactocraftyConfigWidget.Icons(1), this::renderTooltip)
                ,(config)-> new SlotsWindow(config,relX() + imageWidth + 21,relY(), this, menu.equipmentSlots));
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/energy_cell.png");}

}
