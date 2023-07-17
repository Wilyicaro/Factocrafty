package wily.factocrafty.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class JsonUtils {

    public static ItemStack getJsonItemStack(JsonObject jsonObject, String object) {
        if (jsonObject != null) {
            Item item;
            int count = 1;
            if (jsonObject.get(object) instanceof JsonObject result) {
                item = GsonHelper.getAsItem(result, "item");
                count = GsonHelper.getAsInt(result, "count",1);
            } else item = GsonHelper.getAsItem(jsonObject,object, Items.AIR);
            return new ItemStack(item, count);
        }
        return ItemStack.EMPTY;
    }
    public static JsonElement jsonElement(JsonObject jsonObject, String object){ return GsonHelper.isArrayNode(jsonObject, object) ? GsonHelper.getAsJsonArray(jsonObject, object,null) : GsonHelper.getAsJsonObject(jsonObject, object,null);}
}
