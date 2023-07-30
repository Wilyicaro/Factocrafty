package wily.factocrafty.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.CompoundTagUtil;

import java.util.*;
import java.util.function.Consumer;


import static wily.factocrafty.util.JsonUtils.jsonElement;

public class ShapelessTagRecipe extends CustomRecipe {
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final Map<Ingredient,CompoundTag> ingredients;

    public ShapelessTagRecipe(ResourceLocation resourceLocation, String group, CraftingBookCategory craftingBookCategory, ItemStack itemStack, Map<Ingredient,CompoundTag> nonNullList) {
        super(resourceLocation, craftingBookCategory);
        this.group = group;
        this.category = craftingBookCategory;
        this.result = itemStack;
        this.ingredients = nonNullList;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {

        List<Ingredient> remainInput = new ArrayList<>(ingredients.keySet());
        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty()) {{
                    if (!remainInput.removeIf((ing -> {
                        //Factocrafty.LOGGER.info( ing.test(itemStack2) + "-" +ingredients.get(ing).isEmpty());
                        return ing.test(itemStack2) &&(ingredients.get(ing).isEmpty() || CompoundTagUtil.compoundContains(ingredients.get(ing), itemStack2.getOrCreateTag()));
                    }))) {
                        return false;
                    }
                }
            }
        }

        return remainInput.isEmpty();
    }
    public List<List<ItemStack>> getIngredientsStack(){
        List<List<ItemStack>> list = new ArrayList<>();
        ingredients.forEach((i,n)->{
            List<ItemStack> s= List.of(i.getItems());
            if (!n.isEmpty()) s.forEach((stack)-> stack.setTag(n));
            list.add(s);
        });
        return list;
    }
    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return getResultItem(registryAccess).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    public boolean canCraftInDimensions(int i, int j) {
        return i * j >= this.ingredients.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.SHAPELESS_TAG_RECIPE_SERIALIZER.get();
    }
    public static class Serializer implements RecipeSerializer<ShapelessTagRecipe> {
        public ShapelessTagRecipe fromJson(ResourceLocation resourceLocation, JsonObject json) {
            String string = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory craftingBookCategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(json, "category", (String)null), CraftingBookCategory.MISC);
            JsonElement element = jsonElement(json,  "ingredients");
            Map<Ingredient, CompoundTag> map = new HashMap<>();
            Consumer<JsonObject> c = obj-> map.put(Ingredient.fromJson(obj), CompoundTagUtil.getFromJson(obj));
            if (element instanceof JsonArray a) a.forEach(j-> {
                if (j instanceof JsonObject obj) c.accept(obj);
            }); else {
                if (element instanceof JsonObject obj)c.accept(obj);
            }
            if (map.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (map.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                JsonObject obj = GsonHelper.getAsJsonObject(json, "result");
                ItemStack itemStack = ShapedRecipe.itemStackFromJson(obj);
                CompoundTag nbt =  CompoundTagUtil.getFromJson(obj);
                if (!nbt.isEmpty()) itemStack.setTag(nbt);
                return new ShapelessTagRecipe(resourceLocation, string, craftingBookCategory, itemStack, map);
            }
        }

        public ShapelessTagRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            String string = buf.readUtf();
            CraftingBookCategory craftingBookCategory = buf.readEnum(CraftingBookCategory.class);
            Map<Ingredient, CompoundTag> map = new HashMap<>();
            int i = buf.readVarInt();
            for(int j = 0; j < i; ++j) {
                map.put(Ingredient.fromNetwork(buf), buf.readNbt());
            }
            ItemStack itemStack = buf.readItem();
            return new ShapelessTagRecipe(resourceLocation, string, craftingBookCategory, itemStack, map);
        }

        public void toNetwork(FriendlyByteBuf buf, ShapelessTagRecipe shapelessRecipe) {
            buf.writeUtf(shapelessRecipe.group);
            buf.writeEnum(shapelessRecipe.category);
            buf.writeVarInt(shapelessRecipe.ingredients.size());
            shapelessRecipe.ingredients.forEach((i,c)->{
                i.toNetwork(buf);
                buf.writeNbt(c);
            });
            buf.writeItem(shapelessRecipe.result);
        }
    }
}
