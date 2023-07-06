package wily.factocrafty.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;
import wily.factoryapi.base.FactoryCapacityTiers;

public class FactocraftyBlockEntityWLRenderer extends BlockEntityWithoutLevelRenderer {


    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public static FactocraftyBlockEntityWLRenderer INSTANCE = new FactocraftyBlockEntityWLRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());

    public FactocraftyBlockEntityWLRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }


    public static FactocraftyFluidTankBlockEntity getFluidTank(Direction dir, FactoryCapacityTiers capacityTier){
        return new FactocraftyFluidTankBlockEntity(capacityTier, BlockPos.ZERO, FactocraftyFluidTanks.getFromTier(capacityTier).defaultBlockState().setValue(BlockStateProperties.FACING, dir));
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {

        Item item = itemStack.getItem();
        if (item instanceof BlockItem blockItem) {
            BlockEntity blockEntity = null;
            if (blockItem.getBlock() instanceof FactocraftyFluidTankBlock fluidTankBlock) {
                Direction d = Direction.UP;
                if (itemStack.getOrCreateTag().contains(BlockItem.BLOCK_STATE_TAG) && itemStack.getTag().getCompound(BlockItem.BLOCK_STATE_TAG).contains("facing")) d = Direction.byName(itemStack.getTag().getCompound(BlockItem.BLOCK_STATE_TAG).getString("facing"));
                blockEntity = getFluidTank(d, fluidTankBlock.capacityTier);
                blockEntity.load(blockEntity.getUpdateTag().merge( itemStack.getOrCreateTag().getCompound("BlockEntityTag")));
            }
                this.blockEntityRenderDispatcher.renderItem(blockEntity, poseStack, multiBufferSource, i, j);
                }
            }

}
