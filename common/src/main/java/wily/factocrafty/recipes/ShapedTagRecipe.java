package wily.factocrafty.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.CompoundTagUtils;

import java.util.*;
import java.util.function.Consumer;

public class ShapedTagRecipe extends CustomRecipe {
    final int width;
    final int height;
    final NonNullList<Pair<Ingredient,CompoundTag>> recipeItems;
    final ItemStack result;
    final String group;
    final boolean showNotification;

    public ShapedTagRecipe(ResourceLocation resourceLocation, String string, CraftingBookCategory craftingBookCategory, int i, int j, NonNullList<Pair<Ingredient,CompoundTag>> list, ItemStack itemStack, boolean bl) {
        super(resourceLocation,craftingBookCategory);
        this.group = string;
        this.width = i;
        this.height = j;
        this.recipeItems = list;
        this.result = itemStack;
        this.showNotification = bl;
    }

    public ShapedTagRecipe(ResourceLocation resourceLocation, String string, CraftingBookCategory craftingBookCategory, int i, int j,NonNullList<Pair<Ingredient,CompoundTag>> list, ItemStack itemStack) {
        this(resourceLocation, string, craftingBookCategory, i, j, list, itemStack, true);
    }

    public RecipeSerializer<?> getSerializer() {
        return Registration.SHAPED_TAG_RECIPE_SERIALIZER.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result.copy();
    }

    public NonNullList<Ingredient> getIngredients() {
        return new NonNullList<>(this.recipeItems.stream().map((p)-> p.first).toList(),Ingredient.EMPTY);
    }
    public List<List<ItemStack>> getIngredientsStack(){
        List<List<ItemStack>> list = new ArrayList<>();
        recipeItems.forEach((i)->{
            List<ItemStack> s= List.of(i.first.getItems());
            if (!i.second.isEmpty()) s.forEach((stack)-> stack.setTag(i.second));
            list.add(s);
        });
        return list;
    }

    public boolean showNotification() {
        return this.showNotification;
    }

    public boolean canCraftInDimensions(int i, int j) {
        return i >= this.width && j >= this.height;
    }

