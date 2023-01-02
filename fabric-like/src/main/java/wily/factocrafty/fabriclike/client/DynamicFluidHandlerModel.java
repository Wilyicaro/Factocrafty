package wily.factocrafty.fabriclike.client;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.mojang.datafixers.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.hooks.fluid.FluidStackHooks;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.client.renderer.ModelHelper;
import wily.factoryapi.ItemContainerUtil;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

public abstract class DynamicFluidHandlerModel implements BakedModel, UnbakedModel,FabricBakedModel {

        public abstract ModelResourceLocation getBaseModel();

        public abstract ModelResourceLocation getFluidModel();

    public static Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();

        @Override
        public boolean isVanillaAdapter() {
            return false;
        }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {

    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        Fluid fluid = Fluids.EMPTY;
        if (ItemContainerUtil.isFluidContainer(stack)) {
            fluid = ItemContainerUtil.getFluid(stack).getFluid();

        }
        ModelManager bakedModelManager = Minecraft.getInstance().getModelManager();


        if (fluid != Fluids.EMPTY) {
            FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
            BakedModel fluidModel = bakedModelManager.getModel(getFluidModel());
            int fluidColor = fluidRenderHandler.getFluidColor(Minecraft.getInstance().level, Minecraft.getInstance().player.getOnPos(), fluid.defaultFluidState());
            TextureAtlasSprite fluidSprite = fluidRenderHandler.getFluidSprites(Minecraft.getInstance().level, BlockPos.ZERO, fluid.defaultFluidState())[0];
            int color = new Color((float) (fluidColor >> 16 & 255) / 255.0F, (float) (fluidColor >> 8 & 255) / 255.0F, (float) (fluidColor & 255) / 255.0F).getRGB();

            context.pushTransform(quad -> {
                quad.nominalFace(GeometryHelper.lightFace(quad));
                quad.spriteColor(0, color, color, color, color);
                // Some modded fluids doesn't have sprites. Fix for #2429
                quad.spriteBake(0, fluidSprite, MutableQuadView.BAKE_LOCK_UV);

                return true;
            });
            final QuadEmitter emitter = context.getEmitter();
            Fluid finalFluid = fluid;
            fluidModel.getQuads(null, null, randomSupplier.get()).forEach(q -> {
                emitter.fromVanilla(q.getVertices(), 0, false);
                if (FluidStackHooks.getLuminosity(finalFluid,null,null) > 0)emitter.material(RENDERER.materialFinder().emissive(0,true).find());
                emitter.emit();
            });
            context.popTransform();
        }
        context.fallbackConsumer().accept(bakedModelManager.getModel(getBaseModel()));
    }
    private final Material[] SPRITE_IDS = new Material[]{
            new Material(BLOCK_ATLAS, getBaseModel())};

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {

        return Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(new ResourceLocation(getBaseModel().getNamespace(),getBaseModel().getPath()));
    }

    @Override
    public ItemTransforms getTransforms() {
        return ModelHelper.DEFAULT_ITEM_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {

    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> function, ModelState modelState, ResourceLocation resourceLocation) {
        return this;
    }

}
