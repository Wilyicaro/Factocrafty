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
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.SideList;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.DirectionUtil;

import java.util.Optional;

public class SolidConduitRenderer<C extends ConduitBlockEntity<?>> implements BlockEntityRenderer<C> {

    BlockEntityRendererProvider.Context context;

    public SolidConduitRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    @Override
    public void render(C be, float f, PoseStack stack, MultiBufferSource multiBufferSource, int i, int j) {
        BlockRenderDispatcher dispatcher = context.getBlockRenderDispatcher();
        ModelManager modelManager = dispatcher.getBlockModelShaper().getModelManager();
        BlockState blockState = be.getBlockState();
        RenderType renderType = ItemBlockRenderTypes.getRenderType(blockState, false);
        dispatcher.getModelRenderer().renderModel(stack.last(),multiBufferSource.getBuffer(renderType),be.getBlockState(), dispatcher.getBlockModel(blockState),1,1,1,i,j);
        Storages.Storage<?> s =  be.getConduitType().getTransferenceStorage();
        ArbitrarySupplier<?> op = (s == Storages.FLUID ? be.getStorageSides(Storages.FLUID) : s == Storages.CRAFTY_ENERGY ? be.getStorageSides(Storages.CRAFTY_ENERGY) : s== Storages.ITEM ? be.getStorageSides(Storages.ITEM): ArbitrarySupplier.empty());
        for (Direction d : Direction.values()) {
            ConduitSide side = blockState.getValue(be.getBlock().PROPERTY_BY_DIRECTION.get(d));
            if (side.isConnected()) {
                BakedModel cableSide = modelManager.bakedRegistry.get(be.getConduitType().getSideModelLocation(op.isPresent() ? ((SideList<?>)op.get()).getTransport(d) : TransportState.EXTRACT_INSERT));
                stack.pushPose();
                stack.translate(0.5, 0.5, 0.5);
                stack.mulPose(DirectionUtil.getRotation(d));
                stack.translate(-0.5, -0.5, -0.5);
                dispatcher.getModelRenderer().renderModel(stack.last(), multiBufferSource.getBuffer(renderType), be.getBlockState(), cableSide, 1.0F, 1.0F, 1.0F, i, j);
                stack.popPose();
            }
        }

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
