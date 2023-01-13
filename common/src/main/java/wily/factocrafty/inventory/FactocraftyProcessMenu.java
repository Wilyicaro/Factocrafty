package wily.factocrafty.inventory;

import com.google.common.base.Suppliers;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.IFactoryProcessableStorage;
import wily.factocrafty.network.FactocraftyStateButtonPacket;
import wily.factocrafty.network.FactocraftySyncEnergyPacket;
import wily.factocrafty.network.FactocraftySyncFluidPacket;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.Storages;

import java.util.Objects;
import java.util.function.Supplier;

public class FactocraftyProcessMenu<T extends BlockEntity> extends AbstractContainerMenu {
    public IFactoryStorage storage;

    public T be;

    public  Player player;



    public FactocraftyProcessMenu(FactocraftyMenus menu, int containerId, BlockPos pos, Player player) {
    this(FactocraftyMenus.getMachine(menu), containerId, pos, player);

    }

    protected FactocraftyProcessMenu(MenuType<?> menuType, int containerId, BlockPos pos, Player player) {
        super(menuType, containerId);
        Level level = player.getLevel();
        Inventory inventory = player.getInventory();

        this.player = player;
        this.storage = (IFactoryStorage) level.getBlockEntity(pos);
        this.be = (T) level.getBlockEntity(pos);
        assert storage != null;
        checkContainerSize(storage.getStorage(Storages.ITEM).get(), storage.getStorage(Storages.ITEM).get().getContainerSize());


        for (Slot slot : storage.getSlots(player)){
            addSlot(slot);
        }
        addInventorySlots(inventory);

    }

    public void addInventorySlots(  Inventory inventory){

        int j;
        for(j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142));
        }
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;

        int machineSlots = storage.getSlots(player).size();
        int inventorySlots =  machineSlots + 27;
        int totalSlots =  machineSlots + 36;

        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < machineSlots) {
                if (!this.moveItemStackTo(stack, machineSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else if (!this.moveItemStackTo(stack, 0, machineSlots, false)){
                if (index < inventorySlots) {
                    if (!this.moveItemStackTo(stack, inventorySlots, totalSlots, true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < totalSlots) {
                    if (!this.moveItemStackTo(stack, machineSlots, inventorySlots, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return storage.getStorage(Storages.ITEM).get().stillValid(player);
    }

    @Override
    public void broadcastChanges() {
        updateChanges();
        super.broadcastChanges();
    }

    @Override
    public void sendAllDataToRemote() {
        updateChanges();
        super.sendAllDataToRemote();
    }

    protected void updateChanges() {
        if (player instanceof ServerPlayer sp) {
            if (!storage.getTanks().isEmpty()) storage.getTanks().forEach((tank) -> {
                FluidStack newFluid = tank.getFluidStack();
                Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncFluidPacket(be.getBlockPos(), tank.identifier().differential(), newFluid));
            });
            storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncEnergyPacket(be.getBlockPos(), e.getEnergyStored(), e.getStoredTier())));
            if (storage instanceof IFactoryProcessableStorage progressableStorage){
                 progressableStorage.getProgresses().forEach((p) -> {
                    Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncProgressPacket(be.getBlockPos(), p.progressType.identifier.ordinal(), p.get(),p.maxProgress));
                });
                 progressableStorage.itemSides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),0,d.ordinal(),s.transportState, s.getSlotIndex(progressableStorage.getSlotsIdentifiers())))));
                progressableStorage.energySides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),1,d.ordinal(),s,0))));
                progressableStorage.fluidSides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),2,d.ordinal(),s.transportState,s.fluidHandler.identifier().differential()))));

            }
        }
        if (storage instanceof FactocraftyProcessBlockEntity be) be.syncAdditionalMenuData(this, player);
    }
@Override
protected boolean moveItemStackTo(ItemStack itemStack, int i, int j, boolean bl) {
    boolean bl2 = false;
    int k = i;
    if (bl) {
        k = j - 1;
    }

    Slot slot;
    ItemStack itemStack2;
    if (itemStack.isStackable()) {
        while(!itemStack.isEmpty()) {
            if (bl) {
                if (k < i) {
                    break;
                }
            } else if (k >= j) {
                break;
            }

            slot = this.slots.get(k);
            itemStack2 = slot.getItem();
            if (!itemStack2.isEmpty() && ItemStack.isSameItemSameTags(itemStack, itemStack2) && slot.mayPlace(itemStack)) {
                int l = itemStack2.getCount() + itemStack.getCount();
                if (l <= slot.getMaxStackSize(itemStack)) {
                    itemStack.setCount(0);
                    itemStack2.setCount(l);
                    slot.setChanged();
                    bl2 = true;
                } else if (itemStack2.getCount() < slot.getMaxStackSize(itemStack)) {
                    itemStack.shrink(itemStack.getMaxStackSize() - itemStack2.getCount());
                    itemStack2.setCount(itemStack.getMaxStackSize());
                    slot.setChanged();
                    bl2 = true;
                }
            }

            if (bl) {
                --k;
            } else {
                ++k;
            }
        }
    }

    if (!itemStack.isEmpty()) {
        if (bl) {
            k = j - 1;
        } else {
            k = i;
        }

        while(true) {
            if (bl) {
                if (k < i) {
                    break;
                }
            } else if (k >= j) {
                break;
            }

            slot = this.slots.get(k);
            itemStack2 = slot.getItem();
            if (itemStack2.isEmpty() && slot.mayPlace(itemStack)) {
                if (itemStack.getCount() > slot.getMaxStackSize()) {
                    slot.set(itemStack.split(slot.getMaxStackSize()));
                } else {
                    slot.set(itemStack.split(itemStack.getCount()));
                }

                slot.setChanged();
                bl2 = true;
                break;
            }

            if (bl) {
                --k;
            } else {
                ++k;
            }
        }
    }
    return bl2;
}



}
