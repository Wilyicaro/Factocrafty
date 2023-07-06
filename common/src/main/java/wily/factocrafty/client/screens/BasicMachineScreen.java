package wily.factocrafty.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.util.ProgressElementRenderUtil;

public class BasicMachineScreen extends FactocraftyMachineScreen<FactocraftyMachineBlockEntity> {

    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/basic_machine.png");

    public BasicMachineScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public static BasicMachineScreen create(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        BasicMachineScreen b = new BasicMachineScreen(abstractContainerMenu,inventory,component);
        return  b;
    }
    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        FactocraftyDrawables.MACHINE_PROGRESS.drawProgress(graphics,leftPos + 80, topPos + 40, getMenu().be.progress.get()[0],getMenu().be.getTotalProcessTime());
    }
}
