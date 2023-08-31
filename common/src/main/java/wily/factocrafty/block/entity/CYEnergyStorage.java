package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factoryapi.base.CraftyTransaction;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

public class CYEnergyStorage implements ICraftyEnergyStorage {

    private static final String KEY = "energy";
    private int energy;
    public int capacity;
    private final int maxConsume;
    private final int maxReceive;

    public FactoryCapacityTiers supportableTier;

    public FactoryCapacityTiers storedTier = FactoryCapacityTiers.BASIC;

    BlockEntity be;
    public CYEnergyStorage(BlockEntity be, int capacity, FactoryCapacityTiers supportableTier){
        this(be,capacity,capacity,supportableTier);
    }
    public CYEnergyStorage(BlockEntity be, int capacity,int maxTransfer, FactoryCapacityTiers supportableTier){
        this(be,0,capacity,maxTransfer,maxTransfer,supportableTier);
    }
    public CYEnergyStorage(BlockEntity be, int energy, int capacity, int maxConsume, int maxReceive, FactoryCapacityTiers supportableTier){
        this.energy = energy;
        this.capacity = capacity;
        this.be = be;
        this.supportableTier = supportableTier;
        this.maxConsume = maxConsume;
        this.maxReceive = maxConsume;
    }
    public void setStoredTier(FactoryCapacityTiers tier) {
        storedTier = tier;
    }

    @Override
    public void setSupportedTier(FactoryCapacityTiers tier) {
        supportableTier = tier;
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
    public CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return CraftyTransaction.EMPTY;
        int energyReceived = Math.min(getMaxReceive(), transaction.energy);

        if (!simulate && energyReceived != 0) {

            if (supportableTier.supportTier(transaction.tier)) {
                if (storedTier.supportTier(transaction.tier)){
                    energyReceived = transaction.tier.convertEnergyTo(energyReceived, storedTier);
                    transaction.tier = storedTier;
                } else {
                    setEnergyStored(storedTier.convertEnergyTo(getEnergyStored(), transaction.tier));
                    storedTier = transaction.tier;
                }
            }
            else {
                if (be.getLevel().random.nextFloat() >= 0.9 && energyReceived > 0) {
                    if (be.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock)
                        energyBlock.unsupportedTierBurn(be.getLevel(), be.getBlockPos(),transaction.tier);
                }
                return new CraftyTransaction((int) ((transaction.tier.getConductivity() - supportableTier.getConductivity()) * energyReceived), transaction.tier);
            }

            energy += energyReceived;
            this.be.setChanged();

        }

        return new CraftyTransaction(energyReceived, transaction.tier);
    }

    public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energyExtracted = Math.min(getMaxConsume(), transaction.convertEnergyTo(storedTier));

        if (!simulate) {
            energy -= energyExtracted;
            if (energy == 0) storedTier = FactoryCapacityTiers.BASIC;
            this.be.setChanged();
        }
        return new CraftyTransaction(Math.min(getMaxConsume(), transaction.energy), storedTier);
    }

    @Override
    public int getEnergyStored() {
        if (energy> getMaxEnergyStored()) energy = getMaxEnergyStored();
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
        tag.putInt("supportedTier", supportableTier.ordinal());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag compoundTag) {
        setEnergyStored(compoundTag.getInt(KEY));
        setStoredTier(FactoryCapacityTiers.values()[compoundTag.getInt("tier")]);
        setSupportedTier(FactoryCapacityTiers.values()[compoundTag.getInt("supportedTier")]);
    }
    public int getMaxConsume(){
        return Math.min(getEnergyStored(),getTransport().canExtract() ? Math.min(ICraftyEnergyStorage.super.getMaxConsume(),maxConsume) : 0);
    }
    @Override
    public int getMaxReceive() {
        return Math.min(getSpace(),getTransport().canInsert() ? Math.min(ICraftyEnergyStorage.super.getMaxReceive(),maxReceive) : 0);
    }

    @Override
    public ICraftyEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return storedTier.isBurned() ? TransportState.NONE : TransportState.EXTRACT_INSERT;
    }
}
