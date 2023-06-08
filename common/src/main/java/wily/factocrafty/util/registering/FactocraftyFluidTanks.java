package wily.factocrafty.util.registering;

import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

public enum FactocraftyFluidTanks implements IFactocraftyLazyRegistry<FactocraftyFluidTankBlock> {
    BASIC_FLUID_TANK(FactoryCapacityTiers.BASIC),ADVANCED_FLUID_TANK(FactoryCapacityTiers.ADVANCED),
    HIGH_FLUID_TANK(FactoryCapacityTiers.HIGH),ULTIMATE_FLUID_TANK(FactoryCapacityTiers.ULTIMATE),
    QUANTUM_FLUID_TANK(FactoryCapacityTiers.QUANTUM);
    public final FactoryCapacityTiers capacityTier;
    FactocraftyFluidTanks(FactoryCapacityTiers tier){
        capacityTier = tier;
    }
     public static FactocraftyFluidTankBlock getFromTier( FactoryCapacityTiers capacityTier){
        for (FactocraftyFluidTanks tank : values()) if (tank.capacityTier == capacityTier) return tank.get();
        return null;
     }
    @Override
    public FactocraftyFluidTankBlock get() {
        return (FactocraftyFluidTankBlock) Registration.getRegistrarBlockEntry(getName());
    }
}
