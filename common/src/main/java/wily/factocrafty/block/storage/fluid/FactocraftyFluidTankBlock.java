package wily.factocrafty.block.storage.fluid;

import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.SimpleFluidLoggedBlock;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.List;

public class FactocraftyFluidTankBlock extends FactocraftyStorageBlock implements SimpleFluidLoggedBlock, IFactocraftyOrientableBlock {
    public FactocraftyFluidTankBlock(FactoryCapacityTiers tier, Properties properties) {
        super(tier, properties.noOcclusion());
        BlockState state = defaultBlockState().setValue(FLUIDLOGGED(), 0).setValue(getFacingProperty(), Direction.UP);
        registerDefaultState(state);
    }
    public static final VoxelShape BASIC_TANK_DEFAULT_SHAPE = Shapes.or( Block.box(1, 0, 1, 15, 16, 15),Block.box(1.2, 15.4, 1.2, 14.8, 16.4, 14.8));

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FactocraftyFluidTankBlockEntity(capacityTier,blockPos,blockState);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        int f = BLOCK_LOGGABLE_FLUIDS.indexOf(ctx.getLevel().getFluidState(ctx.getClickedPos()).getType());
        return IFactocraftyOrientableBlock.super.getStateForPlacement(ctx).setValue(FLUIDLOGGED(),f == -1 ? 0 : f);
    }
    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState f = BLOCK_LOGGABLE_FLUIDS.get( state.getValue(FLUIDLOGGED())).defaultFluidState();
        return f.getType() instanceof FlowingFluid g ? g.getSource(false) : f;
    }


    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return VoxelShapeUtil.rotate(BASIC_TANK_DEFAULT_SHAPE, blockState.getValue(BlockStateProperties.FACING).getOpposite());
    }

    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
        return (getter.getBlockEntity(pos) instanceof FactocraftyFluidTankBlockEntity fbe) ? FluidStackHooks.getLuminosity(fbe.fluidTank.getFluidStack(), null, null) : 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty(), FLUIDLOGGED());
    }
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }

}
