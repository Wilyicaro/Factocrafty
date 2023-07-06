package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.VoxelShapeUtil;

public class FactocraftyLedPanel extends FactocraftyLedBlock implements IFactocraftyOrientableBlock{

    public FactocraftyLedPanel(Properties properties, boolean hasRGB) {
        super(properties, 10, hasRGB);
        registerDefaultState(defaultBlockState().setValue(getFacingProperty(), Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return VoxelShapeUtil.rotate(Block.box(0,0,0,16,1.5,16),blockState.getValue(getFacingProperty()).getOpposite());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return IFactocraftyOrientableBlock.super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return IFactocraftyOrientableBlock.super.mirror(blockState, mirror);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return IFactocraftyOrientableBlock.super.rotate(blockState, rotation);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty());
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }
}
