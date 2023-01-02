package wily.factocrafty.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.util.ProgressElementRenderUtil;

public class GeneratorScreen extends FactocraftyMachineScreen<GeneratorBlockEntity> {
    public GeneratorScreen(FactocraftyProcessMenu<GeneratorBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generator.png");}

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        super.renderBg(poseStack, f, i, j);
        ProgressElementRenderUtil.renderDefaultProgress(poseStack,this,relX() + 56, relY() + 36, getProgressScaled(getMenu().be.burnTime.getInt(0), getMenu().be.burnTime.maxProgress, 14), FactocraftyProgressType.BURN_PROGRESS);
        ProgressElementRenderUtil.renderDefaultProgress(poseStack,this,relX() + 80, relY() + 39, getProgressScaled(getMenu().be.progress.get()[0],getMenu().be.progress.maxProgress, 22), FactocraftyProgressType.ENERGY_PROGRESS);
        ProgressElementRenderUtil.renderFluidTank(poseStack,this,relX() + 56, relY() + 14, getProgressScaled((int) getMenu().be.fluidTank.getFluidStack().getAmount(), (int) getMenu().be.fluidTank.getMaxFluid(), 19), FactocraftyProgressType.MINI_FLUID_TANK, getMenu().be.fluidTank.getFluidStack(), true);

    }
}
