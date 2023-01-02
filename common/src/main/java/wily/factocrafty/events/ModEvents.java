package wily.factocrafty.events;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.hooks.item.tool.AxeItemHooks;
import dev.architectury.registry.fuel.FuelRegistry;
import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import wily.factocrafty.datagen.FactocraftyWorldGenBootstrap;
import wily.factocrafty.entity.CorruptedEnderMan;
import wily.factocrafty.init.FlammableRegistry;
import wily.factocrafty.init.Registration;

import java.util.function.Predicate;

import static wily.factocrafty.init.Registration.RUBBER_LOG;

public class ModEvents {
    public static void init(){
        LifecycleEvent.SETUP.register(() -> {
            AxeItemHooks.addStrippable(RUBBER_LOG.get(), Registration.STRIPPED_RUBBER_LOG.get()); FlammableRegistry.bootStrap(); FuelRegistry.register(300, Registration.RUBBER_FENCE.get(), Registration.RUBBER_FENCE_GATE.get());
        });
        biomeLoadingEvent();
        EntityAttributeRegistry.register(Registration.CORRUPTED_ENDERMAN, CorruptedEnderMan::createAttributes);


    }


    private static void biomeLoadingEvent(){
        Predicate<BiomeModifications.BiomeContext> RUBBER_BIOMES = (ctx) -> ctx.hasTag(BiomeTags.IS_TAIGA) || ctx.hasTag(BiomeTags.HAS_SWAMP_HUT);
         BiomeModifications.addProperties(RUBBER_BIOMES, ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, FactocraftyWorldGenBootstrap.RUBBER_TREE)));
        //Registration.FEATURES.forEach((f)-> BiomeModifications.addProperties((ctx)-> Registration.FEATURES.getRegistrar().getId(f.get()).getPath().contains("ore"), ((biomeContext, mutable) -> mutable.getGenerationProperties().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Holder.direct(f.get())))));

    }

}
