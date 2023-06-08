package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.GeothermalGeneratorBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class GeothermalGeneratorScreen extends GeneratorScreen {
    public GeothermalGeneratorScreen(FactocraftyProcessMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    GeothermalGeneratorBlockEntity gBe = (GeothermalGeneratorBlockEntity) getMenu().be;
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/geothermal_generator.png");}

    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        super.renderStorageTooltips(poseStack, i, j);
        if (!getMenu().storage.getTanks().isEmpty() && FactocraftyDrawables.FLUID_TANK.inMouseLimit(i,j,   17,  17)) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", gBe.lavaTank),i, j);
    }

    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        FactocraftyDrawables.FLUID_TANK.drawAsFluidTank(poseStack,relX() + 17, relY() + 17, getProgressScaled((int) gBe.lavaTank.getFluidStack().getAmount(), (int) gBe.lavaTank.getMaxFluid(), 52), gBe.lavaTank.getFluidStack(), true);
    }

}
