package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.FactocraftyEnergyTransformerBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.client.FactoryDrawableButton;
import wily.factoryapi.base.client.IFactoryDrawableType;

import java.util.List;
import java.util.function.Supplier;

import static wily.factoryapi.util.StorageStringUtil.*;

public class EnergyTransformerScreen extends FactocraftyStorageScreen<FactocraftyEnergyTransformerBlockEntity> {


    public EnergyTransformerScreen(FactocraftyStorageMenu<FactocraftyEnergyTransformerBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    protected Supplier<IFactoryDrawableType.DrawableStaticProgress> convertedEnergyCellType;

    @Override
    protected void init() {
        super.init();
        convertedEnergyCellType = ()-> FactocraftyDrawables.PLATFORM_ENERGY_CELL.createStatic(leftPos + 112, topPos+ 17);
        defaultProgress = FactocraftyDrawables.TRANSFORMER_PROGRESS.createStatic(leftPos,topPos);
        addWidget(new FactocraftyInfoWidget(leftPos - 20,  topPos + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", getTitle().getString()), null)).button =
                (x,y)->new FactoryDrawableButton(x + 2, y + 2,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(be.getBlockPos(), be.conversionMode.get() >= 1 ? 0: 1, be.additionalSyncInt.indexOf(be.conversionMode))), be.getConversionMode().getComponent(), FactocraftyDrawables.LARGE_BUTTON).icon(FactocraftyDrawables.getButtonIcon(be.getConversionMode().isPlatform() ? 11 : 10));
    }

    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        List<Component> energyComponents = getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),be.energyStorage);
        energyComponents.add(FactoryCapacityTiers.values()[Math.min(be.getConversionTier().ordinal(),be.energyStorage.storedTier.ordinal())].getOutputTierComponent());
        if (energyCellType.inMouseLimit(i,j))graphics.renderComponentTooltip(font, energyComponents, i, j);
        if (convertedEnergyCellType.get().inMouseLimit(i, j)){
            if (be.getConversionMode().isPlatform()) graphics.renderTooltip(font, getEnergyTooltip("tooltip.factory_api.energy_stored",be.platformEnergyStorage), i, j);
        }
    }

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/energy_"+ ( be.getConversionMode().isPlatform() ? "transformer" : "cell") + ".png");}

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        energyCellType = be.getConversionMode().isPlatform() ?  FactocraftyDrawables.ENERGY_CELL.createStatic(leftPos + 48, topPos + 17) : FactocraftyDrawables.BIG_ENERGY_CELL.createStatic(leftPos + 91, topPos + 17);
        super.renderStorageSprites(graphics, i, j);
        if (be.getConversionMode().isPlatform())
            convertedEnergyCellType.get().drawProgress(graphics,be.platformEnergyStorage.getEnergyStored(),be.platformEnergyStorage.getMaxEnergyStored());
    }
}
