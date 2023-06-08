package wily.factocrafty.client.screens;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
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
        this.addConfigToGui(new FactocraftyConfigWidget(relX() + imageWidth,  relY() + 46, true,Component.translatable("gui.factocrafty.window.equipment"), new FactocraftyConfigWidget.Icons(1), this::renderTooltip)
                ,(config)-> new SlotsWindow(config,relX() + imageWidth + 21,relY(), this, menu.equipmentSlots));
        addWidget(new FactocraftyInfoWidget(relX() + imageWidth,  relY() +  100,238 , 18,()->Component.translatable("tooltip.factocrafty.generating",  getStorageAmount(getMenu().be.energyTick.get(),false,"",kiloCY,CYMeasure)).withStyle(ChatFormatting.GRAY), new FactocraftyConfigWidget.Icons(4), this::renderTooltip));
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generator.png");}

    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        FactocraftyDrawables.BURN_PROGRESS.drawProgress(poseStack,relX() + 56, relY() + 36, getProgressScaled(getMenu().be.burnTime.getInt(0), getMenu().be.burnTime.maxProgress, 14));
        FactocraftyDrawables.ENERGY_PROGRESS.drawProgress(poseStack,relX() + 80, relY() + 39, getProgressScaled(getMenu().be.progress.get()[0],getMenu().be.progress.maxProgress, 22));
        FactocraftyDrawables.MINI_FLUID_TANK.drawAsFluidTank(poseStack,relX() + 56, relY() + 14, getProgressScaled((int) getMenu().be.fluidTank.getFluidStack().getAmount(), (int) getMenu().be.fluidTank.getMaxFluid(), 19), getMenu().be.fluidTank.getFluidStack(), true);

    }

}
