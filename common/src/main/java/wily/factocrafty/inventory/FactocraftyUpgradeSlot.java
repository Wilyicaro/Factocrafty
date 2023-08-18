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
                Factocrafty.LOGGER.info(pBe.getLevel().isClientSide + "?");
                for (ItemStack i: pBe.storedUpgrades) {
                    if (ItemStack.isSameItemSameTags(itemStack,i)) {
                        if (pBe.selectedUpgrade.get() <0) i.grow(itemStack.getCount());
                        else  pBe.storedUpgrades.set(pBe.storedUpgrades.indexOf(i),itemStack);
                        return;
                    }
                }
                if (pBe.storedUpgrades.stream().noneMatch(i->ItemStack.isSameItem(i,itemStack))){
                    if (itemStack.isEmpty()) pBe.storedUpgrades.removeIf(i-> {
                        //Factocrafty.LOGGER.info("HERE 2");
                        boolean b = pBe.storedUpgrades.indexOf(i) == pBe.selectedUpgrade.get();
                        if (b) pBe.selectedUpgrade.set(-1);
                        return b;
                    });
                    else pBe.storedUpgrades.add(itemStack);
                }
            }

            @Override
            public ItemStack safeInsert(ItemStack itemStack, int i) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof FactocraftyUpgradeItem upg && this.mayPlace(itemStack)) {
                    ItemStack itemStack2 = pBe.storedUpgrades.getUpgradeStack(upg.upgradeType).copy();
                    int j = Math.min(Math.min(i, itemStack.getCount()), this.getMaxStackSize(itemStack) - itemStack2.getCount());
                    //Factocrafty.LOGGER.info(itemStack2.getDisplayName().getString() +"-"+ itemStack2.getCount());
                    if (itemStack2.isEmpty()) {
                        this.setByPlayer(itemStack.split(j));
                    } else if (ItemStack.isSameItemSameTags(itemStack2, itemStack)) {
                        itemStack.shrink(j);
                        itemStack2.grow(j);
                        this.setByPlayer(itemStack2);
                    }
                    return itemStack;
                } else {
                    return itemStack;
                }
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
                ItemStack stack = pBe.storedUpgrades.get(pBe.selectedUpgrade.get());
                ItemStack itemStack = stack.getCount() <= i ? pBe.storedUpgrades.remove((int)pBe.selectedUpgrade.get()) : stack.split(i);
                if (!itemStack.isEmpty()) {
                    this.setChanged();
                    if (!pBe.storedUpgrades.contains(stack)) {
                        pBe.selectedUpgrade.set(-1);
                    }
                }
                //Factocrafty.LOGGER.info("HERE 1");
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
