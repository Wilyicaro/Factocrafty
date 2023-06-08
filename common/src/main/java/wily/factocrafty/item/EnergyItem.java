package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class EnergyItem extends Item implements ICraftyEnergyItem<CYItemEnergyStorage> {
    public EnergyItem(FactoryCapacityTiers tier, TransportState canIE, Properties properties) {
        super(properties);
        energyTier = tier;
        energyState = canIE;
        capacity = energyTier.energyCapacity;
    }
    public FactoryCapacityTiers energyTier;
    public TransportState energyState;


    public int capacity = 1000;

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getCraftyEnergy(itemStack).getSpace() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getCraftyEnergy(itemStack).getEnergyStored() * 13.0F / (float)this.getCraftyEnergy(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override
    public CYItemEnergyStorage getCraftyEnergy(ItemStack stack) {
        return new CYItemEnergyStorage(stack,0,capacity, energyState, energyTier);
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return energyTier;
    }
}
