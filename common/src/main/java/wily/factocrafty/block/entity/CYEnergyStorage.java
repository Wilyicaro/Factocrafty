package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

public class CYEnergyStorage implements ICraftyEnergyStorage {

    private static final String KEY = "energy";
    private int energy;
    private final int capacity;
    private int maxInOut = 1000000;

    public final FactoryCapacityTiers supportableTier;

    public FactoryCapacityTiers storedTier = FactoryCapacityTiers.BASIC;

    BlockEntity be;
    public CYEnergyStorage(BlockEntity be, int energy, int capacity, FactoryCapacityTiers supportableTier){
        this.energy = energy;
        this.capacity = capacity;
        this.be = be;
        this.supportableTier = supportableTier;
    }
    public CYEnergyStorage(BlockEntity be, int energy, int capacity, int maxInOut, FactoryCapacityTiers supportableTier){
        this(be, energy, capacity,supportableTier);
        this.maxInOut = maxInOut;
    }
    public void setStoredTier(FactoryCapacityTiers tier) {
        storedTier = tier;
    }
    @Override
    public FactoryCapacityTiers getSupportedTier() {
        return supportableTier;
    }

    @Override
    public FactoryCapacityTiers getStoredTier() {
        return storedTier;
    }
    @Override
    public EnergyTransaction receiveEnergy(EnergyTransaction transaction, boolean simulate) {
        int energyReceived = Math.min(getSpace(), Math.min(this.maxInOut, transaction.energy));
        if (!getTransport().canInsert()) return  EnergyTransaction.EMPTY;

        if (!simulate && energyReceived != 0) {

            if ( transaction.tier.supportTier(supportableTier)) {
                if (transaction.tier.supportTier(storedTier))transaction.tier = storedTier;
                else storedTier = transaction.tier;
                // transaction.tier.convertEnergyTo(energyReceived, supportableTier);
            }
            else {
                if (be.getLevel().random.nextFloat() >= 0.9 && energyReceived > 0) {
                    if (be.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock)
                        energyBlock.unsupportedTierBurn(be.getLevel(), be.getBlockPos());
                }
                return new EnergyTransaction((int) ((transaction.tier.getConductivity() - supportableTier.getConductivity()) * energyReceived), transaction.tier);
            }

            energy += energyReceived;
            this.be.setChanged();

        }

        return new EnergyTransaction(energyReceived, transaction.tier);
    }

    public EnergyTransaction consumeEnergy(EnergyTransaction transaction, boolean simulate) {
        int energyExtracted = Math.min(energy, Math.min(this.maxInOut, transaction.energy));
        if (!getTransport().canExtract()) return  EnergyTransaction.EMPTY;

        if (!simulate) {
            energy -= energyExtracted;
            this.be.setChanged();
        }

        return new EnergyTransaction(energyExtracted, storedTier);
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }


    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY, this.energy);
        tag.putInt("tier", storedTier.ordinal());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        this.energy = nbt.getInt(KEY);
        this.storedTier = FactoryCapacityTiers.values()[nbt.getInt("tier")];
    }
    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxInOut);
    }

    @Override
    public Object getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return storedTier.isBurned() ? TransportState.NONE : TransportState.EXTRACT_INSERT;
    }
}
