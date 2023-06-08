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

import static wily.factocrafty.util.JsonUtils.getJsonItem;


public class FactocraftyMachineRecipe extends AbstractFactocraftyProcessRecipe {



    protected FluidStack fluidResult = FluidStack.empty();
    public boolean hasFluidResult(){
        return false;
    }
    public FluidStack getResultFluid(){
        return fluidResult;
    }

    public FactocraftyMachineRecipe(String name,ResourceLocation resourceLocation) {
        super(name, resourceLocation);
    }


    public static class Serializer<T extends FactocraftyMachineRecipe> extends AbstractFactocraftyProcessRecipe.Serializer<T> {


        public Serializer(FactocraftySerializer<T> cookieBaker, int defaultProcess) {
            super(cookieBaker, defaultProcess);
        }
        public Serializer(FactocraftySerializer<T> cookieBaker, int defaultProcess, int defaultDiff) {
            super(cookieBaker, defaultProcess,defaultDiff);
        }

        @Override
        public void otherResultsFromJson(JsonObject json, T recipe) {
            super.otherResultsFromJson(json, recipe);
            if (recipe.hasFluidResult()) {
                recipe.fluidResult = FluidStackUtil.fromJson(json.getAsJsonObject("fluid_result"));
            }
        }
        public void otherResultsFromNetwork(FriendlyByteBuf buf, T recipe){
            super.otherResultsFromNetwork(buf,recipe);
            if(recipe.hasFluidResult()) recipe.fluidResult = FluidStack.read(buf);
        }


        public void otherResultsToNetwork(FriendlyByteBuf buf, T recipe){
            super.otherResultsToNetwork(buf,recipe);
            if(recipe.hasFluidResult()) recipe.fluidResult.write(buf);
        }


    }
}
