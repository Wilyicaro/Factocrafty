package wily.factocrafty.util.registering;

import wily.factocrafty.block.machines.FactocraftyEnergyTransformerBlock;
import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

public enum FactocraftyEnergyTransformers implements IFactocraftyLazyRegistry<FactocraftyEnergyTransformerBlock> {
    ADVANCED_FUNCTIONAL_TRANSFORMER(FactoryCapacityTiers.ADVANCED),
    HIGH_FUNCTIONAL_TRANSFORMER(FactoryCapacityTiers.HIGH),ULTIMATE_FUNCTIONAL_TRANSFORMER(FactoryCapacityTiers.ULTIMATE),
    QUANTUM_TRANSFORMER(FactoryCapacityTiers.QUANTUM);

    public final FactoryCapacityTiers capacityTier;
    FactocraftyEnergyTransformers(FactoryCapacityTiers tier){
        capacityTier = tier;
    }
    @Override
    public FactocraftyEnergyTransformerBlock get() {
        return (FactocraftyEnergyTransformerBlock) Registration.getRegistrarBlockEntry(getName());
    }
}
