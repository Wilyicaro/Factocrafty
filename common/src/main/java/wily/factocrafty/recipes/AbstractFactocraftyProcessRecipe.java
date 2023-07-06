package wily.factocrafty.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.FluidStackUtil;
import wily.factoryapi.base.IPlatformFluidHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static wily.factocrafty.util.JsonUtils.getJsonItem;
import static wily.factocrafty.util.JsonUtils.jsonElement;


public abstract class AbstractFactocraftyProcessRecipe implements Recipe<Container> {
    protected final String name;
    protected final ResourceLocation id;
    protected Ingredient ingredient = Ingredient.EMPTY;
    protected int ingredientCount = 0;
    protected FluidStack fluidIngredient = FluidStack.empty();
    protected ItemStack result;
    protected float experience;
    protected int maxProcess;
    protected int energyConsume;
    protected int diff;

    protected HashMap<ItemStack, Float> otherResults = new HashMap<>();

    public AbstractFactocraftyProcessRecipe(String name, ResourceLocation resourceLocation) {
        this.id = resourceLocation;
        this.name = name;
    }

    public int getDiff() {
        return diff;
    }

    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        for (int i = 0; i < ingredient.getItems().length; i++) {
            if (input.is(ingredient.getItems()[i].getItem()) && input.getCount() >= ingredientCount) return true;
        }
        return false;
    }

    public boolean matchesFluid(IPlatformFluidHandler tank, Level level) {
        return hasFluidIngredient()  && !fluidIngredient.isEmpty()&& tank.getFluidStack().isFluidEqual(fluidIngredient) && tank.getFluidStack().getAmount() >= fluidIngredient.getAmount();
    }
    public int getIngredientCount(){
        return ingredientCount;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.result.copy();
    }
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        addIngredients(nonNullList);
        return nonNullList;
    }
    public boolean hasItemIngredient(){
        return true;
    }
    public boolean hasFluidIngredient(){
        return false;
    }
    public FluidStack getFluidIngredient(){
        return fluidIngredient;
    }
    protected void addIngredients(NonNullList<Ingredient> ingredients) {
        ingredients.add(ingredient);
    }

    public float getExperience() {
        return this.experience;
    }

    public ItemStack getResultItem(RegistryAccess access) {
        return this.result;
    }

    public Map<ItemStack,Float> getOtherResults() {
        return ImmutableMap.copyOf(otherResults);
    }




    public int getMaxProcess() {
        return this.maxProcess;
    }

    public int getEnergyConsume() {
        return this.energyConsume;
    }


    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return Registration.RECIPE_TYPES.getRegistrar().get(Registration.getModResource(name));
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.RECIPE_SERIALIZER.getRegistrar().get(Registration.getModResource(name));
    }
    public static class Serializer<T extends AbstractFactocraftyProcessRecipe> implements RecipeSerializer<T>{

        private final int defaultProcess;
        private int defaultDiff = 0;
        private final FactocraftySerializer<T> factory;


        public Serializer(FactocraftySerializer<T> cookieBaker, int defaultProcess, int defaultDiff) {
            this.defaultProcess = defaultProcess;
            this.factory = cookieBaker;
            this.defaultDiff = defaultDiff;
        }
        public Serializer(FactocraftySerializer<T> cookieBaker, int defaultProcess) {
            this(cookieBaker, defaultProcess, 0);
        }


        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            T rcp = this.factory.create(resourceLocation);
            JsonElement ing = jsonElement(jsonObject,"ingredient");
            if (ing!= null && rcp.hasItemIngredient()) {
                rcp.ingredient = Ingredient.fromJson(jsonElement(jsonObject, "ingredient"));
                rcp.ingredientCount = GsonHelper.getAsInt(jsonObject.getAsJsonObject("ingredient"), "count", 1);
            }
            if (rcp.hasFluidIngredient() && rcp.ingredient.isEmpty()) rcp.fluidIngredient = FluidStackUtil.fromJson(jsonObject.getAsJsonObject("fluid_ingredient"));
            rcp.result = getJsonItem(jsonObject, "result");
            otherResultsFromJson(jsonObject, rcp);
            rcp.experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
            rcp.maxProcess = GsonHelper.getAsInt(jsonObject, "processtime", this.defaultProcess);
            rcp.energyConsume = GsonHelper.getAsInt(jsonObject, "energyconsume",3);
            rcp.diff = GsonHelper.getAsInt(jsonObject, "differential",defaultDiff);
            return rcp;
        }

        public void otherResultsFromJson(JsonObject json, T recipe){
            JsonElement element = jsonElement(json,  "otherResults");
            Consumer<JsonObject> c = obj-> recipe.otherResults.put(getJsonItem(obj,"result"), GsonHelper.convertToFloat(obj.get("chance"),"chance"));
            if (element instanceof JsonArray a) a.forEach(j-> {
                if (j instanceof JsonObject obj) c.accept(obj);
            }); else {
                if (element instanceof JsonObject obj)c.accept(obj);
            }
        }

        public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            T rcp = this.factory.create(resourceLocation);
            rcp.experience = buf.readFloat();
            rcp.maxProcess = buf.readVarInt();
            rcp.energyConsume = buf.readVarInt();
            if (rcp.hasItemIngredient()) {
                rcp.ingredientCount = buf.readVarInt();
                rcp.ingredient = Ingredient.fromNetwork(buf);
            }
            rcp.fluidIngredient = FluidStack.read(buf);
            rcp.result = buf.readItem();
            otherResultsFromNetwork(buf, rcp);
            return rcp;
        }
        public void otherResultsFromNetwork(FriendlyByteBuf friendlyByteBuf, T recipe){
            int size = friendlyByteBuf.readVarInt();
            for (int i = 0; i < size; i++) {
                recipe.otherResults.put(friendlyByteBuf.readItem(),friendlyByteBuf.readFloat());
            }
        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T recipe) {
            friendlyByteBuf.writeFloat(recipe.experience);
            friendlyByteBuf.writeVarInt(recipe.maxProcess);
            friendlyByteBuf.writeVarInt(recipe.energyConsume);
            if (recipe.hasItemIngredient()) {
                friendlyByteBuf.writeVarInt(recipe.ingredientCount);
                recipe.ingredient.toNetwork(friendlyByteBuf);
            }
            recipe.fluidIngredient.write(friendlyByteBuf);
            friendlyByteBuf.writeItem(recipe.result);
            otherResultsToNetwork(friendlyByteBuf, recipe);
        }
        public void otherResultsToNetwork(FriendlyByteBuf friendlyByteBuf, T recipe){
                friendlyByteBuf.writeVarInt(recipe.otherResults.size());
                recipe.otherResults.forEach((i,f)->{
                    friendlyByteBuf.writeItem(i);
                    friendlyByteBuf.writeFloat(f);
                });
        }

        public interface FactocraftySerializer<T extends AbstractFactocraftyProcessRecipe> {
            T create(ResourceLocation resourceLocation);
        }
    }
}
