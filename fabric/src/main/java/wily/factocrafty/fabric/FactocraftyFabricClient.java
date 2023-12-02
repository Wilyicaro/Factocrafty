package wily.factocrafty.fabric;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.fabric.client.DynamicBucketModel;
import wily.factocrafty.fabric.client.FluidCellModel;

public class FactocraftyFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FactocraftyClient.init();
        FactocraftyClient.enqueueInit();
        FactocraftyClient.registerEntityRenderers(EntityRendererRegistry::register);
        FactocraftyClient.registerEntityModelLayers(EntityModelLayerRegistry::register);

        ModelLoadingPlugin.register((c) -> {
            FactocraftyClient.registerAdditionalModels(c::addModels);
            c.addModels(
                new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "fluid_cell_base"), "inventory"),
                new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "fluid_model"), "inventory"),
                new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "bucket_base"), "inventory"),
                new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "bucket_fluid_model"), "inventory"));
            c.modifyModelBeforeBake().register((model,context) -> {
                if (context.id().getNamespace().equals(Factocrafty.MOD_ID)) {
                    if (context.id().getPath().equals("fluid_cell")) {
                        return new FluidCellModel();
                    }else if (context.id().getPath().endsWith("_bucket")) {
                        return new DynamicBucketModel();
                    }
                }
                return model;
            });
        });
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((a,b,c,d)->{
            FactocraftyClient.registerEntityRenderLayers((type) -> b, d.getModelSet(), new FactocraftyClient.FactocraftyRenderLayerRegistry() {
                @Override
                public <T extends LivingEntity, M extends EntityModel<T>> void register(LivingEntityRenderer<T, M> renderer, RenderLayer<T, M> renderLayer) {
                    c.register(renderLayer);
                }
            });
        });

    }
}
