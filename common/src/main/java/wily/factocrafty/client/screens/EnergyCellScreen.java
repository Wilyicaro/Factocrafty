package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.client.screens.widgets.SlotsWindow;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

public class EnergyCellScreen extends FactocraftyStorageScreen<FactocraftyEnergyStorageBlockEntity> {
    public EnergyCellScreen(FactocraftyStorageMenu<FactocraftyEnergyStorageBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        energyCellType.setX(leftPos + 91);
        energyCellType.drawable = FactocraftyDrawables.BIG_ENERGY_CELL;
        this.addWindowToGui(new FactoryDrawableButton(leftPos + imageWidth - 3,  topPos + 46, FactocraftyDrawables.MACHINE_CONFIG_BUTTON_INVERTED).grave(1.0F).icon(FactocraftyDrawables.getInfoIcon(1)).tooltip(Component.translatable("gui.factocrafty.window.equipment"))
                ,(config)-> new SlotsWindow(config,leftPos + imageWidth + 21,topPos, this, menu.equipmentSlots));
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/energy_cell.png");}

}
