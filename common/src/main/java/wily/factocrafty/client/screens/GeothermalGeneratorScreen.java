package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.GeothermalGeneratorBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class GeothermalGeneratorScreen extends GeneratorScreen<GeothermalGeneratorBlockEntity> {
    public GeothermalGeneratorScreen(FactocraftyStorageMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    private DrawableStatic lavaTank;

    @Override
    protected void init() {
        super.init();
        lavaTank= FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 17, topPos + 17);
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/geothermal_generator.png");}

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics, i, j);
        if (!getMenu().getBlockEntity().getTanks().isEmpty() && lavaTank.inMouseLimit(i,j)) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.lavaTank),i, j);
    }

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        lavaTank.drawAsFluidTank(graphics, be.lavaTank.getFluidStack(),(int) be.lavaTank.getMaxFluid(), true);
    }

}
