package wily.factocrafty.util.registering;

import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.block.transport.energy.CableBlock;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.TransportState;

public enum FactocraftyCables implements IFactocraftyConduit<FactocraftyCables, CableBlock, CableBlockEntity> {
    TIN_CABLE(FactoryCapacityTiers.BASIC,0,Shape.COMMON),INSULATED_TIN_CABLE(FactoryCapacityTiers.BASIC,1.0F,Shape.INSULATED),
    COPPER_CABLE(FactoryCapacityTiers.ADVANCED,0,Shape.COMMON), INSULATED_COPPER_CABLE(FactoryCapacityTiers.ADVANCED,1.0F,Shape.INSULATED),
    GOLD_CABLE(FactoryCapacityTiers.HIGH,0,Shape.THIN),INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,0.6F,Shape.INSULATED_THIN),MEDIUM_INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,0.8F,Shape.COMMON),HIGH_INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,1.0F,Shape.INSULATED),
    SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,0,Shape.THIN), INSULATED_SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,0.6F,Shape.INSULATED_THIN),MEDIUM_INSULATED_SILVER_CABLE(FactoryCapacityTiers.HIGH,0.8F,Shape.COMMON), HIGH_INSULATED_SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,1.0F,Shape.INSULATED),
    CRYSTAL_CABLE(FactoryCapacityTiers.QUANTUM,1.0F,Shape.SOLID);

    public final FactoryCapacityTiers energyTier;
    public final float insulation;
    public final Shape cableShape;

    FactocraftyCables(FactoryCapacityTiers energyTier, float insulation, Shape cableShape){
        this.energyTier = energyTier;
        this.insulation = insulation;
        this.cableShape = cableShape;
    }

    @Override
    public Storages.Storage<?> getTransferenceStorage() {
        return Storages.CRAFTY_ENERGY;
    }

    @Override
    public ResourceLocation getSideModelLocation(TransportState state) {
        return new ResourceLocation("factocrafty:block/cable/tier/" + getName() + "_side" +(state == TransportState.EXTRACT_INSERT ? "" : "_" + state.toString()));
    }

    @Override
    public ResourceLocation getUpModelLocation() {
        return new ResourceLocation("factocrafty:block/cable/tier/" + getName() + "_up");
    }

    @Override
    public Shape getConduitShape() {
        return cableShape;
    }
    @Override
    public FactoryCapacityTiers getCapacityTier() {
        return energyTier;
    }

    public float transferenceEfficiency() {
        return (float) Math.min(Math.pow(energyTier.getConductivity(), 0.16F / (insulation + 1)), 0.99F);
    }
    public int maxEnergyTransfer() {
        return (int) (energyTier.initialCapacity * energyTier.getConductivity());
    }

}
