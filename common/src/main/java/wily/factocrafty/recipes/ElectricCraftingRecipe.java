package wily.factocrafty.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.item.ElectricCraftingToolItem;
import wily.factoryapi.base.ICraftyEnergyItem;

import java.util.ArrayList;
import java.util.List;

public class ElectricCraftingRecipe extends CustomRecipe {
    protected ItemStack actualResult = ItemStack.EMPTY;

    public ElectricCraftingRecipe(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory) {
        super(resourceLocation, craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack itemStack = ItemStack.EMPTY;

        List<Ingredient> remainInput = new ArrayList<>(inputItems());
        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof ElectricCraftingToolItem s && s.getCraftyEnergy(itemStack2).getEnergyStored() >= 15) {
                    if (!itemStack.isEmpty()) return false;
                    remainInput.removeIf((ing -> ing.test(itemStack2)));
                    itemStack = itemStack2;
                }
                 else {
                    if (!remainInput.removeIf((stack -> stack.test(itemStack2)))) {
                        return false;
                    }
                }
            }
        }

        return !itemStack.isEmpty() && remainInput.isEmpty();
    }
    protected List<Ingredient> inputItems(){
        return List.of();
    }

    public Ingredient getPrincipalInput(){
        return inputItems().get(0);
    }
    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack principalInput = ItemStack.EMPTY;

        List<Ingredient> remainInput = new ArrayList<>(inputItems());
        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof ElectricCraftingToolItem s && s.getCraftyEnergy(itemStack2).getEnergyStored() >= 15) {
                    if (!itemStack.isEmpty()) return ItemStack.EMPTY;
                    remainInput.removeIf((ing -> ing.test(itemStack2)));
                    itemStack = itemStack2;
                }
                else {
                    if (!remainInput.removeIf((stack -> stack.test(itemStack2)))) {
                        return ItemStack.EMPTY;
                    } else if (getPrincipalInput().test(itemStack2)) principalInput = itemStack2.copyWithCount(1);
                }
            }
        }

        if (!itemStack.isEmpty() && remainInput.isEmpty()) return actualResult = getResultItem(principalInput);
        return ItemStack.EMPTY;
    }
    public @NotNull ItemStack getResultItem(ItemStack inputResult) {
        return inputResult;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputItems().toArray(new Ingredient[0]));
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return actualResult;
    }


    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }
}
