package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import wily.factoryapi.base.*;

public abstract class FilteredCYEnergyStorage implements ICraftyEnergyStorage, IModifiableTransportHandler
{
    ICraftyEnergyStorage energyStorage;
    protected TransportState transportState;

    FilteredCYEnergyStorage(ICraftyEnergyStorage energyStorage, TransportState transportState){
        this.energyStorage = energyStorage;
        this.transportState = transportState;
    }

    @Override
    public boolean isRemoved() {
        return energyStorage.isRemoved();
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

    public static FilteredCYEnergyStorage of(ICraftyEnergyStorage energyStorage){
        return new FilteredCYEnergyStorage(energyStorage,TransportState.EXTRACT_INSERT){};
    }
    public void setTransport(TransportState transportState) {
        this.transportState = transportState;
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