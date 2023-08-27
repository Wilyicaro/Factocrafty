package wily.factocrafty.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.item.FactocraftyUpgradeItem;

public class FactocraftyUpgradeSlot extends FactocraftySlotWrapper{
    public FactocraftyUpgradeSlot(FactocraftyMenuBlockEntity pBe, int i, int x, int y) {
        super(new Slot(pBe.inventory,i,x,y){
            @Override
            public void set(ItemStack itemStack) {
                if (pBe.hasLevel() && pBe.getLevel().isClientSide) return;
                for (ItemStack i: pBe.storedUpgrades) {
                    if (ItemStack.isSameItemSameTags(itemStack,i)) {
                        if (pBe.selectedUpgrade.get() <0) i.grow(itemStack.getCount());
                        else  pBe.storedUpgrades.set(pBe.storedUpgrades.indexOf(i),itemStack);
                        return;
                    }
                }
                if (pBe.storedUpgrades.stream().noneMatch(i->ItemStack.isSameItem(i,itemStack))){
                    if (itemStack.isEmpty()) pBe.storedUpgrades.removeIf(i-> {
                        boolean b = pBe.storedUpgrades.indexOf(i) == pBe.selectedUpgrade.get();
                        if (b) pBe.selectedUpgrade.set(-1);
                        return b;
                    });
                    else pBe.storedUpgrades.add(itemStack);
                }
                this.setChanged();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                pBe.storedUpgrades.checkEmptyValues();
                pBe.storedUpgrades.setChanged(pBe.selectedUpgrade.get(),false,getItem());
            }

            @Override
            public ItemStack safeInsert(ItemStack itemStack, int i) {
                if (!pBe.getLevel().isClientSide && !itemStack.isEmpty() && itemStack.getItem() instanceof FactocraftyUpgradeItem upg && this.mayPlace(itemStack)) {
                    ItemStack itemStack2 = pBe.storedUpgrades.getUpgradeStack(upg.upgradeType).copy();
                    int j = Math.min(Math.min(i, itemStack.getCount()), this.getMaxStackSize(itemStack) - itemStack2.getCount());
                    if (itemStack2.isEmpty()) {
                        this.setByPlayer(itemStack.split(j));
                    } else if (ItemStack.isSameItemSameTags(itemStack2, itemStack)) {
                        itemStack.shrink(j);
                        itemStack2.grow(j);
                        this.setByPlayer(itemStack2);
                    }
                }
                return itemStack;
            }

            @Override
            public ItemStack getItem() {
                return (pBe.selectedUpgrade.get() < 0 || pBe.storedUpgrades.isEmpty()? ItemStack.EMPTY: pBe.storedUpgrades.get(Math.min(pBe.selectedUpgrade.get(),pBe.storedUpgrades.size() - 1)));
            }

            @Override
            public boolean mayPickup(Player player) {
                return pBe.selectedUpgrade.get() >=0;
            }

            @Override
            public ItemStack remove(int i) {
                if (pBe.getLevel().isClientSide) return ItemStack.EMPTY;
                ItemStack stack = pBe.storedUpgrades.get(pBe.selectedUpgrade.get());
                ItemStack itemStack = stack.getCount() <= i ? pBe.storedUpgrades.remove((int)pBe.selectedUpgrade.get()) : stack.split(i);
                if (!itemStack.isEmpty()) {
                    if (!pBe.storedUpgrades.contains(stack)) {
                        pBe.selectedUpgrade.set(-1);
                    }
                }
                this.setChanged();
                return itemStack;
            }

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof FactocraftyUpgradeItem upg && upg.isValid(pBe) && pBe.storedUpgrades.stream().noneMatch(i->ItemStack.isSameItem(i,itemStack)) || pBe.storedUpgrades.stream().anyMatch(i-> ItemStack.isSameItemSameTags(i,itemStack) && getMaxStackSize(i) - i.getCount() >= itemStack.getCount());
            }

            @Override
            public int getMaxStackSize() {
                return 256;
            }

        }, i, x, y);
    }
}
