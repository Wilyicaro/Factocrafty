package wily.factocrafty.datagen;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import wily.factocrafty.block.RubberLog;
import wily.factocrafty.gen.RubberTreeFoliagePlacer;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.FactocraftyOre;
import wily.factocrafty.util.registering.RegisterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

import static wily.factocrafty.init.Registration.*;

public class FactocraftyWorldGenBootstrap {

    public static ResourceKey<ConfiguredFeature<?,?>> RUBBER_TREE_CONFIGURED = RegisterUtil.createConfiguredFeature(getModResource("rubber_tree"));

    public static void configuredFeatures(BootstapContext<ConfiguredFeature<?, ?>> bootstapContext) {
        bootstapContext.register(RUBBER_TREE_CONFIGURED,new ConfiguredFeature<>(Feature.TREE,(
                new TreeConfiguration.TreeConfigurationBuilder(

                        new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder().add(RUBBER_LOG.get().defaultBlockState(), 5).add(RUBBER_LOG.get().defaultBlockState().setValue(RubberLog.LATEX_STATE,true), 2)),
                        new FancyTrunkPlacer(5, 3, 0),
                        SimpleStateProvider.simple(Registration.RUBBER_LEAVES.get().defaultBlockState()),
                        new RubberTreeFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 3),
                        new TwoLayersFeatureSize(3, 0, 2, OptionalInt.of(2))
                ).ignoreVines().build())));

        for (FactocraftyOre.Tier oreTiers : FactocraftyOre.Tier.values()) {
            oreTiers.getDerivative("ore").ifPresent((e)-> {
                if (e instanceof FactocraftyOre.Derivative.OreDerivative ore) {
                    String feature = "overworld_ore_" + oreTiers.getName();
                    bootstapContext.register(RegisterUtil.createConfiguredFeature(getModResource(feature)), new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(commonOreReplacement(oreTiers.getOre(false),oreTiers.getOre(true)).get(),ore.count)));
                }});
        }
    }

    private static Supplier<List<OreConfiguration.TargetBlockState>> commonOreReplacement(Block ore, Block deepSlateOre){

        return Suppliers.memoize(() ->{
            List<OreConfiguration.TargetBlockState> list = new ArrayList<>();
            if (!ore.equals(Blocks.AIR)) list.add(OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES), ore.defaultBlockState()));
            if (!deepSlateOre.equals(Blocks.AIR)) list.add(OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES), deepSlateOre.defaultBlockState()));
            return list;
        });
    }
    private static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, PlacementModifier placementModifier2) {
        return List.of(placementModifier, InSquarePlacement.spread(), placementModifier2, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacementModifier(int countPerChunk, int minY, int maxY, boolean uniform){
        VerticalAnchor v =  minY == -64 ? VerticalAnchor.bottom() : VerticalAnchor.absolute(minY);
        return orePlacement( CountPlacement.of(countPerChunk), uniform ? HeightRangePlacement.uniform(v, VerticalAnchor.absolute(maxY)) :  HeightRangePlacement.triangle(v, VerticalAnchor.absolute(maxY)));
    }

    public static ResourceKey<PlacedFeature> RUBBER_TREE = RegisterUtil.createPlacedFeature(getModResource("rubber_tree"));

    public static void placedFeatures(BootstapContext<PlacedFeature> bootstapContext) {
        HolderGetter<ConfiguredFeature<?,?>> getter = bootstapContext.lookup(Registries.CONFIGURED_FEATURE);
        bootstapContext.register( RUBBER_TREE,new PlacedFeature(getter.getOrThrow(RUBBER_TREE_CONFIGURED) , List.of(PlacementUtils.countExtra(0, 0.2F, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(0), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(RUBBER_TREE_SAPLING.get().defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome())));

        for (FactocraftyOre.Tier oreTiers : FactocraftyOre.Tier.values()) {
            oreTiers.getDerivative("ore").ifPresent((e)-> {
                if (e instanceof FactocraftyOre.Derivative.OreDerivative ore) {
                    String feature = "overworld_ore_" + oreTiers.getName();
                    bootstapContext.register(RegisterUtil.createPlacedFeature(getModResource(feature + "_middle")), new PlacedFeature(getter.getOrThrow(RegisterUtil.createConfiguredFeature(getModResource(feature))), commonOrePlacementModifier(ore.countPerChunk, ore.minY + 30, ore.maxY, false)));
                    bootstapContext.register(RegisterUtil.createPlacedFeature(getModResource(feature + "_small")), new PlacedFeature(getter.getOrThrow(RegisterUtil.createConfiguredFeature(getModResource(feature))), commonOrePlacementModifier(ore.countPerChunk, ore.minY, ore.maxY + 16, true)));
                    bootstapContext.register(RegisterUtil.createPlacedFeature(getModResource(feature + "_extra")), new PlacedFeature(getter.getOrThrow(RegisterUtil.createConfiguredFeature(getModResource(feature))), commonOrePlacementModifier(ore.extraCountPerChunk, ore.extraMinY, ore.extraMaxY, false)));
                }});
        }
    }
}
