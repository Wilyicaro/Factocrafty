package wily.factocrafty.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ElectricFurnaceBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

public class ElectricFurnaceScreen extends FactocraftyMachineScreen<ElectricFurnaceBlockEntity> {
    public ElectricFurnaceScreen(FactocraftyProcessMenu<ElectricFurnaceBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        energyCellPosX = 20;

    }
    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/electric_furnace.png");
    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        FactocraftyDrawables.PROGRESS.drawProgress(poseStack,relX() + 79, relY() + 35, getProgressScaled(getMenu().be.progress.get()[0],getMenu().be.getTotalProcessTime(), 24));
    }
}
