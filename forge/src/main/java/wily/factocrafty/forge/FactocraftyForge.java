package wily.factocrafty.forge;


import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.datagen.FactocraftyWorldGenBootstrap;

import java.util.function.Consumer;


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
