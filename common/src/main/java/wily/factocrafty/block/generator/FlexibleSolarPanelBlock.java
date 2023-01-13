package wily.factocrafty.block.generator;

import net.minecraft.core.BlockPos;
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
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factoryapi.util.VoxelShapeUtil;

public class FlexibleSolarPanelBlock extends SolarPanelBlock implements IFactocraftyOrientableBlock {

    public FlexibleSolarPanelBlock(SolarPanelTiers tier, Properties properties) {
        super(tier,properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return VoxelShapeUtil.rotate(super.getShape(blockState, blockGetter, blockPos, collisionContext), blockState.getValue(BlockStateProperties.FACING).getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return IFactocraftyOrientableBlock.super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return IFactocraftyOrientableBlock.super.mirror(blockState, mirror);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return IFactocraftyOrientableBlock.super.rotate(blockState, rotation);
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }
}
