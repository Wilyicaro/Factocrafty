package wily.factocrafty.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.architectury.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.init.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wily.factocrafty.util.FluidStackUtil.getPlatformFluidAmount;

public class CompoundTagUtil {


    public static boolean compoundContains(CompoundTag comparator, CompoundTag contains){
        Map<String, Tag> map = new HashMap<>(comparator.tags);

        for (String key : contains.getAllKeys()) {
            if (map.containsKey(key)){
                if (map.get(key).getAsString().equals(contains.get(key).getAsString()) || (map.get(key) instanceof ListTag l1 && contains.get(key) instanceof ListTag l2 && l2.containsAll(l1))||(map.get(key) instanceof CompoundTag ct1 && contains.get(key) instanceof CompoundTag ct2 && compoundContains(ct1,ct2)) )map.remove(key);
            }
        }

        return map.isEmpty();
    }
    public static CompoundTag getFromJson(JsonObject obj){
        CompoundTag tag = new CompoundTag();
        JsonObject nbt = obj.getAsJsonObject("nbt");
        if (obj.has("nbt") && nbt!= null)tag = jsonObject(nbt);
        putJsonFluidTag(tag,obj);
        return tag;
    }
    public static CompoundTag putJsonFluidTag(CompoundTag tag, JsonObject obj){
        JsonObject fluidJson = obj.getAsJsonObject("fluidStack");
        if (obj.has("fluidStack") && fluidJson!= null){
            CompoundTag fluid = new CompoundTag();
            String name = GsonHelper.getAsString(fluidJson, "fluid", "minecraft:empty");
            if (Platform.isForge())fluid.putString( "FluidName", name);
            else {
                CompoundTag fluidVariant = new CompoundTag();
                fluidVariant.putString("fluid",name);
                fluid.put("fluidVariant",fluidVariant);
            }
            fluid.putLong(Platform.isForge() ? "Amount": "amount",  getPlatformFluidAmount(GsonHelper.getAsLong(fluidJson, "amount", 1000)));
            tag.put(Platform.isForge() ? "Fluid": "fluidStorage",fluid);
        }
        return tag;
    }
    public static CompoundTag jsonObject(JsonObject obj){
        CompoundTag tag = new CompoundTag();
        if (obj!= null)obj.asMap().forEach((s,e)->{
            if (e instanceof JsonPrimitive p) jsonPrimitiveNbt(tag,s,p);
            else if (e instanceof JsonArray a && !a.isEmpty()) {
                ListTag arr = new ListTag();
                a.forEach((t) -> {
                    if (t instanceof JsonPrimitive p)
                        jsonPrimitiveListTag(arr, p);
                });
                tag.put(s, arr);
            }
            else if (e instanceof JsonObject o) tag.put(s,jsonObject(o));
        });
        return tag;
    }
    public static void jsonPrimitiveNbt(CompoundTag tag, String s, JsonPrimitive p){
            if (p.isNumber()){
                Number n = p.getAsNumber();
                if (n instanceof Long l)tag.putLong(s, l);
                else if (n instanceof Float f)tag.putFloat(s, f);
                else if (n instanceof Short st)tag.putShort(s, st);
                else if (n instanceof Byte bt)tag.putByte(s, bt);
                else tag.putInt(s, n.intValue());
            }
            else if (p.isBoolean()) tag.putBoolean(s,p.getAsBoolean());
            else if (p.isString()) tag.putString(s,p.getAsString());
    }
    public static void jsonPrimitiveListTag(ListTag tag,JsonPrimitive p){
        if (p.isNumber()){
            Number n = p.getAsNumber();
            if (n instanceof Long l)tag.add(LongTag.valueOf(l));
            else if (n instanceof Float f)tag.add(FloatTag.valueOf(f));
            else if (n instanceof Short st)tag.add(ShortTag.valueOf(st));
            else if (n instanceof Byte bt)tag.add(ByteTag.valueOf(bt));
            else tag.add(IntTag.valueOf(n.intValue()));
        }
        else if (p.isBoolean()) tag.add(ByteTag.valueOf(p.getAsBoolean()));
        else if (p.isString()) tag.add(StringTag.valueOf(p.getAsString()));
    }

    public static List<Item> getItemComponents(ItemStack itemStack){
        List<Item> list = new ArrayList<>();
        if (itemStack.hasTag() && !itemStack.getTag().getList("Components",8).isEmpty())
            itemStack.getTag().getList("Components",8).forEach(t->list.add(Registration.ITEMS_REGISTRAR.get(new ResourceLocation(t.getAsString()))));
        return list;
    }

}
