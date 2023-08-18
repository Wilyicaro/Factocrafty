package wily.factocrafty.inventory;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.item.UpgradeType;

import java.util.List;

public class UpgradeList extends NonNullList<ItemStack> {

    protected UpgradeList(List<ItemStack> list, @Nullable ItemStack object) {
        super(list, object);
    }
    public static UpgradeList create() {
        return new UpgradeList(Lists.newArrayList(), null);
    }
    @NotNull
    @Override
    public ItemStack get(int i) {
        ItemStack s = super.get(i);
        if (s.isEmpty()) remove(i);
        return s.isEmpty() ? isEmpty() ? ItemStack.EMPTY : super.get(Math.min(i,size()-1)):s;
    }
    public int getUpgradeIndex(UpgradeType type){
        return getUpgradeStack(type).isEmpty() ? indexOf(getUpgradeStack(type)) : -1;
    }
    public ItemStack getUpgradeStack(UpgradeType type){
        for (ItemStack i: this) {
            if (i.getItem() instanceof FactocraftyUpgradeItem upg && upg.upgradeType == type) return i;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean add(ItemStack stack) {
        if (!(stack.getItem() instanceof FactocraftyUpgradeItem i)) return false;
        ItemStack storedUpgrade = getUpgradeStackByItem(i);
        if (storedUpgrade.isEmpty()) {
            ItemStack added = stack.copy();
            stack.shrink(stack.getCount());
            return super.add(added);
        }
        int initialCount = stack.getCount();
        int max = storedUpgrade.getMaxStackSize() - storedUpgrade.getCount();
        storedUpgrade.setCount(storedUpgrade.getCount() + stack.split(max).getCount());
        return stack.getCount() < initialCount;
    }

    public ItemStack getUpgradeStackByItem(FactocraftyUpgradeItem item){
        for (ItemStack i: this) {
            if (i.getItem() == item) return i;
        }
        return ItemStack.EMPTY;
    }
    public double getUpgradeEfficiency(UpgradeType type){
        for (ItemStack i: this) {if (i.getItem() instanceof FactocraftyUpgradeItem upg && upg.upgradeType == type) return (double) i.getCount() / i.getMaxStackSize();}
        return 0.0F;
    }

}
