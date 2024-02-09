package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyStorageItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class EnergyItem extends Item implements ICraftyStorageItem {
    public EnergyItem(FactoryCapacityTiers tier, TransportState canIE, Properties properties) {
        super(properties);
        energyTier = tier;
        energyState = canIE;
        capacity = energyTier.initialCapacity;
    }
    public FactoryCapacityTiers energyTier;
    public TransportState energyState;


    public int capacity;

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getEnergyStorage(itemStack)));
    }
    public float getChargedLevel(ItemStack stack){
        int e = getEnergyStorage(stack).getEnergyStored();
        if (e > 0 ) {
            return e /(float)capacity;
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getEnergyStorage(itemStack).getEnergySpace() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getEnergyStorage(itemStack).getEnergyStored() * 13.0F / (float)this.getEnergyStorage(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public TransportState getTransport() {
        return energyState;
    }

    @Override
    public FactoryCapacityTiers getSupportedEnergyTier() {
        return energyTier;
    }


}
