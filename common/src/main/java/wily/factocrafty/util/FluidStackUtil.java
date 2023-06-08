package wily.factocrafty.util;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public class FluidStackUtil {

    public static FluidStack fromJson(JsonObject jsonObject){
        if (jsonObject != null){
            String string2 = GsonHelper.getAsString(jsonObject, "fluid", "minecraft:empty");
            long amount = getPlatformFluidAmount(GsonHelper.getAsLong(jsonObject, "amount", 1000));
            return FluidStack.create(BuiltInRegistries.FLUID.get(new ResourceLocation(string2)),amount);
        }
        return FluidStack.empty();
    }

    public static long getPlatformFluidAmount(long amount){
        return (long) (((float)amount / 1000) * FluidStack.bucketAmount());
    }
}
