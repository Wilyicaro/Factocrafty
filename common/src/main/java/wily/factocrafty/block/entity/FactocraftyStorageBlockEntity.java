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

public abstract class FactocraftyStorageBlockEntity extends BlockEntity implements IFactoryExpandedStorage {


    public final List<Bearer<Integer>> additionalSyncInt = new ArrayList<>();
    public FactocraftyStorageBlockEntity(BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        additionalSyncInt.add(selectedUpgrade);
    }

    public UpgradeList storedUpgrades = UpgradeList.create();
    public Bearer<Integer> selectedUpgrade = Bearer.of(-1);
    protected int[] STORAGE_SLOTS = new int[]{0};


    public IPlatformFluidHandler<?> fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.INPUT, TransportState.INSERT);

    public IPlatformItemHandler<?> inventory = FactoryAPIPlatform.getItemHandlerApi(getInvSize(), this);


    public SideList<FluidSide> fluidSides = SideList.createSideTypeList(()->new FluidSide(fluidTank,TransportState.EXTRACT_INSERT));

    public SideList<ItemSide> itemSides =  SideList.createSideTypeList(()->new ItemSide(SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT));

    public SideList<TransportState> energySides = new SideList<>(()->TransportState.EXTRACT_INSERT);

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
    @Override
    public <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage) {
        return IFactoryExpandedStorage.super.getStorage(storage);
    }

    @Override
    public <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        boolean b =  (direction == null);
        if (!b && getBlockedSides().contains(direction)) return Optional.empty();

        if (storage == Storages.CRAFTY_ENERGY && hasEnergyCell()) {
            if (!b) {
                return (Optional<T>) Optional.of(FilteredCYEnergyStorage.of(energyStorage, energySides.get(direction)));
            }else return (Optional<T>) Optional.of(energyStorage);//(FilteredCYEnergyStorage.readable(energyStorage))
        }
        if (storage == Storages.ITEM && hasInventory()) {
            if (!b)
                return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(inventory, direction, itemSlotsIdentifiers().get(itemSides.get(direction).identifier), itemSides.getTransport(direction)));
            else return (Optional<T>) Optional.of(inventory);//(FilteredItemHandler.readable(inventory,null,new int[]{}))
        }
        if (storage == Storages.FLUID && !getTanks().isEmpty()) {
            if (!b && fluidSides().isPresent()) {
                for (IPlatformFluidHandler<?> f : getTanks()) {
                    if(fluidSides.get(direction).identifier().differential() == f.identifier().differential())
                        return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(f, fluidSides.getTransport(direction)));
                }
            }
            else return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(fluidTank,TransportState.NONE));
        }

        return Optional.empty();
    }

    @Override
    public Optional<SideList<ItemSide>> itemSides() {
        return hasInventory() ? Optional.of(itemSides) : Optional.empty();
    }

    @Override
    public Optional<SideList<TransportState>> energySides() {
        return hasEnergyCell() ? Optional.of(energySides) : Optional.empty();
    }

    @Override
    public Optional<SideList<FluidSide>> fluidSides() {
        return getTanks().isEmpty() ?  Optional.empty(): Optional.of(fluidSides);
    }

    @Override
    public List<IPlatformFluidHandler<?>> getTanks() {
        List<IPlatformFluidHandler<?>> tanks = IFactoryExpandedStorage.super.getTanks();
        addTanks(tanks);
        return tanks;
    }

    protected void addTanks(List<IPlatformFluidHandler<?>> list) {

    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        saveTag(compoundTag);
        if (!additionalSyncInt.isEmpty()) compoundTag.putIntArray("additionalInt", additionalSyncInt.stream().map(Bearer::get).toList());
        if (hasUpgradeStorage()) {
            ListTag upgradeItems = new ListTag();
            storedUpgrades.forEach((i) -> upgradeItems.add(i.save(new CompoundTag())));
            compoundTag.put("StoredUpgrades", upgradeItems);
        }
    }


    @Override
    public void load(CompoundTag compoundTag) {
        loadTag(compoundTag);
        int[] ar = compoundTag.getIntArray("additionalInt");
        if (hasUpgradeStorage()) {
            for (int i = 0; i < ar.length; i++) additionalSyncInt.get(i).set(ar[i]);
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
