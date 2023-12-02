package wily.factocrafty.client.screens;


import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.FluidPumpBlockEntity;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;

import java.util.ArrayList;
import java.util.List;

public class FluidPumpScreen extends FactocraftyStorageScreen<FluidPumpBlockEntity> {

    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/fluid_pump.png");

    public FluidPumpScreen(FactocraftyStorageMenu<FluidPumpBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        fluidTankType = FactocraftyDrawables.FLUID_TANK.createStatic(leftPos + 109, topPos + 17);
        defaultProgress = FactocraftyDrawables.MACHINE_PROGRESS.createStatic(leftPos, topPos);
        addNestedRenderable(new DrawableStatic( FactocraftyDrawables.MACHINE_BUTTON_LAYOUT,leftPos - 20,  topPos + 100));
    }
    public List<? extends Renderable> getNestedRenderables() {
        List<Renderable> list = new ArrayList<>(nestedRenderables);
        list.add(new FactoryDrawableButton(leftPos - 18, topPos + 102, FactocraftyDrawables.LARGE_BUTTON).tooltips(List.of(Component.translatable("tooltip.factocrafty.machine_config", title.getString()),Component.translatable("tooltip.factocrafty.config.pump_mode."+ TransportState.values()[menu.be.pumpMode.get()]))).icon( FactocraftyDrawables.getButtonIcon(4 + menu.be.pumpMode.get())).onPress((b, i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket( menu.be.getBlockPos(),menu.be.pumpMode.get() >= 3 ? 0 : menu.be.pumpMode.get() + 1, menu.be.additionalSyncInt.indexOf(menu.be.pumpMode)))));
        return list;
    }
    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

}
