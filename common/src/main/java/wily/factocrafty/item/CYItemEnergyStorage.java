package wily.factocrafty.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.CraftyTransaction;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class CYItemEnergyStorage implements ICraftyEnergyStorage {
    private static final String KEY = "energy";



    private int capacity;
    private int maxInOut = 2000000;

    ItemStack container;
    TransportState transportState;
    final boolean isBlockItem;

    public final FactoryCapacityTiers supportableTier;

    public FactoryCapacityTiers storedTier = FactoryCapacityTiers.BASIC;
    public CYItemEnergyStorage(ItemStack stack, int energy, int capacity, TransportState transportState, FactoryCapacityTiers supportableTier, boolean isBlockItem){
        this.supportableTier = supportableTier;
        CompoundTag tag = stack.getOrCreateTag();
        if (isBlockItem) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        if (tag.getCompound("CYEnergy").isEmpty()){
            CompoundTag storage = new CompoundTag();
            storage.putInt(KEY,energy);
            storage.putInt("tier",storedTier.ordinal());
            tag.put("CYEnergy", storage);
            if (isBlockItem) stack.getOrCreateTag().put(BLOCK_ENTITY_TAG, tag);
        }
        this.isBlockItem = isBlockItem;
        this.capacity = capacity;
        this.container = stack;
        this.transportState = transportState;
    }
    public CYItemEnergyStorage(ItemStack stack, int initialEnergy, int capacity, TransportState transportState, FactoryCapacityTiers supportableTier){
        this(stack,initialEnergy,capacity, transportState,supportableTier,false);
    }
    public CYItemEnergyStorage(ItemStack stack, int initialEnergy, int capacity, int maxInOut, TransportState transportState, FactoryCapacityTiers supportableTier){
        this(stack, initialEnergy, capacity, transportState, supportableTier);
        this.maxInOut = maxInOut;
    }
    private CompoundTag getEnergyCompound(){
        CompoundTag tag = container.getOrCreateTag();
        if (isBlockItem) return tag.getCompound(BLOCK_ENTITY_TAG).getCompound("CYEnergy");
        return tag.getCompound("CYEnergy");
    }
    @Override
    public CraftyTransaction receiveEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energyReceived = Math.min(getSpace(), Math.min(this.maxInOut, transaction.energy));
        int energy = getEnergyStored();
        if (!simulate) {
            if ( supportableTier.supportTier(transaction.tier)) setStoredTier(transaction.tier);
            else {
                return CraftyTransaction.EMPTY;
            }
            energy += energyReceived;
            setEnergyStored(energy);

        }

        return new CraftyTransaction(energyReceived, transaction.tier);
    }

    public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
        if (transaction.isEmpty()) return  CraftyTransaction.EMPTY;
        int energy = getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(this.maxInOut, transaction.energy));

        if (!simulate) {
            if (!storedTier.supportTier(transaction.tier)) energyExtracted = storedTier.convertEnergyTo(energyExtracted,transaction.tier);
            energy -= energyExtracted;
            setEnergyStored(energy);
        }

        return new CraftyTransaction(energyExtracted, transaction.tier);
    }

    @Override
    public FactoryCapacityTiers getSupportedTier() {
        return supportableTier;
    }

    @Override
    public FactoryCapacityTiers getStoredTier() {
        return FactoryCapacityTiers.values()[getEnergyCompound().getInt("tier")];
    }

    @Override
    public int getEnergyStored() {
        return getEnergyCompound().getInt(KEY);
    }

    public void setEnergyStored(int energy) {
        CompoundTag tag = getEnergyCompound();
        tag.putInt(KEY,energy);
    }

    public void setStoredTier(FactoryCapacityTiers tier){
        CompoundTag tag = getEnergyCompound();
        tag.putInt("tier",tier.ordinal());
    }
    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }


    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY, getEnergyStored());
        tag.putInt("tier",storedTier.ordinal());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        this.container.setTag(nbt);
    }


    public int getMaxConsume(){
        return Math.min(getEnergyStored(),maxInOut);
    }

    @Override
    public ICraftyEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
