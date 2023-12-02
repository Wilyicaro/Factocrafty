package wily.factocrafty.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wily.factoryapi.base.IFactoryExpandedStorage;

public interface IFactoryContainerMenu<T extends BlockEntity & IFactoryExpandedStorage> {
    T getBlockEntity();
    default BlockPos getBlockPos(){
        return getBlockEntity().getBlockPos();
    }
    default BlockState getBlockState(){
        return getBlockEntity().getBlockState();
    }

    Player getPlayer();

    NonNullList<Slot> getSlots();

    default void updateChanges(){

    }

    default ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;

        int machineSlots = getBlockEntity().getSlots(player).size();
        int inventorySlots =  machineSlots + 27;
        int totalSlots =  machineSlots + 36;

        Slot slot = this.getSlots().get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            if (index < machineSlots) {
                if (!this.moveItemStackToSlot(stack, machineSlots, totalSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else if (!this.moveItemStackToSlot(stack, 0, machineSlots, false)){
                if (index < inventorySlots) {
                    if (!this.moveItemStackToSlot(stack, inventorySlots, totalSlots, true)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < totalSlots) {
                    if (!this.moveItemStackToSlot(stack, machineSlots, inventorySlots, false)) {
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
    default boolean moveItemStackToSlot(ItemStack itemStack, int i, int j, boolean inclusive) {
        boolean bl2 = false;
        int k = i;
        if (inclusive) {
            k = j - 1;
        }

        Slot slot;
        ItemStack itemStack2;
        if (itemStack.isStackable()) {
            while(!itemStack.isEmpty()) {
                if (inclusive) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }

                slot = getSlots().get(k);
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

                if (inclusive) {
                    --k;
                } else {
                    ++k;
                }
            }
        }

        if (!itemStack.isEmpty()) {
            if (inclusive) {
                k = j - 1;
            } else {
                k = i;
            }

            while(true) {
                if (inclusive) {
                    if (k < i) {
                        break;
                    }
                } else if (k >= j) {
                    break;
                }

                slot = getSlots().get(k);
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

                if (inclusive) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        return bl2;
    }
}
