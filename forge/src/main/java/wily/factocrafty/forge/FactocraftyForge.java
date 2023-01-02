package wily.factocrafty.forge;


import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import wily.factocrafty.Factocrafty;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.datagen.FactocraftyWorldGenBootstrap;


@Mod(Factocrafty.MOD_ID)
@Mod.EventBusSubscriber(modid = Factocrafty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FactocraftyForge {

    public FactocraftyForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        EventBuses.registerModEventBus(Factocrafty.MOD_ID, eventBus);
        Factocrafty.init();


    }

    @SubscribeEvent
    public static void addDataGen(GatherDataEvent event){
        RegistrySetBuilder registrySetBuilder=  new RegistrySetBuilder();
        registrySetBuilder.add(Registries.CONFIGURED_FEATURE, FactocraftyWorldGenBootstrap::configuredFeatures);
        registrySetBuilder.add(Registries.PLACED_FEATURE, FactocraftyWorldGenBootstrap::placedFeatures);
        event.getGenerator().addProvider(true, new DatapackBuiltinEntriesProvider(event.getGenerator().getPackOutput(),registrySetBuilder));
    }

}
