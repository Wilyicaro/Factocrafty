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

    public FactoryCapacityTiers supportedTier;

    public FactoryCapacityTiers storedTier = FactoryCapacityTiers.BASIC;

    BlockEntity be;
    public CYEnergyStorage(BlockEntity be, int capacity, FactoryCapacityTiers supportedTier){
        this(be,capacity,capacity, supportedTier);
    }
    public CYEnergyStorage(BlockEntity be, int capacity,int maxTransfer, FactoryCapacityTiers supportedTier){
        this(be,0,capacity,maxTransfer,maxTransfer, supportedTier);
    }
    public CYEnergyStorage(BlockEntity be, int energy, int capacity, int maxConsume, int maxReceive, FactoryCapacityTiers supportedTier){
        this.energy = energy;
        this.capacity = capacity;
        this.be = be;
        this.supportedTier = supportedTier;
        this.maxConsume = maxConsume;
        this.maxReceive = maxReceive;
    }
    public void setStoredTier(FactoryCapacityTiers tier) {
        storedTier = tier;
    }

    @Override
    public void setSupportedTier(FactoryCapacityTiers tier) {
        supportedTier = tier;
    }

    @Override
    public FactoryCapacityTiers getSupportedTier() {
        return supportedTier;
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

            if (supportedTier.supportTier(transaction.tier)) {
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
                return new CraftyTransaction((int) ((transaction.tier.getConductivity() - supportedTier.getConductivity()) * energyReceived), transaction.tier);
            }

            energy += energyReceived;
            setChanged();

        }

        return new CraftyTransaction(energyReceived, transaction.tier);
    }

    public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energyExtracted = Math.min(getMaxConsume(), transaction.convertEnergyTo(storedTier));

        if (!simulate) {
            energy -= energyExtracted;
            if (energy == 0) storedTier = FactoryCapacityTiers.BASIC;
            setChanged();
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
    public void setChanged() {
        be.setChanged();
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray(KEY, new int[]{this.energy, storedTier.ordinal(), supportedTier.ordinal(), capacity});
        tag.putInt(KEY, this.energy);
        tag.putInt("tier", storedTier.ordinal());
        tag.putInt("supportedTier", supportedTier.ordinal());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag compoundTag) {
        if (compoundTag.contains("tier") || compoundTag.contains("supportedTier")){
            setEnergyStored(compoundTag.getInt(KEY));
            setStoredTier(FactoryCapacityTiers.values()[compoundTag.getInt("tier")]);
            if (compoundTag.contains("supportedTier"))setSupportedTier(FactoryCapacityTiers.values()[compoundTag.getInt("supportedTier")]);
        } else {
            int[] values = compoundTag.getIntArray(KEY);
            setEnergyStored(values[0]);
            setStoredTier(FactoryCapacityTiers.values()[values[1]]);
            setSupportedTier(FactoryCapacityTiers.values()[values[2]]);
            capacity = values[3];
        }
    }
    public int getMaxConsume(){
        return Math.min(getEnergyStored(),getTransport().canExtract() ? Math.min(ICraftyEnergyStorage.super.getMaxConsume(),maxConsume) : 0);
    }
    @Override
    public int getMaxReceive() {
        return Math.min(getEnergySpace(),getTransport().canInsert() ? Math.min(ICraftyEnergyStorage.super.getMaxReceive(),maxReceive) : 0);
    }

    @Override
    public TransportState getTransport() {
        return storedTier.isBurned() ? TransportState.NONE : TransportState.EXTRACT_INSERT;
    }

    @Override
    public boolean isRemoved() {
        return be != null && be.isRemoved();
    }
}
