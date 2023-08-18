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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.entity.TreeTapBlockEntity;
import wily.factocrafty.client.renderer.DynamicBakedModel;
import wily.factoryapi.util.DirectionUtil;

import static wily.factocrafty.client.renderer.ModelHelper.*;

public class TreeTapRenderer implements BlockEntityRenderer<TreeTapBlockEntity> {

    BlockEntityRendererProvider.Context context;
    Minecraft mc = Minecraft.getInstance();

    public static final ResourceLocation TREETAP_BOWL  = new ResourceLocation("factocrafty:block/treetap/treetap_bowl");
    public static final ResourceLocation TREETAP_LATEX  = new ResourceLocation("factocrafty:block/treetap/treetap_latex");
    public static final ResourceLocation TREETAP_LATEX_FALL  = new ResourceLocation("factocrafty:block/treetap/treetap_latex_fall");


    public TreeTapRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(TreeTapBlockEntity be, float f, PoseStack stack, MultiBufferSource bufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        FluidStack fluid = be.fluidTank.getFluidStack();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        BakedModel treetap = modelManager.bakedRegistry.get(TREETAP_BOWL);
        DynamicBakedModel latexModel = new DynamicBakedModel(modelManager.bakedRegistry.get(TREETAP_LATEX),FluidStackHooks.getStillTexture(fluid), TREETAP_LATEX_MODEL);
        DynamicBakedModel latexFallModel = new DynamicBakedModel(modelManager.bakedRegistry.get(TREETAP_LATEX_FALL),FluidStackHooks.getFlowingTexture(fluid), TREETAP_LATEX_FALL_MODEL);

        boolean hasFluid = !fluid.isEmpty();
        stack.pushPose();
        stack.translate(0.5,0.5,0.5);
        stack.mulPose(DirectionUtil.getHorizontalRotation(be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)));
        stack.translate(-0.5,-0.5,-0.5);
        if (hasFluid && be.getBlockState().getValue(FactocraftyStorageBlock.ACTIVE))
            dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.translucentCullBlockSheet()),be.getBlockState(), latexFallModel,1,1,1,i,j);
        dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.cutoutBlockSheet()),be.getBlockState(), treetap,1,1,1,i,j);
        if (hasFluid) {
            stack.translate(0.5,0.0625,0.5);
            stack.scale(1F, be.fluidTank.getFluidStack().getAmount() / (float)be.fluidTank.getMaxFluid(),1F);
            stack.translate(-0.5,-0.0625,-0.5);
            dispatcher.getModelRenderer().renderModel(stack.last(), bufferSource.getBuffer(Sheets.translucentCullBlockSheet()), be.getBlockState(), latexModel, 1, 1, 1, i, j);
        }


        stack.popPose();
    }





}
