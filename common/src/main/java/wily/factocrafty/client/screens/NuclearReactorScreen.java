package wily.factocrafty.client.screens;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.NuclearReactorBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyScreenWindow;
import wily.factocrafty.client.screens.widgets.ReactorChamberWindow;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

import java.util.List;
import java.util.stream.IntStream;

public class NuclearReactorScreen extends GeneratorScreen<NuclearReactorBlockEntity> {
    public NuclearReactorScreen(FactocraftyStorageMenu<NuclearReactorBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        drawableBurn = FactocraftyDrawables.TEMPERATURE_PROGRESS;

    }

    @Override
    protected void init() {
        super.init();
        fluidTankType = FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 46,topPos + 17);
        this.addWindowToGui(new FactoryDrawableButton(leftPos - 18,  topPos + 43, FactocraftyDrawables.MACHINE_CONFIG_BUTTON).icon(FactocraftyDrawables.getInfoIcon(5)).tooltip(Component.translatable("tooltip.factory_api.config.identifier.principal_chamber"))
                ,(config)-> new ReactorChamberWindow(config, leftPos - 88,this, IntStream.range(2,20).toArray()));
        this.addWindowToGui(new FactoryDrawableButton(leftPos - 18,  topPos + 67, FactocraftyDrawables.MACHINE_CONFIG_BUTTON).icon(FactocraftyDrawables.getInfoIcon(5)).visible(menu.be::hasSecondChamber).tooltip(Component.translatable("tooltip.factory_api.config.identifier.second_chamber"))
                ,(config)-> new ReactorChamberWindow(config, leftPos - 88,this, IntStream.range(20,38).toArray()));
        this.addWindowToGui(new FactoryDrawableButton(leftPos - 18,  topPos + 91, FactocraftyDrawables.MACHINE_CONFIG_BUTTON).icon(FactocraftyDrawables.getInfoIcon(5)).visible(menu.be::hasThirdChamber).tooltip(Component.translatable("tooltip.factory_api.config.identifier.third_chamber"))
                ,(config)-> new ReactorChamberWindow(config, leftPos - 88,this, IntStream.range(38,56).toArray()));
        this.addWindowToGui(new FactoryDrawableButton(leftPos - 18,  topPos + 115, FactocraftyDrawables.MACHINE_CONFIG_BUTTON).icon(FactocraftyDrawables.getInfoIcon(2)).tooltip(Component.translatable("tooltip.factocrafty.machine_config", title.getString()))
                ,(config)-> new FactocraftyScreenWindow<>(config, leftPos + (imageWidth - 121) / 2, config.getY(),121,80, this){
                    @Override
                    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                        super.renderWidget(guiGraphics, i, j, f);
                        Component injectionRate = Component.translatable("gui.factocrafty.window.injection_rate", menu.be.injectionRate.get());
                        guiGraphics.drawString(font,injectionRate,getX()+ (width - font.width(injectionRate)) / 2, getY() + 46, 0x404040,false);
                    }

                    @Override
                    public List<Renderable> getNestedRenderables() {
                        List<Renderable> renderables = super.getNestedRenderables();
                        renderables.add(new FactoryDrawableButton(getX() + 54,getY() + 32,FactocraftyDrawables.MEDIUM_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(10)).onPress((b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(menu.getBlockPos(),Math.min(20, menu.be.injectionRate.get() + 1), menu.be.additionalSyncInt.indexOf(menu.be.injectionRate)))));
                        renderables.add(new FactoryDrawableButton(getX() + 54,getY() + 56,FactocraftyDrawables.MEDIUM_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(11)).onPress((b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(menu.getBlockPos(),Math.max(0, menu.be.injectionRate.get() - 1), menu.be.additionalSyncInt.indexOf(menu.be.injectionRate)))));
                        return renderables;
                    }
                });
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        super.renderStorageTooltips(graphics, i, j);
        if (drawableBurn.inMouseLimit(i,j,leftPos + be.burnTime.first().x,topPos + be.burnTime.first().y))
            graphics.renderTooltip(font,Component.translatable("gui.factocrafty.heat",be.burnTime.first().get()), i, j);
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/nuclear_generator.png");}


}
