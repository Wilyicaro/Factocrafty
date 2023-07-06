package wily.factocrafty.tag;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class Fluids {
    public static final TagKey<Fluid> PETROLEUM = TagKey.create(Registries.FLUID,new ResourceLocation(MOD_ID,"petroleum"));
    public static final TagKey<Fluid> LATEX = TagKey.create(Registries.FLUID,new ResourceLocation(MOD_ID,"latex"));

    public static final TagKey<Fluid> GASOLINE = TagKey.create(Registries.FLUID,new ResourceLocation(MOD_ID,"gasoline"));

    public static final TagKey<Fluid> GAS = TagKey.create(Registries.FLUID,new ResourceLocation(MOD_ID,"gas"));
    
    
}
