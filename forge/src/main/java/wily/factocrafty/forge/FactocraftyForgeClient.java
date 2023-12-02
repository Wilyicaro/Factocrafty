package wily.factocrafty.forge;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@Mod.EventBusSubscriber(modid = Factocrafty.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FactocraftyForgeClient {

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event){event.enqueueWork(FactocraftyClient::enqueueInit);}
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        FactocraftyClient.registerEntityRenderers(new FactocraftyClient.FactocraftyEntityRendererRegistry() {
            @Override
            public <T extends Entity> void register(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider) {
                event.registerEntityRenderer(type.get(),provider);
            }
        });
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        FactocraftyClient.registerEntityModelLayers(event::registerLayerDefinition);
    }
    @SubscribeEvent
    public static void registerRenderLayer(EntityRenderersEvent.AddLayers event) {
        FactocraftyClient.registerEntityRenderLayers(event::getRenderer, event.getEntityModels(), new FactocraftyClient.FactocraftyRenderLayerRegistry() {
                    @Override
                    public <T extends LivingEntity, M extends EntityModel<T>> void register(LivingEntityRenderer<T, M> renderer, RenderLayer<T, M> renderLayer) {
                        renderer.addLayer(renderLayer);
                    }
                }
        );
    }
    @SubscribeEvent
    public static void clientInit(ModelEvent.RegisterAdditional event){
            FactocraftyClient.registerAdditionalModels(event::register);
    }
}
