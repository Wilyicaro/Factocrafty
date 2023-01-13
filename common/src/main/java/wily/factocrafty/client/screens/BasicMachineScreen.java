package wily.factocrafty.client.screens;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.CompoundResultMachineBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.util.ProgressElementRenderUtil;

public class BasicMachineScreen extends FactocraftyMachineScreen<FactocraftyMachineBlockEntity> {

    protected boolean hasAdditionalResultSlot = true;
    public BasicMachineScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        energyCellPosX = 20;

    }
    public static BasicMachineScreen create(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        BasicMachineScreen b = new BasicMachineScreen(abstractContainerMenu,inventory,component);
        b.hasAdditionalResultSlot = false;
        return  b;
    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/basic_machine.png");}

    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        ProgressElementRenderUtil.renderDefaultProgress(poseStack,this,relX() + 80, relY() + 40, getProgressScaled(getMenu().be.progress.get()[0],getMenu().be.getTotalProcessTime(), 21), FactocraftyProgressType.MACHINE_PROGRESS);

        if (hasAdditionalResultSlot) {
            blit(poseStack,relX() + 106 ,relY() + 34,178,0 ,18,18);
            blit(poseStack,relX() + 124 ,relY() + 30,196,0 ,26,26);
        }else {
            blit(poseStack,relX() + 111 ,relY() + 30,196,0 ,26,26);
        }
    }
}
