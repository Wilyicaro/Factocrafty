package wily.factocrafty.fabriclike;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.client.renderer.entity.FactocraftyBoatRenderer;
import wily.factocrafty.entity.IFactocraftyBoat;
import wily.factocrafty.fabriclike.client.DynamicBucketModel;
import wily.factocrafty.fabriclike.client.FluidCellModel;
import wily.factocrafty.init.Registration;

import java.util.function.Supplier;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class FactocraftyFabricLikeClient {


    public static void init() {
        FactocraftyClient.init();
        FactocraftyClient.registerEntityRenderers(EntityRendererRegistry::register);
        FactocraftyClient.registerEntityModelLayers(EntityModelLayerRegistry::register);
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
