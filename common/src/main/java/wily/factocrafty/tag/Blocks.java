package wily.factocrafty.tag;

import dev.architectury.platform.Platform;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class Blocks {
    public static final String commonName = Platform.isForge() ? "forge" : "c";

    public static final TagKey<Block> NUCLEAR_CASINGS = TagKey.create(Registries.BLOCK,new ResourceLocation(MOD_ID,"nuclear_casings"));
}
