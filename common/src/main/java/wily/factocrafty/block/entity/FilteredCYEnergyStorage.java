package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

public abstract class FilteredCYEnergyStorage implements ICraftyEnergyStorage
{
    ICraftyEnergyStorage energyStorage;
    protected TransportState transportState;

    FilteredCYEnergyStorage(ICraftyEnergyStorage energyStorage, TransportState transportState){
        this.energyStorage = energyStorage;
        this.transportState = transportState;
    }

    @Override
    public Object getHandler() {
        return this;
    }

    @Override
    public EnergyTransaction consumeEnergy(EnergyTransaction transaction, boolean simulate) {
        if (transportState.canExtract()) return energyStorage.consumeEnergy(transaction,simulate);
        return EnergyTransaction.EMPTY;
    }

    @Override
    public EnergyTransaction receiveEnergy(EnergyTransaction transaction, boolean simulate) {
        if (transportState.canInsert()) return energyStorage.receiveEnergy(transaction,simulate);
        return EnergyTransaction.EMPTY;
    }

    @Override
    public void setEnergyStored(int energy) {
        if (transportState.isUsable())
            energyStorage.setEnergyStored(energy);
    }

    @Override
    public CompoundTag serializeTag() {
        return energyStorage.serializeTag();
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        energyStorage.deserializeTag(nbt);
    }

    @Override
    public FactoryCapacityTiers getSupportedTier() {
        return energyStorage.getSupportedTier();
    }

    @Override
    public FactoryCapacityTiers getStoredTier() {
        return energyStorage.getStoredTier();
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void setStoredTier(FactoryCapacityTiers tier) {
        energyStorage.setStoredTier(tier);
    }

    @Override
    public int getMaxConsume() {
        return energyStorage.getMaxConsume();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public static ICraftyEnergyStorage of(ICraftyEnergyStorage fluidHandler, TransportState transportState){
        return new FilteredCYEnergyStorage(fluidHandler,transportState) {};
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}