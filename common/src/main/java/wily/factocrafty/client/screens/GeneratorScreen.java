package wily.factocrafty.client.screens;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.client.screens.widgets.windows.SlotsWindow;
import wily.factocrafty.inventory.FactocraftyProcessMenu;

import static wily.factoryapi.util.StorageStringUtil.*;

public class GeneratorScreen extends FactocraftyMachineScreen<GeneratorBlockEntity> {
    public GeneratorScreen(FactocraftyProcessMenu<GeneratorBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }

    @Override
    protected void init() {
        super.init();
        energyCellType.posX = leftPos + 112;
        this.addConfigToGui(new FactocraftyConfigWidget(leftPos + imageWidth,  topPos + 46, true,Component.translatable("gui.factocrafty.window.equipment"), FactocraftyDrawables.getInfoIcon(1))
                ,(config)-> new SlotsWindow(config,leftPos + imageWidth + 21,topPos, this, menu.equipmentSlots));
        addWidget(new FactocraftyInfoWidget(leftPos + imageWidth,  topPos +  100,238 , 18,()->Component.translatable("tooltip.factocrafty.generating",  getStorageAmount(getMenu().be.energyTick.get(),false,"",kiloCY,CYMeasure)).withStyle(ChatFormatting.GRAY), FactocraftyDrawables.getInfoIcon(4)));
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generator.png");}

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        FactocraftyDrawables.BURN_PROGRESS.drawProgress(graphics,leftPos + 56, topPos + 36,getMenu().be.burnTime.getInt(0), getMenu().be.burnTime.maxProgress);
        FactocraftyDrawables.ENERGY_PROGRESS.drawProgress(graphics,leftPos + 80, topPos + 39, getMenu().be.progress.get()[0],getMenu().be.progress.maxProgress);
        FactocraftyDrawables.MINI_FLUID_TANK.drawAsFluidTank(graphics,leftPos + 56, topPos + 14, getMenu().be.fluidTank.getFluidStack(),(int) getMenu().be.fluidTank.getMaxFluid(), true);
    }

}
