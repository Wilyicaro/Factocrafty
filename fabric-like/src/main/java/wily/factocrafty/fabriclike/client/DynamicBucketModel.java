package wily.factocrafty.fabriclike.client;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;

import java.util.function.Function;
import java.util.function.Supplier;

public class DynamicBucketModel extends DynamicFluidHandlerModel {


    @Override
    public ModelResourceLocation getBaseModel() {
        return new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID,"bucket_base"),"inventory");
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        ModelManager bakedModelManager = Minecraft.getInstance().getModelManager();
       context.fallbackConsumer().accept(bakedModelManager.getModel(getFluidModel()));
        super.emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public ModelResourceLocation getFluidModel() {
        return new ModelResourceLocation(new ResourceLocation(Factocrafty.MOD_ID, "bucket_fluid_model"),"inventory");
    }

}
