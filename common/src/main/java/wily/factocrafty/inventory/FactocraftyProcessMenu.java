package wily.factocrafty.inventory;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.block.machines.entity.EnricherBlockEntity;
import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.network.FactocraftyStateButtonPacket;
import wily.factocrafty.network.FactocraftySyncEnergyPacket;
import wily.factocrafty.network.FactocraftySyncFluidPacket;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.IFactoryProcessableStorage;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.Storages;

public class FactocraftyProcessMenu<T extends BlockEntity> extends AbstractContainerMenu {
    public IFactoryStorage storage;

    public T be;

    public  Player player;

    public int[] equipmentSlots;
    public int upgradeSlot;

    public FactocraftyProcessMenu(FactocraftyMenus menu, int containerId, BlockPos pos, Player player) {
    this(FactocraftyMenus.getMachine(menu), containerId, pos, player);

    }

    protected FactocraftyProcessMenu(MenuType<?> menuType, int containerId, BlockPos pos, Player player) {
        super(menuType, containerId);
        Level level = player.level();
        Inventory inventory = player.getInventory();

        this.player = player;
        this.storage = (IFactoryStorage) level.getBlockEntity(pos);
        this.be = (T) level.getBlockEntity(pos);
        assert storage != null;
        checkContainerSize(storage.getStorage(Storages.ITEM).get(), storage.getStorage(Storages.ITEM).get().getContainerSize());


        for (Slot slot : storage.getSlots(player)){
            addSlot(slot);
        }
        upgradeSlot = slots.size();
        if (be instanceof FactocraftyProcessBlockEntity pBe)
            addSlot(new FactocraftyUpgradeSlot(pBe,0,9,9));
        addInventorySlots(inventory);
    }

    public void addInventorySlots(Inventory inventory){
        int addH = 0;
        if (be instanceof EnricherBlockEntity e) addH += 5;
        int j;
        for(j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + addH + j * 18));
            }
        }

        for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142 + addH));
        }
        if (be.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock b && (b.produceEnergy() || b instanceof FactocraftyEnergyStorageBlock))
            for(int i = 0; i < 5; ++i) {
                Slot s = player.inventoryMenu.getSlot(i == 0? 45 : 4+i);
                int finalI = i;
                this.addSlot(new FactocraftySlotWrapper(s, s.index,9, finalI == 0 ? 85 : 9 + 18* (finalI - 1)));
                equipmentSlots= ArrayUtils.add(equipmentSlots, slots.size() - 1);
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
                    Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncProgressPacket(be.getBlockPos(), progressableStorage.getProgresses().indexOf(p), p.getValues()));
                });
                 progressableStorage.itemSides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),0,d.ordinal(),s.transportState, s.getSlotIndex(progressableStorage.getSlotsIdentifiers())))));
                 progressableStorage.energySides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),1,d.ordinal(),s,0))));
                 progressableStorage.fluidSides().ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStateButtonPacket(be.getBlockPos(),2,d.ordinal(),s.transportState,s.fluidHandler.identifier().differential()))));

            }
            if (storage instanceof FactocraftyStorageBlockEntity be) be.syncAdditionalMenuData(this, sp);
        }
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
