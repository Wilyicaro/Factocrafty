package wily.factocrafty.client.screens;


import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.FluidPumpBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.TransportState;

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
        addWidget(new FactocraftyInfoWidget(leftPos - 20,  topPos + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", menu.be.getBlockState().getBlock().getName().getString()), null)).button =
                (x,y)->new FactocraftyDrawableButton(x + 2, y + 2,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket( menu.be.getBlockPos(),menu.be.pumpMode.get() >= 3 ? 0 : menu.be.pumpMode.get() + 1, menu.be.additionalSyncInt.indexOf(menu.be.pumpMode))), Component.translatable("tooltip.factocrafty.config.pump_mode."+ TransportState.values()[menu.be.pumpMode.get()]), FactocraftyDrawables.LARGE_BUTTON).icon( FactocraftyDrawables.getButtonIcon(4 + menu.be.pumpMode.get()));
    }

    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

}
