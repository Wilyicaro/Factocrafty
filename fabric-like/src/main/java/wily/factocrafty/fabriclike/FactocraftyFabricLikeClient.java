package wily.factocrafty.fabriclike;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.client.renderer.entity.FactocraftyBoatRenderer;
import wily.factocrafty.client.renderer.entity.HangGliderModel;
import wily.factocrafty.client.renderer.entity.JetpackModel;
import wily.factocrafty.entity.IFactocraftyBoat;
import wily.factocrafty.fabriclike.client.DynamicBucketModel;
import wily.factocrafty.fabriclike.client.FluidCellModel;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.JetpackItem;

import java.util.function.Supplier;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class FactocraftyFabricLikeClient {


    public static void init() {
        FactocraftyClient.init();
        FactocraftyClient.registerEntityRenderers(EntityRendererRegistry::register);
        FactocraftyClient.registerEntityModelLayers(EntityModelLayerRegistry::register);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((a,b,c,d)->{
            FactocraftyClient.registerEntityRenderLayers((type) -> b, d.getModelSet(), new FactocraftyClient.FactocraftyRenderLayerRegistry() {
                @Override
                public <T extends LivingEntity, M extends EntityModel<T>> void register(LivingEntityRenderer<T, M> renderer, RenderLayer<T, M> renderLayer) {
                    c.register(renderLayer);
                }
            });
        });
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> new JetpackModel<>(contextModel, Minecraft.getInstance().getEntityModels().bakeLayer(JetpackModel.LAYER_LOCATION)).renderToBuffer(matrices,vertexConsumers.getBuffer(RenderType.entityCutout(JetpackModel.getTexture())), light, OverlayTexture.NO_OVERLAY,1.0F,1.0F,1.0F, 1.0F),Registration.ELECTRIC_JETPACK.get());

        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "fluid_cell_base"), "inventory"));
            out.accept(new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "fluid_model"), "inventory"));
            out.accept(new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "bucket_base"), "inventory"));
            out.accept(new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "bucket_fluid_model"), "inventory"));
        });
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelIdentifier, modelProviderContext) -> {
            if (modelIdentifier.getNamespace().equals(Factocrafty.MOD_ID)) {
                if (modelIdentifier.getPath().equals("fluid_cell")) {
                    return new FluidCellModel();
                }else if (modelIdentifier.getPath().endsWith("_bucket")) {
                    return new DynamicBucketModel();
                }
            }
            return null;
        });

    }
}
