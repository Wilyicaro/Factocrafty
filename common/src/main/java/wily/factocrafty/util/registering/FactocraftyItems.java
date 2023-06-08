package wily.factocrafty.util.registering;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.init.Registration;

public enum FactocraftyItems {
    RUBBER,RUBBER_SIGN,
    ELECTRONIC_CIRCUIT,ADVANCED_CIRCUIT,
    CARBON_PLATE,CARBON_FIBERS,COMBINED_CARBON,
    TIN_INGOT,RAW_TIN,TIN_NUGGET,TIN_DUST,
    PLATINUM_INGOT,RAW_PLATINUM,PLATINUM_DUST,PLATINUM_NUGGET,PLATINUM_SWORD,PLATINUM_PICKAXE,PLATINUM_AXE,PLATINUM_SHOVEL,PLATINUM_HOE,PLATINUM_HELMET,PLATINUM_CHESTPLATE,PLATINUM_LEGGINGS,PLATINUM_BOOTS,
    BRONZE_INGOT,BRONZE_NUGGET_,BRONZE_DUST,BRONZE_SWORD,BRONZE_PICKAXE,BRONZE_AXE,BRONZE_SHOVEL,BRONZE_HOE,BRONZE_HELMET,BRONZE_CHESTPLATE,BRONZE_LEGGINGS,BRONZE_BOOTS,
    URANIUM,REFINED_URANIUM,URANIUM_NUGGET,URANIUM_DUST,
    LEAD_INGOT,RAW_LEAD,LEAD_DUST,LEAD_NUGGET,
    RUBY,RUBY_SWORD,RUBY_PICKAXE,RUBY_AXE,RUBY_SHOVEL,RUBY_HOE,RUBY_HELMET,RUBY_CHESTPLATE,RUBY_LEGGINGS,RUBY_BOOTS,RUBY_DUST;

    public Item get(){
        return Registration.getRegistrarItemEntry(getName());
    }
    public String getName(){
        return name().toLowerCase();
    }

}
