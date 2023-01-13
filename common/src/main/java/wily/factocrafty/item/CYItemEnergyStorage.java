package wily.factocrafty.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.IStorageItem;
import wily.factoryapi.base.TransportState;

public class CYItemEnergyStorage implements ICraftyEnergyStorage, IStorageItem {
    private static final String KEY = "energy";

    private static final String BETAG = "BlockEntityTag";

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
        if (isBlockItem) tag = tag.getCompound(BETAG);
        if (tag.getCompound("CYEnergy").isEmpty()){
            CompoundTag storage = new CompoundTag();
            storage.putInt(KEY,energy);
            storage.putInt("tier",storedTier.ordinal());
            tag.put("CYEnergy", storage);
            if (isBlockItem) stack.getOrCreateTag().put(BETAG, tag);
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
        if (isBlockItem) return tag.getCompound(BETAG).getCompound("CYEnergy");
        return tag.getCompound("CYEnergy");
    }
    @Override
    public EnergyTransaction receiveEnergy(EnergyTransaction transaction, boolean simulate) {

        int energyReceived = Math.min(getSpace(), Math.min(this.maxInOut, transaction.energy));
        int energy = getEnergyStored();
        if (!simulate) {
            if ( transaction.tier.supportTier(supportableTier)) setStoredTier(transaction.tier);
            else {
                return EnergyTransaction.EMPTY;
            }
            energy += energyReceived;
            setEnergyStored(energy);

        }

        return new EnergyTransaction(energyReceived, transaction.tier);
    }

    public EnergyTransaction consumeEnergy(EnergyTransaction transaction, boolean simulate) {
        int energy = getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(this.maxInOut, transaction.energy));

        if (!simulate) {
            energy -= energyExtracted;
            setEnergyStored(energy);
        }

        return new EnergyTransaction(energyExtracted, transaction.tier);
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

    @Override
    public ItemStack getContainer() {
        return container;
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
        return transportState;
    }
}
