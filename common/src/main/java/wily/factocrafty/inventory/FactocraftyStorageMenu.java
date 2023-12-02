package wily.factocrafty.inventory;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.network.FactocraftyStorageSidesPacket;
import wily.factocrafty.network.FactocraftySyncEnergyPacket;
import wily.factocrafty.network.FactocraftySyncFluidPacket;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factoryapi.base.IFactoryExpandedStorage;
import wily.factoryapi.base.IFactoryProgressiveStorage;
import wily.factoryapi.base.Storages;

public class FactocraftyStorageMenu<T extends BlockEntity & IFactoryExpandedStorage> extends AbstractContainerMenu implements IFactoryContainerMenu<T> {

    public T be;

    public Player player;

    public int[] equipmentSlots;
    public int upgradeSlot;
    private final int inventoryY;


    public FactocraftyStorageMenu(MenuType<?> menuType, int containerId, BlockPos pos, Inventory inventory, int inventoryY) {
        super(menuType, containerId);
        this.player = inventory.player;
        Level level = player.level();
        this.inventoryY = inventoryY;
        this.be = (T) level.getBlockEntity(pos);
        if (be != null)
            be.getStorage(Storages.ITEM).ifPresent(i->checkContainerSize(i, i.getContainerSize()));

        for (Slot slot : be.getSlots(player))
            addSlot(slot);
        upgradeSlot = slots.size();
        if (be instanceof FactocraftyMenuBlockEntity pBe && pBe.hasUpgradeStorage())
            addSlot(new FactocraftyUpgradeSlot(pBe,0,9,9));
        addInventorySlots(inventory);
    }
    public FactocraftyStorageMenu(MenuType<?> menuType, int containerId, BlockPos pos, Inventory inventory) {
        this(menuType, containerId,pos,inventory,84);
    }

    public void addInventorySlots(Inventory inventory){
        int j;
        for(j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, inventoryY + j * 18));
            }
        }

        for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 58 + inventoryY));
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

        int machineSlots = be.getSlots(player).size();
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
        return be.getStorage(Storages.ITEM).get().stillValid(player);
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

    public void updateChanges() {

        if (player instanceof ServerPlayer sp) {
            if (!be.getTanks().isEmpty()) be.getTanks().forEach((tank) -> {
                FluidStack newFluid = tank.getFluidStack();
                Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncFluidPacket(be.getBlockPos(),tank.getMaxFluid(), be.getTanks().indexOf(tank), newFluid));
            });
            be.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncEnergyPacket(be.getBlockPos(), e)));
            be.getStorage(Storages.ENERGY).ifPresent((e)-> Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncEnergyPacket(be.getBlockPos(), e)));
            if (be instanceof IFactoryProgressiveStorage pStorage)
                pStorage.getProgresses().forEach((p) -> {Factocrafty.NETWORK.sendToPlayer(sp, new FactocraftySyncProgressPacket(be.getBlockPos(), pStorage.getProgresses().indexOf(p), p.getValues()));});

            be.getStorageSides(Storages.ITEM).ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStorageSidesPacket(be.getBlockPos(),0,d.ordinal(),s.getTransport(), s.getSlotIndex(be.getItemSlotsIdentifiers())))));
            be.getStorageSides(Storages.CRAFTY_ENERGY).ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStorageSidesPacket(be.getBlockPos(),1,d.ordinal(),s.getTransport(),0))));
            be.getStorageSides(Storages.FLUID).ifPresent((i) -> i.forEach((d,s) -> Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftyStorageSidesPacket(be.getBlockPos(),2,d.ordinal(),s.getTransport(),s.getSlotIndex(be.getFluidSlotsIdentifiers())))));

            if (be instanceof FactocraftyMenuBlockEntity be) be.syncAdditionalMenuData(this, sp);
        }
    }
    @Override
    public boolean moveItemStackTo(ItemStack itemStack, int i, int j, boolean inclusive) {
        return moveItemStackToSlot(itemStack,i,j,inclusive);
    }

    public T getBlockEntity() {
        return be;
    }
    public Player getPlayer() {
        return player;
    }
    @Override
    public NonNullList<Slot> getSlots() {
        return slots;
    }
}
