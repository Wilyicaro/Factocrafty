package wily.factocrafty.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;

public class CraftingToolItem extends Item {
    public CraftingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return hasCraftingRemainingItem();
    }
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack itemStack = stack.copy();
        if (itemStack.hurt(1, null, null) && itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
            itemStack.shrink(1);
        }
        return itemStack;
    }
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return hasCraftingRemainingItem() ? getCraftingRemainingItem(stack) : ItemStack.EMPTY;
    }
}
