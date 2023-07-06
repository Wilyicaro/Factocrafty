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
    }

    @Override
    protected void init() {
        super.init();
        fluidTankType.drawable = FactocraftyDrawables.BIG_FLUID_TANK;
        fluidTankType.posX = leftPos + 81;
        fluidTankType.posY = topPos + 17;
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/fluid_tank.png");}

}
