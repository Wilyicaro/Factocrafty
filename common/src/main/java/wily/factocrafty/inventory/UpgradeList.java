package wily.factocrafty.inventory;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.item.UpgradeType;

import java.util.AbstractList;
import java.util.Arrays;
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
    public double getUpgradeEfficiency(UpgradeType type){
        for (ItemStack i: this) {if (i.getItem() instanceof FactocraftyUpgradeItem upg && upg.upgradeType == type) return (double) i.getCount() / i.getMaxStackSize();}
        return 0.0F;
    }

}
