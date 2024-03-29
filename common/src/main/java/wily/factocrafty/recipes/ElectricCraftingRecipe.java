package wily.factocrafty.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.item.ElectricCraftingToolItem;
import wily.factoryapi.base.Bearer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ElectricCraftingRecipe extends CustomRecipe {
    protected ItemStack actualResult = ItemStack.EMPTY;

    public ElectricCraftingRecipe(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory) {
        super(resourceLocation, craftingBookCategory);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return !assemble(container,RegistryAccess.EMPTY).isEmpty();
    }
    protected List<Ingredient> inputItems(){
        return List.of();
    }

    public Ingredient getPrincipalInput(){
        return inputItems().get(0);
    }
    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        return assemble(container);
    }

    public @NotNull ItemStack assemble(Container container) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack principalInput = ItemStack.EMPTY;

        List<Ingredient> remainInput = new ArrayList<>(inputItems());
        List<ItemStack> additionalInputs = new ArrayList<>();
        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            Bearer<Boolean> b = Bearer.of(true);
            Predicate<Ingredient> pred = ing -> {
                if (ing.test(itemStack2) && b.get()) {
                    b.set(false);
                    return true;
                }
                return false;
            };
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof ElectricCraftingToolItem s && s.getEnergyStorage(itemStack2).getEnergyStored() >= 15) {
                    if (!itemStack.isEmpty()) return ItemStack.EMPTY;
                    remainInput.removeIf((pred));
                    itemStack = itemStack2;
                }
                else {
                    if (!remainInput.removeIf((pred.and(ing->additionalInputs.add(itemStack2))))) {
                        return ItemStack.EMPTY;
                    } else if (getPrincipalInput().test(itemStack2)) principalInput = itemStack2.copyWithCount(1);
                }
            }
        }

        if (!itemStack.isEmpty() && remainInput.isEmpty()) return actualResult = getResultItem(additionalInputs.stream().filter(i->!getPrincipalInput().test(i)).toList(),principalInput);
        return ItemStack.EMPTY;
    }
    public @NotNull ItemStack getResultItem(List<ItemStack> additionalInputs, ItemStack inputResult) {
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
