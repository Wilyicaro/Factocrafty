package wily.factocrafty.forge;


import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.datagen.FactocraftyWorldGenBootstrap;

import java.util.Collections;


@Mod(Factocrafty.MOD_ID)
@Mod.EventBusSubscriber(modid = Factocrafty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FactocraftyForge {

    public FactocraftyForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Factocrafty.MOD_ID, eventBus);
        Factocrafty.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT,()->FactocraftyClient::init);

    }

    @SubscribeEvent
    public static void addDataGen(GatherDataEvent event){
        RegistrySetBuilder registrySetBuilder =  new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, FactocraftyWorldGenBootstrap::configuredFeatures)
                .add(Registries.PLACED_FEATURE, FactocraftyWorldGenBootstrap::placedFeatures);
        event.getGenerator().addProvider(true, new DatapackBuiltinEntriesProvider(event.getGenerator().getPackOutput(),event.getLookupProvider(),registrySetBuilder, Collections.singleton(Factocrafty.MOD_ID)));
    }

}
