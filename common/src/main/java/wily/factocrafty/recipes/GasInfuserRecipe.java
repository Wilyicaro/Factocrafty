package wily.factocrafty.recipes;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import wily.factocrafty.util.FluidStackUtil;
import wily.factoryapi.base.IPlatformFluidHandler;

public class GasInfuserRecipe extends FactocraftyMachineRecipe{
    protected FluidStack otherFluid = FluidStack.empty();

    public GasInfuserRecipe(String name, ResourceLocation resourceLocation) {
        super(name, resourceLocation);
    }

    @Override
    public FluidStack getFluidIngredient() {
        return super.getFluidIngredient();
    }
    public boolean matchesOtherFluid(IPlatformFluidHandler tank, Level level) {
        return  matchesFluid(otherFluid,tank.getFluidStack());
    }
    public FluidStack getOtherFluid() {
        return otherFluid;
    }
    @Override
    public boolean hasFluidIngredient() {
        return true;
    }
    public boolean hasFluidResult() {
        return true;
    }

    @Override
    public boolean hasItemIngredient() {
        return false;
    }

    public static class Serializer extends FactocraftyMachineRecipe.Serializer<GasInfuserRecipe> {

        public Serializer(FactocraftySerializer<GasInfuserRecipe> cookieBaker, int defaultProcess) {
            super(cookieBaker, defaultProcess);
        }
        @Override
        public void otherResultsFromJson(JsonObject json, GasInfuserRecipe recipe) {
            super.otherResultsFromJson(json, recipe);
            recipe.otherFluid = FluidStackUtil.fromJson(json.getAsJsonObject("extra_fluid"));

        }
        public void otherResultsFromNetwork(FriendlyByteBuf buf, GasInfuserRecipe recipe){
            super.otherResultsFromNetwork(buf,recipe);
            recipe.otherFluid = FluidStack.read(buf);
        }


        public void otherResultsToNetwork(FriendlyByteBuf buf, GasInfuserRecipe recipe){
            super.otherResultsToNetwork(buf,recipe);
            recipe.otherFluid.write(buf);
        }


    }
}
