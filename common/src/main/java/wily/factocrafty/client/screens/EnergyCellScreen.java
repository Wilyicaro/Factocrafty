package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public class EnergyCellScreen extends FactocraftyMachineScreen<FactocraftyEnergyStorageBlockEntity> {
    public EnergyCellScreen(FactocraftyProcessMenu<FactocraftyEnergyStorageBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        energyCellPosX = 91;
        energyCellType = FactocraftyProgressType.BIG_ENERGY_CELL;

    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/energy_cell.png");}

}
