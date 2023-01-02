package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;

public class FactocraftyMachineRenderer implements BlockEntityRenderer<FactocraftyProcessBlockEntity> {

    BlockEntityRendererProvider.Context context;

    public FactocraftyMachineRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(FactocraftyProcessBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        dispatcher.renderSingleBlock(blockEntity.getBlockState(),poseStack,multiBufferSource,i,j);
        //new BlockModel(new ResourceLocation("",""), List.of(new BlockElement()))
    }

    @Override
    public boolean shouldRenderOffScreen(FactocraftyProcessBlockEntity blockEntity) {
        return false;
    }

    @Override
    public int getViewDistance() {
        return BlockEntityRenderer.super.getViewDistance();
    }

    @Override
    public boolean shouldRender(FactocraftyProcessBlockEntity blockEntity, Vec3 vec3) {
        return true;
    }
}
