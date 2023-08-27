package wily.factocrafty.client.screens;


import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.block.machines.entity.MaceratorBlockEntity;
import wily.factocrafty.block.machines.entity.ProcessMachineBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;

public class BasicMachineScreen<T extends ProcessMachineBlockEntity<?>> extends FactocraftyStorageScreen<T> {

    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/basic_machine.png");


    public BasicMachineScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    public static <BE extends ProcessMachineBlockEntity<?>, T extends BasicMachineScreen<BE>> MenuRegistry.ScreenFactory<FactocraftyStorageMenu<BE>,T> cast(){
        return (containerMenu, inventory1, component1) -> (T) new BasicMachineScreen<>(containerMenu, inventory1, component1);
    }
    @Override
    protected void init() {
        super.init();
        defaultProgress = FactocraftyDrawables.MACHINE_PROGRESS.createStatic(leftPos, topPos);
    }

    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

}
