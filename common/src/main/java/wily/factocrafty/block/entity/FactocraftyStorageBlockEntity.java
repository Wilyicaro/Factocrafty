package wily.factocrafty.block.entity;

import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.inventory.UpgradeList;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.*;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public abstract class FactocraftyStorageBlockEntity extends BlockEntity implements IFactoryExpandedStorage,ITicker {


    public FactocraftyStorageBlockEntity(BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
    }

    public UpgradeList storedUpgrades = UpgradeList.create();
    public Bearer<Integer> selectedUpgrade = Bearer.of(-1);
    protected int[] STORAGE_SLOTS = new int[]{0};


    public IPlatformFluidHandler fluidTank = new FactoryFluidHandler(getTankCapacity(), this, f -> true, SlotsIdentifier.INPUT, TransportState.INSERT);

    public FactoryItemHandler inventory = getInitialInventory();


    public SideList<TransportSide> fluidSides = new SideList<>(()->new TransportSide(fluidTank.identifier(),TransportState.EXTRACT_INSERT));

    public SideList<TransportSide> itemSides =  new SideList<>(()->new TransportSide(SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT));

    public SideList<TransportSide> energySides = new SideList<>(()-> new TransportSide(SlotsIdentifier.ENERGY,TransportState.EXTRACT_INSERT));

    public CYEnergyStorage energyStorage;



    protected long getTankCapacity(){return (2 * FluidStackHooks.bucketAmount());}

    protected int getInvSize(){return getSlots(null).size();}

    public boolean hasInventory(){
        return false;
    }

    public boolean hasEnergyCell(){
        return false;
    }

    public boolean hasUpgradeStorage(){ return true;}

    protected FactoryItemHandler getInitialInventory(){
        return new FactoryItemHandler(getInvSize(), this, TransportState.EXTRACT_INSERT);
    }

    @Override
    public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        boolean b =  (direction == null);
        if (!b && getBlockedSides().contains(direction)) return ArbitrarySupplier.empty();

        if (storage == Storages.CRAFTY_ENERGY && hasEnergyCell()) {
            if (!b) {
                return ()-> (T) FactoryAPIPlatform.filteredOf(energyStorage,direction,energySides.get(direction).getTransport(), FilteredCYEnergyStorage::of);
            }else return ()-> (T)energyStorage;
        }
        if (storage == Storages.ITEM && hasInventory()) {
            if (!b)
                return ()-> (T)FactoryAPIPlatform.filteredOf(inventory, direction, itemSlotsIdentifiers().get(itemSides.get(direction).identifier()), itemSides.getTransport(direction));
            else return ()-> (T)inventory;
        }
        if (storage == Storages.FLUID && !getTanks().isEmpty()) {
            if (!b && getStorageSides(Storages.FLUID).isPresent()) {
                for (IPlatformFluidHandler f : getTanks()) {
                    if(fluidSides.get(direction).identifier() == f.identifier())
                        return ()-> (T) FactoryAPIPlatform.filteredOf(f,direction, fluidSides.getTransport(direction));
                }
            }
            else return ()-> (T) fluidTank;
        }

        return ArbitrarySupplier.empty();
    }

    @Override
    public ArbitrarySupplier<SideList<TransportSide>> getStorageSides(Storages.Storage<?> storage) {
        if (hasInventory() && storage == Storages.ITEM) return ()->itemSides;
        if (hasEnergyCell() && storage == Storages.CRAFTY_ENERGY || storage == Storages.ENERGY) return ()->energySides;
        if (!getTanks().isEmpty() && storage == Storages.FLUID) return ()->fluidSides;
        return IFactoryExpandedStorage.super.getStorageSides(storage);
    }


    @Override
    public List<IPlatformFluidHandler> getTanks() {
        List<IPlatformFluidHandler> tanks = IFactoryExpandedStorage.super.getTanks();
        addTanks(tanks);
        return tanks;
    }

    protected void addTanks(List<IPlatformFluidHandler> list) {

    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        saveTag(compoundTag);
        if (hasUpgradeStorage()) {
            ListTag upgradeItems = new ListTag();
            storedUpgrades.forEach((i) -> upgradeItems.add(i.save(new CompoundTag())));
            compoundTag.put("StoredUpgrades", upgradeItems);
        }
    }


    @Override
    public void load(CompoundTag compoundTag) {
        loadTag(compoundTag);
        if (hasUpgradeStorage()) {
            compoundTag.getList("StoredUpgrades", 10).forEach((t -> {
                if (t instanceof CompoundTag cT && storedUpgrades.stream().noneMatch(i -> ItemStack.isSameItem(i, ItemStack.of(cT))))
                    storedUpgrades.add(ItemStack.of(cT));
            }));
        }
    }

    public abstract void tick();

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

    public void handleUpdateTag(CompoundTag tag){
        if (tag != null)
            load(tag);

    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

}
