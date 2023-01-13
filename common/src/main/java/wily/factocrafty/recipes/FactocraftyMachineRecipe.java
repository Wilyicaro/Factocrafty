package wily.factocrafty.recipes;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.FluidStackUtil;

import java.util.Map;


public class FactocraftyMachineRecipe extends AbstractFactocraftyProcessRecipe {



    protected ItemStack other_result = ItemStack.EMPTY;
    protected ItemStack other_result1 = ItemStack.EMPTY;
    protected float other_result_chance;
    protected float other_result1_chance;

    protected FluidStack fluidResult;
    public boolean hasFluidResult(){
        return false;
    }
    public FluidStack getResultFluid(){
        return fluidResult;
    }

    public FactocraftyMachineRecipe(String name,ResourceLocation resourceLocation) {
        super(name, resourceLocation);
    }

    @Override
    protected void addOtherResults(Map<ItemStack, Float> ingredients) {
        ingredients.put(other_result, other_result_chance);
        ingredients.put(other_result1, other_result1_chance);
    }


    public static class Serializer extends AbstractFactocraftyProcessRecipe.Serializer<FactocraftyMachineRecipe> {


        public Serializer(FactocraftySerializer<FactocraftyMachineRecipe> cookieBaker, int i) {
            super(cookieBaker, i);
        }

        @Override
        public void otherResultFromJson(JsonObject json, FactocraftyMachineRecipe recipe) {
            JsonObject other_result = json.getAsJsonObject("other_result");
            JsonObject other_result1 = json.getAsJsonObject("other_result1");
            if (recipe.hasFluidResult()) {
                recipe.fluidResult = FluidStackUtil.fromJson(json.getAsJsonObject("fluid_result"));
            }
            recipe.other_result = getJsonItem(other_result, "result");
            recipe.other_result1 = getJsonItem(other_result1, "result");
            recipe.other_result_chance = getJsonFloat(other_result,"chance");
            recipe.other_result1_chance = getJsonFloat(other_result1,"chance");
        }
        private float getJsonFloat(JsonObject jsonObject, String name){
            return jsonObject == null ? 0.0F : GsonHelper.getAsFloat(jsonObject,name);
        }
        public void otherResultFromNetwork(FriendlyByteBuf buf, FactocraftyMachineRecipe recipe){
            if(recipe.hasFluidResult()) recipe.fluidResult = FluidStack.read(buf);
            recipe.other_result1 = buf.readItem();
            recipe.other_result = buf.readItem();
            recipe.other_result1_chance = buf.readFloat();
            recipe.other_result_chance = buf.readFloat();
        }


        public void otherResultToNetwork(FriendlyByteBuf buf, FactocraftyMachineRecipe recipe){
            if(recipe.hasFluidResult()) recipe.fluidResult.write(buf);
            buf.writeItem(recipe.other_result1);
            buf.writeItem(recipe.other_result);
            buf.writeFloat(recipe.other_result1_chance);
            buf.writeFloat(recipe.other_result_chance);
        }


    }
}
