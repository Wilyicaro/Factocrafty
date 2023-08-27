package wily.factocrafty.inventory;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.item.UpgradeType;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class UpgradeList extends AbstractList<ItemStack> {
    public List<ItemStack> list;

    public static UpgradeList create() {
        return new UpgradeList();
    }
    public UpgradeList(){
        this(Lists.newArrayList());
    }
    public UpgradeList(List<ItemStack> list){
        super();
        this.list = list;
    }
    @Override
    public int size() {
        return list.size();
    }

    public int getUpgradeIndex(UpgradeType type){
        return getUpgradeStack(type).isEmpty() ? indexOf(getUpgradeStack(type)) : -1;
    }
    public ItemStack getUpgradeStack(UpgradeType type){
        for (ItemStack i: this)
            if (i.getItem() instanceof FactocraftyUpgradeItem upg && upg.upgradeType == type) return i;
        return ItemStack.EMPTY;
    }
    public void checkEmptyValues(){
        removeIf(ItemStack::isEmpty);
    }

    @Override
    public boolean removeIf(Predicate<? super ItemStack> filter) {
        return list.removeIf(filter);
    }

    @Override
    public void add(int index, ItemStack stack) {
        if (!(stack.getItem() instanceof FactocraftyUpgradeItem i)) return;
        ItemStack storedUpgrade = getUpgradeStackByItem(i);
        if (storedUpgrade.isEmpty()) {
            ItemStack added = stack.copyAndClear();
            list.add(index,added);
            setChanged(index,false,added);
            return;
        }
        ItemStack added = stack.split(storedUpgrade.getMaxStackSize() - storedUpgrade.getCount());
        if (!added.isEmpty()) {
            storedUpgrade.setCount(storedUpgrade.getCount() + added.getCount());
            setChanged(indexOf(storedUpgrade),false,added);
        }
    }

    @Override
    public boolean add(ItemStack stack) {
        ItemStack copy = stack.copy();
        add(size(),stack);
        return !ItemStack.matches(copy,stack) || contains(stack);
    }

    @Override
    public ItemStack get(int i) {
        return list.get(i);
    }

    @Override
    public ItemStack set(int i, ItemStack stack) {
        if (stack.getItem() instanceof FactocraftyUpgradeItem upg)
            if (!upg.isSameType((FactocraftyUpgradeItem) get(i).getItem()))
                setChanged(i, true,stack);
        ItemStack set = list.set(i, stack);
        setChanged(i,false,set);
        return set;
    }

    @Override
    public ItemStack remove(int i) {
        ItemStack removedStack = list.remove(i);
        setChanged(i,true,removedStack);
        return removedStack;
    }

    public void setChanged(int index, boolean removed, ItemStack upgradeStack){

    }
    public ItemStack getUpgradeStackByItem(FactocraftyUpgradeItem item){
        for (ItemStack i: this) {
            if (i.getItem() == item) return i;
        }
        return ItemStack.EMPTY;
    }
    public double getUpgradeEfficiency(ItemStack stack) {
        return (double) stack.getCount() / stack.getMaxStackSize();
    }
    public double getUpgradeEfficiency(UpgradeType type){
        for (ItemStack i: this) {if (i.getItem() instanceof FactocraftyUpgradeItem upg && upg.upgradeType == type) return getUpgradeEfficiency(i);}
        return 0.0F;
    }

}
