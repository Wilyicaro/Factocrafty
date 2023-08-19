package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.block.transport.ConduitSide;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factoryapi.util.DirectionUtil;

public class ConduitRenderer<C extends ConduitBlockEntity<?>> implements BlockEntityRenderer<C> {

    BlockEntityRendererProvider.Context context;

    public ConduitRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(C be, float f, PoseStack stack, MultiBufferSource multiBufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        BlockState blockState = be.getBlockState();
        RenderType renderType = ItemBlockRenderTypes.getRenderType(blockState, false);
        BakedModel cableUp = modelManager.bakedRegistry.get(be.getConduitType().getUpModelLocation());
        BakedModel cableSide = modelManager.bakedRegistry.get(be.getConduitType().getSideModelLocation());
        dispatcher.getModelRenderer().renderModel(stack.last(),multiBufferSource.getBuffer(renderType),be.getBlockState(), dispatcher.getBlockModel(blockState),1,1,1,i,j);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            ConduitSide side = be.getBlockState().getValue(be.getBlock().PROPERTY_BY_DIRECTION.get(d));
            stack.pushPose();
            stack.translate(0.5,0.5,0.5);
            stack.mulPose(DirectionUtil.getHorizontalRotation(d));
            stack.translate(-0.5,-0.5,-0.5);
            if (side.isConnected()){
                dispatcher.getModelRenderer().renderModel(stack.last(), multiBufferSource.getBuffer(renderType),be.getBlockState(),cableSide,1.0F,1.0F,1.0F,i,j);
                if (side == ConduitSide.UP)dispatcher.getModelRenderer().renderModel(stack.last(), multiBufferSource.getBuffer(renderType),be.getBlockState(),cableUp,1.0F,1.0F,1.0F,i,j);
            }
            stack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(C blockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(C blockEntity, Vec3 vec3) {
        return true;
    }
}
