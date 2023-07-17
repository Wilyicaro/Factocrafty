package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.GeothermalGeneratorBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.base.IFactoryDrawableType;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class GeothermalGeneratorScreen extends GeneratorScreen {
    public GeothermalGeneratorScreen(FactocraftyProcessMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    private IFactoryDrawableType.DrawableStatic<?> lavaTank;

    @Override
    protected void init() {
        super.init();
        lavaTank= FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 17, topPos + 17);
    }

    GeothermalGeneratorBlockEntity gBe = (GeothermalGeneratorBlockEntity) getMenu().be;
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/geothermal_generator.png");}

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics, i, j);
        if (!getMenu().storage.getTanks().isEmpty() && lavaTank.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", gBe.lavaTank),i, j);
    }

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        lavaTank.drawAsFluidTank(graphics, gBe.lavaTank.getFluidStack(),(int) gBe.lavaTank.getMaxFluid(), true);
    }

}
