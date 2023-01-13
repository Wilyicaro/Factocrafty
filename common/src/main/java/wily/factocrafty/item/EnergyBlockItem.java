package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class EnergyBlockItem extends BlockItem implements ICraftyEnergyItem<CYItemEnergyStorage> {
    public EnergyBlockItem(Block block, FactoryCapacityTiers tier, TransportState canIE, Properties properties) {
        super(block,properties);
        energyTier = tier;
        energyState = canIE;
    }
    public FactoryCapacityTiers energyTier;
    public TransportState energyState;


    public int capacity = 1000;

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
      list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getCraftyEnergy(itemStack).getEnergyStored() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getCraftyEnergy(itemStack).getEnergyStored() * 13.0F / (float)this.getCraftyEnergy(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override public CYItemEnergyStorage getCraftyEnergy(ItemStack stack) {
        return new CYItemEnergyStorage(stack, 0, energyTier.getStorageCapacity(), TransportState.EXTRACT_INSERT,energyTier,true);
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return energyTier;
    }
}
