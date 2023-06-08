package wily.factocrafty.util.registering;

import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

public enum FactocraftyEnergyStorages implements IFactocraftyLazyRegistry<FactocraftyEnergyStorageBlock> {
    BATTERY_BOX(FactoryCapacityTiers.BASIC),ADVANCED_FUNCTIONAL_STORAGE(FactoryCapacityTiers.ADVANCED),
    HIGH_FUNCTIONAL_STORAGE(FactoryCapacityTiers.HIGH),ULTIMATE_FUNCTIONAL_UNIT(FactoryCapacityTiers.ULTIMATE),
    QUANTUM_UNIT(FactoryCapacityTiers.QUANTUM);

    public final FactoryCapacityTiers capacityTier;
    FactocraftyEnergyStorages(FactoryCapacityTiers tier){
        capacityTier = tier;
    }
    @Override
    public FactocraftyEnergyStorageBlock get() {
        return (FactocraftyEnergyStorageBlock) Registration.getRegistrarBlockEntry(getName());
    }
}
