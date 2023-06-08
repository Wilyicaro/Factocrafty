package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public class FluidTankScreen extends FactocraftyMachineScreen<FactocraftyFluidTankBlockEntity> {
    public FluidTankScreen(FactocraftyProcessMenu<FactocraftyFluidTankBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        fluidTankType = FactocraftyDrawables.BIG_FLUID_TANK;
        fluidTankPos = new int[]{81,17};

    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/fluid_tank.png");}

}
