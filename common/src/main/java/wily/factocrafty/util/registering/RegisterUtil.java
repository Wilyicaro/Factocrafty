package wily.factocrafty.util.registering;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.HashMap;
import java.util.Map;

public class RegisterUtil {

    public static ResourceKey<ConfiguredFeature<?,?>> createConfiguredFeature(ResourceLocation resource){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, resource);
    }
    public static ResourceKey<PlacedFeature> createPlacedFeature(ResourceLocation resource){
        return ResourceKey.create(Registries.PLACED_FEATURE, resource);
    }


}
