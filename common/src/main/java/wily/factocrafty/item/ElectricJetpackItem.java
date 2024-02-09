package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyStorageItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class ElectricJetpackItem extends JetpackItem implements ICraftyStorageItem {
    public ElectricJetpackItem(FactoryCapacityTiers energyTier, ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, properties);
        this.energyTier = energyTier;
    }

    public FactoryCapacityTiers energyTier;

    @Override
    public boolean canLaunchJetpack(ItemStack stack) {
        return getEnergyStorage(stack).getEnergyStored() >= 1;
    }

    @Override
    public ItemStack consumeFuel(ItemStack stack) {
        getEnergyStorage(stack).consumeEnergy(1,false);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent(false));
        list.add(StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getEnergyStorage(itemStack)));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return getEnergyStorage(itemStack).getEnergySpace() > 0;
    }

    public int getBarWidth(ItemStack itemStack) {
        return Math.round(getEnergyStorage(itemStack).getEnergyStored() * 13.0F / (float) this.getEnergyStorage(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override
    public FactoryCapacityTiers getSupportedEnergyTier() {
        return energyTier;
    }
    @Override
    public TransportState getTransport() {
        return TransportState.INSERT;
    }

    @Override
    public int getCapacity() {
        return energyTier.initialCapacity  * energyTier.capacityMultiplier;
    }
}

