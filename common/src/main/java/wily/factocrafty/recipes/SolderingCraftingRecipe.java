package wily.factocrafty.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;

import java.util.ArrayList;
import java.util.List;

import static wily.factocrafty.util.JsonUtils.jsonElement;

public class SolderingCraftingRecipe extends ElectricCraftingRecipe {


    protected final Ingredient principalInput;
    protected final List<Ingredient> additionalInputs;
    public SolderingCraftingRecipe(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory, Ingredient principalInput, Ingredient... additionalInputs) {
        super(resourceLocation, craftingBookCategory);
        this.principalInput = principalInput;
        this.additionalInputs = List.of(additionalInputs);
        this.actualResult = getFactocraftyStack(principalInput);
    }



    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.SOLDERING_RECIPE_SERIALIZER.get();
    }

    @Override
    protected List<Ingredient> inputItems() {
        List<Ingredient> list = new ArrayList<>(additionalInputs);
        list.add(0,principalInput);
        list.add(Ingredient.of(Registration.SOLDERING_IRON.get()));
        return list;
    }

    public static ItemStack getFactocraftyStack(Ingredient i){
        for (int j = 0; j < i.getItems().length; j++) {
            ItemStack s = i.getItems()[j];
            if (s.getItem().arch$registryName().getNamespace().equals(Factocrafty.MOD_ID)) return s;
        }
        return i.getItems()[0];
    }
    @Override
    public @NotNull ItemStack getResultItem(ItemStack inputResult) {
        ItemStack result = inputResult.copy();
        CompoundTag tag = result.getOrCreateTag();
        ListTag components = tag.getList("Components",8);
        additionalInputs.forEach((i)-> components.add(StringTag.valueOf(getFactocraftyStack(i).getItem().arch$registryName().toString())));
        tag.put("Components", components);
        return result;
    }
    public static class SimpleSerializer<T extends SolderingCraftingRecipe> implements RecipeSerializer<T> {
        private final SimpleSerializer.Factory<T> constructor;

        public SimpleSerializer(SimpleSerializer.Factory<T> factory) {
            this.constructor = factory;
        }

        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            Ingredient principalItem = Ingredient.fromJson(jsonElement(jsonObject, "principalInput"));
            NonNullList<Ingredient> ings = NonNullList.create();
            JsonElement element = jsonElement(jsonObject,  "otherInputs");
            if (element instanceof JsonArray a) a.forEach(j-> {
                Ingredient ing = Ingredient.fromJson(j);
                if (!ing.isEmpty())ings.add(ing);
            }); else {
                Ingredient ing = Ingredient.fromJson(element);
                if (!ing.isEmpty())ings.add(ing);
            }
            return this.constructor.create(resourceLocation, CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", (String) null), CraftingBookCategory.MISC),principalItem, ings.toArray(new Ingredient[0]));
        }

        public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            Ingredient principalItem = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient[] ingredients = new Ingredient[0];
            Ingredient ing;
            int s = friendlyByteBuf.readVarInt();
            for (int i = 0; i < s; i++) {
                ing = Ingredient.fromNetwork(friendlyByteBuf);
                ingredients = ArrayUtils.addAll(ingredients, ing);
            }
            return this.constructor.create(resourceLocation, craftingBookCategory,principalItem, ingredients);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T craftingRecipe) {
            friendlyByteBuf.writeEnum(craftingRecipe.category());
            craftingRecipe.principalInput.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeVarInt(craftingRecipe.additionalInputs.size());
            craftingRecipe.additionalInputs.forEach(i-> i.toNetwork(friendlyByteBuf));

        }

        @FunctionalInterface
        public interface Factory<T extends CraftingRecipe> {
            T create(ResourceLocation resourceLocation, CraftingBookCategory craftingBookCategory, Ingredient principalInput, Ingredient... additionalInputs);
        }
    }
}
