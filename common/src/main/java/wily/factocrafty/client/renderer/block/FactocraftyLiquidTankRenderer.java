package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.client.renderer.DynamicBakedModel;
import wily.factoryapi.util.DirectionUtil;

import static wily.factocrafty.client.renderer.ModelHelper.FLUID_MODEL;

public class FactocraftyLiquidTankRenderer implements BlockEntityRenderer<FactocraftyFluidTankBlockEntity> {

    BlockEntityRendererProvider.Context context;


    public static final ResourceLocation FLUID_MODEL_LOCATION = new ResourceLocation("factocrafty:block/fluid_tank/fluid_model");


    public FactocraftyLiquidTankRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(FactocraftyFluidTankBlockEntity be, float f, PoseStack stack, MultiBufferSource bufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        FluidStack fluid = be.fluidTank.getFluidStack();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        DynamicBakedModel fluidModel = new DynamicBakedModel(modelManager.bakedRegistry.get(FLUID_MODEL_LOCATION),FluidStackHooks.getStillTexture(fluid), FLUID_MODEL);


        int k = be.getLevel() == null ? FluidStackHooks.getColor(fluid) : FluidStackHooks.getColor(be.getLevel(),be.getBlockPos(),fluid.getFluid().defaultFluidState());
        float r = ((k & 0xFF0000) >> 16) / 255F;
        float g = ((k & 0xFF00) >> 8) / 255F;
        float b = (k & 0xFF) / 255F;
        stack.pushPose();
        stack.translate(0.5,0.5,0.5);
        stack.mulPose(DirectionUtil.getRotation( be.getBlockState().getValue(BlockStateProperties.FACING)));
        stack.translate(-0.5,-0.5,-0.5);
        dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.cutoutBlockSheet()),be.getBlockState(), dispatcher.getBlockModel(be.getBlockState()),1,1,1,i,j);

        if (!fluid.isEmpty()){
            if (be.hasLevel()) {
                Direction rel = be.getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
                BlockPos relPos = be.getBlockPos().relative(rel);
                BlockState rState = be.getLevel().getBlockState(relPos);
                if (rState.getBlock() instanceof FactocraftyFluidTankBlock && rState.getValue(BlockStateProperties.FACING).equals(be.getBlockState().getValue(BlockStateProperties.FACING)) &&  be.getLevel().getBlockEntity(relPos) instanceof FactocraftyFluidTankBlockEntity relBe && relBe.fluidTank.getFluidStack().isFluidEqual(be.fluidTank.getFluidStack())){
                    stack.translate(0, be.unitHeight= -(0.8125D-0.8125D*relBe.smoothFluidAmount / (float)relBe.fluidTank.getMaxFluid()) - 0.22 + relBe.unitHeight, 0);
                }
            }
            fluidModel.setScale(1.0F,Math.max(0.05F,(!be.hasLevel() ? be.fluidTank.getFluidStack().getAmount() : be.smoothFluidAmount) / (float)be.fluidTank.getMaxFluid()),1.0F);
            dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.translucentCullBlockSheet()),be.getBlockState(), fluidModel,r,g,b,i,j);

        }

        stack.popPose();
    }





}
