package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class ElectricWrenchItem extends WrenchItem implements ICraftyEnergyItem<CYItemEnergyStorage> {


    private final FactoryCapacityTiers energyTier;

    public ElectricWrenchItem(FactoryCapacityTiers energyTier, Properties properties) {
        super(properties);
        this.energyTier = energyTier;
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(getEnergyTier().getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }

    @Override
    protected void whenUseWrench(int used, UseOnContext useOnContext) {
        getCraftyEnergy(useOnContext.getItemInHand()).consumeEnergy(new ICraftyEnergyStorage.EnergyTransaction(used, energyTier),false);
    }

    @Override
    public CYItemEnergyStorage getCraftyEnergy(ItemStack stack) {
        return new CYItemEnergyStorage(stack,0,getEnergyTier().energyCapacity, TransportState.INSERT, getEnergyTier());
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
    public FactoryCapacityTiers getEnergyTier() {
        return energyTier;
    }
}
