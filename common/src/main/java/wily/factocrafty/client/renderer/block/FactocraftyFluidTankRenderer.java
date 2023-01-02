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
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.client.renderer.DynamicBakedModel;
import wily.factocrafty.util.DirectionUtil;

import static wily.factocrafty.client.renderer.ModelHelper.FLUID_MODEL;

public class FactocraftyFluidTankRenderer implements BlockEntityRenderer<FactocraftyFluidTankBlockEntity> {

    BlockEntityRendererProvider.Context context;
    Minecraft mc = Minecraft.getInstance();


    ModelResourceLocation fluidModelLocation = new ModelResourceLocation( new ResourceLocation("factocrafty:fluid_model"),"");


    public FactocraftyFluidTankRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(FactocraftyFluidTankBlockEntity be, float f, PoseStack stack, MultiBufferSource bufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        FluidStack fluid = be.fluidTank.getFluidStack();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        BakedModel tank = modelManager.getModel(new ModelResourceLocation( new ResourceLocation( "factocrafty:fluid_tank"),""));
        DynamicBakedModel fluidModel = new DynamicBakedModel(modelManager.getModel(fluidModelLocation),FluidStackHooks.getStillTexture(fluid), FLUID_MODEL);


        int k =  be.getBlockPos() == null || be.getLevel() == null ? FluidStackHooks.getColor(fluid) : FluidStackHooks.getColor(be.getLevel(),be.getBlockPos(),fluid.getFluid().defaultFluidState());
        float r = ((k & 0xFF0000) >> 16) / 255F;
        float g = ((k & 0xFF00) >> 8) / 255F;
        float b = (k & 0xFF) / 255F;
        stack.pushPose();
        stack.translate(0.5,0.5,0.5);
        stack.mulPose(DirectionUtil.getRotation( be.getBlockState().getValue(BlockStateProperties.FACING)));
        stack.translate(-0.5,-0.5,-0.5);
        dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.cutoutBlockSheet()),be.getBlockState(), tank,1,1,1,i,j);
        stack.scale(1F, (be.getLevel() == null ? be.fluidTank.getFluidStack().getAmount() : be.smoothFluidAmount) / (float)be.fluidTank.getMaxFluid(),1F);
        if (!fluid.isEmpty())dispatcher.getModelRenderer().renderModel(stack.last(),bufferSource.getBuffer(Sheets.translucentCullBlockSheet()),be.getBlockState(), fluidModel,r,g,b,i,j);

        stack.popPose();
    }





}
