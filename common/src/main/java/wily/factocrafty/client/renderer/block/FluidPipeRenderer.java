package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.block.transport.ConduitSide;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.block.transport.fluid.FluidPipeBlockEntity;
import wily.factocrafty.client.renderer.DynamicBakedModel;
import wily.factocrafty.client.renderer.ModelHelper;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.DirectionUtil;

public class FluidPipeRenderer extends SolidConduitRenderer<FluidPipeBlockEntity>{
    public FluidPipeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    public static ResourceLocation fluidCenterLocation(boolean isLarge) {
        return new ResourceLocation("factocrafty:block/transport/fluid/" + (isLarge ? "large_" : "") +"fluid_center");
    }
    public static ResourceLocation fluidSideLocation(boolean isLarge, boolean isNone) {
       return new ResourceLocation("factocrafty:block/transport/fluid/" + (isLarge ? "large_" : "") +"fluid_side" + (isNone? "_none": ""));
    }

    @Override
    public void render(FluidPipeBlockEntity be, float f, PoseStack stack, MultiBufferSource multiBufferSource, int i, int j) {
        super.render(be, f, stack, multiBufferSource, i, j);
        FluidStack fluid = be.fluidHandler.getFluidStack();
        if (!fluid.isEmpty()) {
            boolean isLarge = be.getConduitType().ordinal() >=  3;
            float a = Math.max(0.1F, be.smoothFluidAmount / (float)be.fluidHandler.getMaxFluid());
            BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
            ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
            BlockState blockState = be.getBlockState();
            RenderType renderType = Sheets.translucentCullBlockSheet();
            int k = FluidStackHooks.getColor(be.getLevel(),be.getBlockPos(),fluid.getFluid().defaultFluidState());
            TextureAtlasSprite sprite = FluidStackHooks.getStillTexture(fluid);
            BakedModel fluidCenter = new DynamicBakedModel(modelManager.bakedRegistry.get(fluidCenterLocation(isLarge)),sprite, isLarge ?  ModelHelper.LARGE_FLUID_CENTER_MODEL: ModelHelper.FLUID_CENTER_MODEL);
            float r = ((k & 0xFF0000) >> 16) / 255F;
            float g = ((k & 0xFF00) >> 8) / 255F;
            float b = (k & 0xFF) / 255F;

                stack.pushPose();
                stack.translate(0.5, 0.5, 0.5);
                stack.scale(a,a,a);
                stack.translate(-0.5, -0.5, -0.5);
                dispatcher.getModelRenderer().renderModel(stack.last(), multiBufferSource.getBuffer(renderType), be.getBlockState(), fluidCenter, r, g, b, i, j);
                stack.popPose();

            for (Direction d : Direction.values()) {
                ConduitSide side = blockState.getValue(be.getBlock().PROPERTY_BY_DIRECTION.get(d));
                if (side.isConnected()) {
                    DynamicBakedModel fluidSide = new DynamicBakedModel(modelManager.bakedRegistry.get(fluidSideLocation(isLarge,!be.fluidSides.getTransport(d).isUsable())),sprite,be.fluidSides.getTransport(d).isUsable() ? isLarge ?  ModelHelper.LARGE_FLUID_SIDE_MODEL: ModelHelper.FLUID_SIDE_MODEL : isLarge ?  ModelHelper.LARGE_FLUID_SIDE_NONE_MODEL: ModelHelper.FLUID_SIDE_NONE_MODEL);
                    stack.pushPose();
                    stack.translate(0.5, 0.5, 0.5);
                    stack.mulPose(DirectionUtil.getRotation(d));
                    stack.translate(0,-((isLarge ? 0.78125 : 0.4375)*(1-a) / 2 ),0);
                    stack.scale(a,1 + (1-a) / 2 * (0.3125F * (isLarge ? 7/5F : 1) / (isLarge ? 0.28125F : 0.34375F)),a);
                    stack.translate(-0.5, -0.5, -0.5);
                    dispatcher.getModelRenderer().renderModel(stack.last(), multiBufferSource.getBuffer(renderType), be.getBlockState(), fluidSide, r, g, b, i, j);
                    stack.popPose();
                }
            }
        }
    }
}
