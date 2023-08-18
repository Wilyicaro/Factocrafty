package wily.factocrafty.item;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

public class ElectricCraftingToolItem extends EnergyItem{

    public ElectricCraftingToolItem(FactoryCapacityTiers tier, Properties properties) {
        super(tier, TransportState.INSERT, properties);
    }

    public boolean hasCraftingRemainingItem() {
        return true;
    }
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return hasCraftingRemainingItem();
    }
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack itemStack = stack.copy();
        ICraftyEnergyStorage storage = getEnergyStorage(itemStack);
        storage.consumeEnergy(15,false);
        return itemStack;
    }
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return hasCraftingRemainingItem() ? getCraftingRemainingItem(stack) : ItemStack.EMPTY;
    }
}
