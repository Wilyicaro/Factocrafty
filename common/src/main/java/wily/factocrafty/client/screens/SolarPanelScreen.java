package wily.factocrafty.client.screens;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.SolarPanelBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.windows.SlotsWindow;
import wily.factocrafty.client.screens.widgets.windows.UpgradesWindow;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.base.IFactoryDrawableType;

import static wily.factoryapi.util.StorageStringUtil.*;


public class SolarPanelScreen extends FactocraftyMachineScreen<SolarPanelBlockEntity> {
    public SolarPanelScreen(FactocraftyProcessMenu<SolarPanelBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/solar_panel.png");}

    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        super.renderStorageTooltips(poseStack, i, j);
        if (IFactoryDrawableType.getMouseLimit(i,j,78, 31, 20,20)) renderTooltip(poseStack, Component.translatable("tooltip.factocrafty.generating",  getStorageAmount(getMenu().be.tickEnergy.get(),false,"",kiloCY,CYMeasure)).withStyle(ChatFormatting.GRAY), i,j);
    }

    @Override
    protected void init() {
        super.init();
        this.addConfigToGui(new FactocraftyConfigWidget(relX() + imageWidth,  relY() + 46, true,Component.translatable("gui.factocrafty.window.equipment"), new FactocraftyConfigWidget.Icons(1), this::renderTooltip)
                ,(config)-> new SlotsWindow(config,relX() + imageWidth + 21,relY(), this, menu.equipmentSlots));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        RenderSystem.setShaderTexture(0, WIDGETS);
        boolean isNight = minecraft.level.getSkyDarken(1.0F) < 0.25;
        Vec3 vec3 = minecraft.level.getSkyColor(Vec3.atCenterOf(getMenu().be.getBlockPos()), 1.0F);
        float f0 = (float)vec3.x;
        float f1 = (float)vec3.y;
        float f2 = (float)vec3.z;
        int dayTime =(int) minecraft.level.getDayTime();
        int day = dayTime - (isNight ? 12000 : 0) - 24000 * (dayTime  / 24000);

        RenderSystem.setShaderColor(f0, f1, f2, 1F);
        blit(poseStack, relX() + 78, relY() + 31,0,102, 20,20);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        blit(poseStack, relX() + 85, relY() + 25 + getProgressScaled(day, 12000, 32), isNight ? 6 * (minecraft.level.getMoonPhase() + 1) : 0,122, 6,6);
        super.renderBg(poseStack, f, i, j);
    }
}
