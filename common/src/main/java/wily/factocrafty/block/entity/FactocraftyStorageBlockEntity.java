package wily.factocrafty.block.entity;

import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.network.FactocraftySyncSelectedUpgradePacket;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.*;

public class FactocraftyStorageBlockEntity extends BlockEntity implements IFactoryStorage {


    public final List<Bearer<Integer>> additionalSyncInt = new ArrayList<>();
    public FactocraftyStorageBlockEntity(BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);

    }


    protected int FILL_SLOT = 0;
    protected int DRAIN_SLOT = 0;



    public IPlatformFluidHandler<?> fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.INPUT, TransportState.INSERT);

    public IPlatformItemHandler inventory = FactoryAPIPlatform.getItemHandlerApi(getInvSize(), this);


    public Map<Direction, FluidSide> fluidSides = new HashMap<>(Map.of(Direction.NORTH, new FluidSide(fluidTank, TransportState.NONE),Direction.SOUTH,new FluidSide(fluidTank, TransportState.NONE), Direction.EAST,new FluidSide(fluidTank, TransportState.NONE),Direction.WEST,new FluidSide(fluidTank, TransportState.NONE),Direction.UP,new FluidSide(fluidTank, TransportState.NONE),Direction.DOWN,new FluidSide(fluidTank, TransportState.NONE)));

    public Map<Direction, ItemSide> itemSides =  new HashMap<>(Map.of(Direction.NORTH, new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE),Direction.SOUTH,new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE), Direction.EAST,new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE),Direction.WEST,new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE),Direction.UP,new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE),Direction.DOWN,new ItemSide(SlotsIdentifier.GENERIC, TransportState.NONE)));

    public Map<Direction, TransportState> energySides =  new HashMap<>(Map.of(Direction.NORTH, TransportState.EXTRACT_INSERT,Direction.SOUTH, TransportState.EXTRACT_INSERT, Direction.EAST, TransportState.EXTRACT_INSERT,Direction.WEST, TransportState.EXTRACT_INSERT,Direction.UP, TransportState.EXTRACT_INSERT,Direction.DOWN, TransportState.EXTRACT_INSERT));

    public CYEnergyStorage energyStorage;



    protected long getTankCapacity(){return (2 * FluidStackHooks.bucketAmount());}

    protected int getInvSize(){return getSlots(null).size();}

    public boolean hasInventory(){
        return false;
    }

    public boolean hasEnergyCell(){
        return false;
    }

    @Override
    public <T extends IPlatformHandlerApi> Optional<T> getStorage(Storages.Storage<T> storage) {
        return IFactoryStorage.super.getStorage(storage);
    }

    @Override
    public <T extends IPlatformHandlerApi> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        boolean b =  (direction == null);
        if (!b && getBlockedSides().contains(direction)) return Optional.empty();

        if (storage == Storages.CRAFTY_ENERGY && hasEnergyCell()) {
            if (!b) {
                return (Optional<T>) Optional.of(FilteredCYEnergyStorage.of(energyStorage, energySides.get(direction)));
            }else return (Optional<T>) Optional.of(energyStorage);//(FilteredCYEnergyStorage.readable(energyStorage))
        }
        if (storage == Storages.ITEM && hasInventory()) {
            if (!b)
                return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(inventory, direction, itemSlotsIdentifiers().get(itemSides.get(direction).identifier), itemSides.get(direction).transportState));
            else return (Optional<T>) Optional.of(inventory);//(FilteredItemHandler.readable(inventory,null,new int[]{}))
        }
        if (storage == Storages.FLUID && !getTanks().isEmpty()) {
            if (!b || fluidSides().isEmpty()) {
                for (IPlatformFluidHandler f : getTanks()) {
                    if(fluidSides.get(direction).identifier() == f.identifier())
                        return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(f, fluidSides.get(direction).transportState));
                }
            }
            else return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(fluidTank,TransportState.NONE));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Map<Direction, ItemSide>> itemSides() {
        return hasInventory() ? Optional.of(itemSides) : Optional.empty();
    }

    @Override
    public Optional<Map<Direction, TransportState>> energySides() {
        return hasEnergyCell() ? Optional.of(energySides) : Optional.empty();
    }

    @Override
    public Optional<Map<Direction, FluidSide>> fluidSides() {
        return getTanks().isEmpty() ?  Optional.empty(): Optional.of(fluidSides);
    }

    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {

    }



    public void addTanks(List<IPlatformFluidHandler> list) {

    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        saveTag(compoundTag);
        if (!additionalSyncInt.isEmpty()) compoundTag.putIntArray("additionalInt", additionalSyncInt.stream().map(Bearer::get).toList());
    }

    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player){
        for (Bearer<Integer> b : additionalSyncInt) Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncIntegerBearerPacket(getBlockPos(), b.get(),additionalSyncInt.indexOf(b)));
    }
    @Override
    public void load(CompoundTag compoundTag) {
        loadTag(compoundTag);
        int[] ar = compoundTag.getIntArray("additionalInt");
        for (int i = 0; i < ar.length; i++) additionalSyncInt.get(i).set(ar[i]);
    }


    public void tick() {
        getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)->{
            int[] chargeSlots = new int[]{FILL_SLOT, DRAIN_SLOT};
            for (int charge : chargeSlots) {
                if (getSlots(null).get(charge) instanceof FactocraftyCYItemSlot slot) {
                    ItemStack energyItem = inventory.getItem(charge);
                    if (energyItem.getItem() instanceof ICraftyEnergyItem<?> cy) {
                        ICraftyEnergyStorage storage = cy.getCraftyEnergy(energyItem);
                        if (storage.getTransport().canInsert() && slot.transportState.canInsert()) transferEnergyTo(null,storage);
                        if (storage.getTransport().canExtract() && slot.transportState.canExtract()) transferEnergyFrom(null,storage);
                    }
                }
            }
        });
        getTanks().forEach((tank)->{
            int[] fluidSlots = new int[]{FILL_SLOT, DRAIN_SLOT};
            for (int i : fluidSlots) {
                if (getSlots(null).get(i) instanceof FactocraftyFluidItemSlot slot) {
                    ItemStack stack = inventory.getItem(i);
                    if (ItemContainerUtil.isFluidContainer(stack)) {
                        ItemContainerUtil.ItemFluidContext context = null;
                        if (slot.transportState.canInsert() && !ItemContainerUtil.getFluid(stack).isEmpty()) context = ItemContainerUtil.drainItem(tank.fill(ItemContainerUtil.getFluid(stack),false), stack);
                        if (slot.transportState.canExtract()) tank.drain((context = ItemContainerUtil.fillItem(stack,tank.getFluidStack())).fluidStack(),false);
                        if (context != null) inventory.setItem(i,context.container());
                    }
                }
            }
        });
    }

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