    public boolean matches(CraftingContainer craftingContainer, Level level) {
        for(int i = 0; i <= craftingContainer.getWidth() - this.width; ++i) {
            for(int j = 0; j <= craftingContainer.getHeight() - this.height; ++j) {
                if (this.matches(craftingContainer, i, j, true)) {
                    return true;
                }

                if (this.matches(craftingContainer, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }
    private boolean matches(CraftingContainer craftingContainer, int i, int j, boolean bl) {

        for(int k = 0; k < craftingContainer.getWidth(); ++k) {
            for(int l = 0; l < craftingContainer.getHeight(); ++l) {
                int m = k - i;
                int n = l - j;
                int index = 0;
                if (m >= 0 && n >= 0 && m < this.width && n < this.height) {
                    if (bl) {
                        index = this.width - m - 1 + n * this.width;
                    } else {
                        index = m + n * this.width;
                    }
                }
                Ingredient ing = recipeItems.get(index).first;
                ItemStack stack = craftingContainer.getItem(k + l * craftingContainer.getWidth()).copy();
                if (!ing.test(stack) || !(recipeItems.get(index).second.isEmpty() || CompoundTagUtils.compoundContains(recipeItems.get(index).second, stack.getOrCreateTag()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    static NonNullList<Pair<Ingredient,CompoundTag>> dissolvePattern(String[] strings, Map<String, Pair<Ingredient,CompoundTag>> map, int i, int j) {
        NonNullList<Pair<Ingredient,CompoundTag>> nonNullList = NonNullList.withSize(i * j, Pair.of(Ingredient.EMPTY, new CompoundTag()));
        Set<String> set = Sets.newHashSet((Iterable)map.keySet());
        set.remove(" ");
        for(int k = 0; k < strings.length; ++k) {
            for(int l = 0; l < strings[k].length(); ++l) {
                String string = strings[k].substring(l, l + 1);
                Ingredient ing = map.get(string).first;
                CompoundTag tag = map.get(string).second;
                if (ing == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }
                set.remove(string);
                nonNullList.set(l + i * k, Pair.of(ing, tag));
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonNullList;
        }
    }

    @VisibleForTesting
    static String[] shrink(String... strings) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < strings.length; ++m) {
            String string = strings[m];
            i = Math.min(i, firstNonSpace(string));
            int n = lastNonSpace(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (strings.length == l) {
            return new String[0];
        } else {
            String[] strings2 = new String[strings.length - l - k];

            for(int o = 0; o < strings2.length; ++o) {
                strings2[o] = strings[o + k].substring(i, j + 1);
            }

            return strings2;
        }
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonNullList = this.getIngredients();
        return nonNullList.isEmpty() || nonNullList.stream().filter((ingredient) -> !ingredient.isEmpty()).anyMatch((ingredient) -> ingredient.getItems().length == 0);
    }

    private static int firstNonSpace(String string) {
        int i;
        for(i = 0; i < string.length() && string.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String string) {
        int i;
        for(i = string.length() - 1; i >= 0 && string.charAt(i) == ' '; --i) {
        }

        return i;
    }

    static String[] patternFromJson(JsonArray jsonArray) {
        String[] strings = new String[jsonArray.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = GsonHelper.convertToString(jsonArray.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    static Map<String, Pair<Ingredient,CompoundTag>> keyFromJson(JsonObject jsonObject) {
        Map<String, Pair<Ingredient,CompoundTag>> map = Maps.newHashMap();
        jsonObject.asMap().forEach((s,e)->{
            if (s.length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + s + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(s)) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(s, Pair.of(Ingredient.fromJson(e), CompoundTagUtils.getFromJson((JsonObject) e)));
        });

        map.put(" ", Pair.of(Ingredient.EMPTY,new CompoundTag()));
        return map;
    }

    public static ItemStack itemStackFromJson(JsonObject jsonObject) {
        Item item = itemFromJson(jsonObject);
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = GsonHelper.getAsInt(jsonObject, "count", 1);
            if (i < 1) {
                throw new JsonSyntaxException("Invalid output count: " + i);
            } else {
                return new ItemStack(item, i);
            }
        }
    }

    public static Item itemFromJson(JsonObject jsonObject) {
        String string = GsonHelper.getAsString(jsonObject, "item");
        Item item = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + string);
        } else {
            return item;
        }
    }

    public static class Serializer implements RecipeSerializer<ShapedTagRecipe> {
        public ShapedTagRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            String string = GsonHelper.getAsString(jsonObject, "group", "");
            CraftingBookCategory craftingBookCategory = CraftingBookCategory.CODEC.byName(GsonHelper.getAsString(jsonObject, "category", (String)null), CraftingBookCategory.MISC);
            Map<String, Pair<Ingredient,CompoundTag>> map = ShapedTagRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonObject, "key"));
            String[] strings = ShapedTagRecipe.shrink(ShapedTagRecipe.patternFromJson(GsonHelper.getAsJsonArray(jsonObject, "pattern")));
            int i = strings[0].length();
            int j = strings.length;
            NonNullList<Pair<Ingredient,CompoundTag>> nonNullList = ShapedTagRecipe.dissolvePattern(strings, map, i, j);
            ItemStack itemStack = ShapedTagRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "show_notification", true);
            return new ShapedTagRecipe(resourceLocation, string, craftingBookCategory, i, j, nonNullList, itemStack, bl);
        }

        public ShapedTagRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            int i = friendlyByteBuf.readVarInt();
            int j = friendlyByteBuf.readVarInt();
            String string = friendlyByteBuf.readUtf();
            CraftingBookCategory craftingBookCategory = friendlyByteBuf.readEnum(CraftingBookCategory.class);
            NonNullList<Pair<Ingredient,CompoundTag>> nonNullList = NonNullList.withSize(i * j, Pair.of(Ingredient.EMPTY,new CompoundTag()));

            for(int k = 0; k < nonNullList.size(); ++k) {
                nonNullList.set(k, Pair.of(Ingredient.fromNetwork(friendlyByteBuf), friendlyByteBuf.readNbt()));
            }

            ItemStack itemStack = friendlyByteBuf.readItem();
            boolean bl = friendlyByteBuf.readBoolean();
            return new ShapedTagRecipe(resourceLocation, string, craftingBookCategory, i, j, nonNullList, itemStack, bl);
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ShapedTagRecipe rcp) {
            friendlyByteBuf.writeVarInt(rcp.width);
            friendlyByteBuf.writeVarInt(rcp.height);
            friendlyByteBuf.writeUtf(rcp.group);
            friendlyByteBuf.writeEnum(rcp.category());
            rcp.recipeItems.forEach((p)->{
                p.first.toNetwork(friendlyByteBuf);
                friendlyByteBuf.writeNbt(p.second);
            });
            friendlyByteBuf.writeItem(rcp.result);
            friendlyByteBuf.writeBoolean(rcp.showNotification);
        }
    }
}
