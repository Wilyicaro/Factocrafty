package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ProcessMachineBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;

public class BasicMachineScreen extends FactocraftyStorageScreen<ProcessMachineBlockEntity> {

    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/basic_machine.png");

    public BasicMachineScreen(FactocraftyStorageMenu<ProcessMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        defaultProgress = FactocraftyDrawables.MACHINE_PROGRESS.createStatic(leftPos, topPos);
    }

    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

}
