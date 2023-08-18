package wily.factocrafty.tag;

import dev.architectury.platform.Platform;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class Items {
    public static final String commonName = Platform.isForge() ? "forge" : "c";

    public static final TagKey<Item> ORES = TagKey.create(Registries.ITEM,new ResourceLocation(commonName,"ores"));

    public static final TagKey<Item> PLATES = TagKey.create(Registries.ITEM,new ResourceLocation(commonName,"plates"));

    public static final TagKey<Item> STORAGE_BLOCKS = TagKey.create(Registries.ITEM,new ResourceLocation(commonName,"storage_blocks"));

    public static final TagKey<Item> NUGGETS = TagKey.create(Registries.ITEM,new ResourceLocation(commonName,"nuggets"));
}
