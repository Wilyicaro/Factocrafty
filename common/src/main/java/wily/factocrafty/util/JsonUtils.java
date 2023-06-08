package wily.factocrafty.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class JsonUtils {

    public static ItemStack getJsonItem(JsonObject jsonObject, String object) {
        if (jsonObject != null) {
            String string2 = GsonHelper.getAsString(jsonObject, object, "minecraft:air");
            ResourceLocation resourceLocation2 = new ResourceLocation(string2);
            return new ItemStack(BuiltInRegistries.ITEM.getOptional(resourceLocation2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
        }
        return ItemStack.EMPTY;
    }
    public static JsonElement jsonElement(JsonObject jsonObject, String object){ return GsonHelper.isArrayNode(jsonObject, object) ? GsonHelper.getAsJsonArray(jsonObject, object,null) : GsonHelper.getAsJsonObject(jsonObject, object,null);}
}
