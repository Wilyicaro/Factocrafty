package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ElectricFurnaceBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;

public class ElectricFurnaceScreen extends FactocraftyStorageScreen<ElectricFurnaceBlockEntity> {
    public ElectricFurnaceScreen(FactocraftyStorageMenu<ElectricFurnaceBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/electric_furnace.png");
    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}
    @Override
    protected void init() {
        super.init();
        defaultProgress = FactocraftyDrawables.PROGRESS.createStatic(leftPos, topPos);
    }
}
