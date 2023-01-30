package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class ElectricJetpackItem extends JetpackItem implements ICraftyEnergyItem<CYItemEnergyStorage> {
    public ElectricJetpackItem(FactoryCapacityTiers energyTier, ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, properties);
        this.energyTier = energyTier;

    }

    public FactoryCapacityTiers energyTier;

    @Override
    protected boolean canLaunchJetpack(ItemStack stack) {
        return getCraftyEnergy(stack).getEnergyStored() >= 1;
    }

    @Override
    protected ItemStack consumeFuel(ItemStack stack) {
        getCraftyEnergy(stack).consumeEnergy(1,false);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent());
        list.add(StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return getCraftyEnergy(itemStack).getSpace() > 0;
    }

    public int getBarWidth(ItemStack itemStack) {
        return Math.round(getCraftyEnergy(itemStack).getEnergyStored() * 13.0F / (float) this.getCraftyEnergy(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override
    public CYItemEnergyStorage getCraftyEnergy(ItemStack stack) {
        return new CYItemEnergyStorage(stack, 0, energyTier.energyCapacity  * energyTier.capacityMultiplier,TransportState.INSERT, energyTier);
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return energyTier;
    }

    ;
}

