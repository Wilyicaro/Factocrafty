package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import wily.factoryapi.base.CraftyTransaction;
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
    public ICraftyEnergyStorage getHandler() {
        return this;
    }

    @Override
    public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transportState.canExtract()) return energyStorage.consumeEnergy(transaction,simulate);
        return CraftyTransaction.EMPTY;
    }

    @Override
    public CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transportState.canInsert()) return energyStorage.receiveEnergy(transaction,simulate);
        return CraftyTransaction.EMPTY;
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

    public static ICraftyEnergyStorage of(ICraftyEnergyStorage energyStorage, TransportState transportState){
        return new FilteredCYEnergyStorage(energyStorage,transportState){};
    }

    @Override
    public void setSupportedTier(FactoryCapacityTiers tier) {
        energyStorage.setSupportedTier(tier);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}