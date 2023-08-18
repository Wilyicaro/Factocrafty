package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.renderer.DynamicBakedModel;
import wily.factocrafty.client.renderer.ModelHelper;
import wily.factoryapi.util.DirectionUtil;

public class FactocraftyMachineRenderer<T extends FactocraftyMenuBlockEntity> implements BlockEntityRenderer<T> {

    BlockEntityRendererProvider.Context context;

    public FactocraftyMachineRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(FactocraftyMenuBlockEntity be, float f, PoseStack stack, MultiBufferSource bufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        stack.pushPose();
        stack.translate(0.5,0.5,0.5);
        be.getBlockState().getOptionalValue(BlockStateProperties.FACING).ifPresent(d-> stack.mulPose(DirectionUtil.getNorthRotation(d)));
        be.getBlockState().getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).ifPresent(d-> stack.mulPose(DirectionUtil.getHorizontalRotation(d)));
        stack.translate(-0.5,-0.5,-0.5);
        dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.cutoutBlockSheet()),be.getBlockState(), dispatcher.getBlockModel(be.getBlockState()),1,1,1,i,j);

        if(be.getTanks().contains(be.fluidTank) && !be.fluidTank.getFluidStack().isEmpty()){
            FluidStack fluid = be.fluidTank.getFluidStack();
            int k = be.getLevel() == null ? FluidStackHooks.getColor(fluid) : FluidStackHooks.getColor(be.getLevel(),be.getBlockPos(),fluid.getFluid().defaultFluidState());
            float r = ((k & 0xFF0000) >> 16) / 255F;
            float g = ((k & 0xFF00) >> 8) / 255F;
            float b = (k & 0xFF) / 255F;
            TextureAtlasSprite sprite = FluidStackHooks.getStillTexture(be.fluidTank.getFluidStack());
            DynamicBakedModel fluidModel = new DynamicBakedModel(modelManager.bakedRegistry.get(new ResourceLocation("block/leaves")),sprite, ModelHelper.LEAVES_MODEL);
            stack.translate(0.5,0.5,0.5);
            stack.scale(0.995F,0.995F,0.995F);
            stack.translate(-0.5,-0.5,-0.5);
            dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.cutoutBlockSheet()),be.getBlockState(),fluidModel,r,g,b,i,j);
        }
        stack.popPose();

    }

    @Override
    public boolean shouldRenderOffScreen(FactocraftyMenuBlockEntity blockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(FactocraftyMenuBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }
}
