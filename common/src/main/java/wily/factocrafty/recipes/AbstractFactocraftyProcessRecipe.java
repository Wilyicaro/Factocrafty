package wily.factocrafty.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.FluidStackUtil;

import java.util.HashMap;
import java.util.Map;


public abstract class AbstractFactocraftyProcessRecipe implements Recipe<Container> {
    protected final String name;
    protected final ResourceLocation id;
    protected Ingredient ingredient;
    protected int ingredientCount;
    protected FluidStack fluidIngredient;
    protected ItemStack result;
    protected float experience;
    protected int maxProcess;


    public AbstractFactocraftyProcessRecipe(String name, ResourceLocation resourceLocation) {
        this.id = resourceLocation;
        this.name = name;
    }

    public boolean matches(Container container, Level level) {
        ItemStack input = container.getItem(0);
        for (int i = 0; i < ingredient.getItems().length; i++) {
            if (input.is(ingredient.getItems()[i].getItem()) && input.getCount() >= ingredientCount) return true;
        }
        return false;
    }
    public int getIngredientCount(){
        return ingredientCount;
    }

    public ItemStack assemble(Container container) {
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
    public boolean hasFluidIngredient(){
        return false;
    }
    protected void addIngredients(NonNullList<Ingredient> ingredients) {
        ingredients.add(ingredient);
    }

    public float getExperience() {
        return this.experience;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public Map<ItemStack,Float> getOtherResults() {
        HashMap<ItemStack, Float> map = new HashMap<>();
        addOtherResults(map);
        return map;
    }
    protected void addOtherResults(Map<ItemStack,Float>  ingredients) {

    }


    public int getMaxProcess() {
        return this.maxProcess;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeType<?> getType() {
        return Registration.RECIPES.getRegistrar().get(Registration.getModResource(name));
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.RECIPE_SERIALIZER.getRegistrar().get(Registration.getModResource(name));
    }
    public static class Serializer<T extends AbstractFactocraftyProcessRecipe> implements RecipeSerializer<T>{

        private final int defaultProcess;
        private final FactocraftySerializer<T> factory;


        public Serializer(FactocraftySerializer<T> cookieBaker, int defaultProcess) {
            this.defaultProcess = defaultProcess;
            this.factory = cookieBaker;
        }
        protected  JsonElement jsonElement(JsonObject jsonObject, String object){ return GsonHelper.isArrayNode(jsonObject, object) ? GsonHelper.getAsJsonArray(jsonObject, object,null) : GsonHelper.getAsJsonObject(jsonObject, object,null);}

        public T fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            T rcp = this.factory.create(resourceLocation);
            rcp.ingredient = Ingredient.fromJson(jsonElement(jsonObject,"ingredient"));
            rcp.ingredientCount = GsonHelper.getAsInt(jsonObject.getAsJsonObject("ingredient"),"count",1);
            if (rcp.hasFluidIngredient()) rcp.fluidIngredient = FluidStackUtil.fromJson(jsonObject.getAsJsonObject("fluidIngredient"));
            rcp.result = getJsonItem(jsonObject, "result");
            otherResultFromJson(jsonObject, rcp);
            rcp.experience = GsonHelper.getAsFloat(jsonObject, "experience", 0.0F);
            rcp.maxProcess = GsonHelper.getAsInt(jsonObject, "processtime", this.defaultProcess);
            return rcp;
        }
        protected ItemStack getJsonItem(JsonObject jsonObject, String object) {
            if (jsonObject != null) {
                String string2 = GsonHelper.getAsString(jsonObject, object);
                ResourceLocation resourceLocation2 = new ResourceLocation(string2);
                return new ItemStack(BuiltInRegistries.ITEM.getOptional(resourceLocation2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
            }
            return ItemStack.EMPTY;
        }
        public void otherResultFromJson(JsonObject json, T recipe){

        }

        public T fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            T rcp = this.factory.create(resourceLocation);
            rcp.experience = buf.readFloat();
            rcp.maxProcess = buf.readVarInt();
            if (rcp.hasFluidIngredient()) rcp.fluidIngredient = FluidStack.read(buf);
            rcp.ingredientCount = buf.readVarInt();
            rcp.ingredient = Ingredient.fromNetwork(buf);
            rcp.result = buf.readItem();
            otherResultFromNetwork(buf, rcp);
            return rcp;
        }
        public void otherResultFromNetwork(FriendlyByteBuf friendlyByteBuf, T recipe){

        }

        public void toNetwork(FriendlyByteBuf friendlyByteBuf, T recipe) {
            friendlyByteBuf.writeFloat(recipe.experience);
            friendlyByteBuf.writeVarInt(recipe.maxProcess);
            if(recipe.hasFluidIngredient()) recipe.fluidIngredient.write(friendlyByteBuf);
            friendlyByteBuf.writeVarInt(recipe.ingredientCount);
            recipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItem(recipe.result);
            otherResultToNetwork(friendlyByteBuf, recipe);
        }
        public void otherResultToNetwork(FriendlyByteBuf friendlyByteBuf, T recipe){

        }

        public interface FactocraftySerializer<T extends AbstractFactocraftyProcessRecipe> {
            T create(ResourceLocation resourceLocation);
        }
    }
}
