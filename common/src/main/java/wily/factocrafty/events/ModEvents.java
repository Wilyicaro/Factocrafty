package wily.factocrafty.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.hooks.item.tool.AxeItemHooks;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.GenerationStep;
import wily.factocrafty.datagen.FactocraftyWorldGenBootstrap;
import wily.factocrafty.entity.CorruptedEnderMan;
import wily.factocrafty.init.FlammableRegistry;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.ElectricArmorItem;
import wily.factocrafty.item.FactocraftyOre;
import wily.factocrafty.util.registering.RegisterUtil;

import java.util.function.Predicate;

import static wily.factocrafty.init.Registration.RUBBER_LOG;
import static wily.factocrafty.init.Registration.getModResource;

public class ModEvents {
    public static void init(){
        LifecycleEvent.SETUP.register(() -> {
            AxeItemHooks.addStrippable(RUBBER_LOG.get(), Registration.STRIPPED_RUBBER_LOG.get()); FlammableRegistry.bootStrap(); FuelRegistry.register(300, Registration.RUBBER_FENCE.get(), Registration.RUBBER_FENCE_GATE.get());
        });
        biomeLoadingEvent();
        EntityAttributeRegistry.register(Registration.CORRUPTED_ENDERMAN, CorruptedEnderMan::createAttributes);
        EntityEvent.LIVING_HURT.register((l, s, f)-> {
                    if (!s.is(DamageTypeTags.BYPASSES_ARMOR)) {
                        for (ItemStack i: l.getArmorSlots()) {
                            if (i.getItem() instanceof ElectricArmorItem item) item.getCraftyEnergy(i).consumeEnergy((int) Math.max( f * 140,1), false);
                        }
                        return EventResult.pass();
                    }
                    return EventResult.interruptTrue();
                }
        );
    }


    private static void biomeLoadingEvent(){
        Predicate<BiomeModifications.BiomeContext> RUBBER_BIOMES = (ctx) -> ctx.hasTag(BiomeTags.IS_TAIGA) || ctx.hasTag(BiomeTags.HAS_SWAMP_HUT);
        BiomeModifications.addProperties(RUBBER_BIOMES, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, FactocraftyWorldGenBootstrap.RUBBER_TREE)));

        Predicate<BiomeModifications.BiomeContext> PETROLEUM_LAKE_BIOMES = (ctx) -> ctx.hasTag(BiomeTags.IS_BEACH) || ctx.hasTag(BiomeTags.IS_OCEAN) || ctx.hasTag(BiomeTags.IS_RIVER)|| ctx.hasTag(BiomeTags.IS_SAVANNA) | ctx.hasTag(BiomeTags.HAS_DESERT_PYRAMID);
        BiomeModifications.addProperties(PETROLEUM_LAKE_BIOMES, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.LAKES, FactocraftyWorldGenBootstrap.PETROLEUM_LAKE)));
        for (FactocraftyOre.Material oreTiers : FactocraftyOre.Material.values()) {
            oreTiers.getDerivative("ore").ifPresent((e)-> {
                if (e instanceof FactocraftyOre.OreDerivative) {
                    String feature = "overworld_ore_" + oreTiers.getName();
                    BiomeModifications.addProperties((ctx) -> true, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RegisterUtil.createPlacedFeature(getModResource(feature + "_small")))));
                    BiomeModifications.addProperties((ctx) -> true, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RegisterUtil.createPlacedFeature(getModResource(feature + "_middle")))));
                    BiomeModifications.addProperties((ctx) -> true, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, RegisterUtil.createPlacedFeature(getModResource(feature + "_extra")))));
                }});}
    }

}
