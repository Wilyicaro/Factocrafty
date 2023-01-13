package wily.factocrafty.util;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class FluidStackUtil {

    public static FluidStack fromJson(JsonObject jsonObject){
        if (jsonObject != null){
            String string2 = GsonHelper.getAsString(jsonObject, "fluid", "minecraft:empty");
            long amount = GsonHelper.getAsLong(jsonObject, "amount", FluidStack.bucketAmount());
            return FluidStack.create(BuiltInRegistries.FLUID.get(new ResourceLocation(string2)),amount);
        }
        return FluidStack.empty();
    }
}
