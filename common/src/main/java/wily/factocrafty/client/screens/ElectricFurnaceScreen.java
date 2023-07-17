package wily.factocrafty.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ElectricFurnaceBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public class ElectricFurnaceScreen extends FactocraftyMachineScreen<ElectricFurnaceBlockEntity> {
    public ElectricFurnaceScreen(FactocraftyProcessMenu<ElectricFurnaceBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/electric_furnace.png");
    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}
    @Override
    protected void init() {
        super.init();
        machineProgress = FactocraftyDrawables.PROGRESS.createStatic(leftPos + 79, topPos + 35);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        machineProgress.drawProgress(graphics, getMenu().be.progress.get()[0],getMenu().be.getTotalProcessTime());
    }
}
