package wily.factocrafty.block.generator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.Pair;
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
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factoryapi.util.VoxelShapeUtil;

public class FlexibleSolarPanelBlock extends SolarPanelBlock implements IFactocraftyOrientableBlock {

    final LoadingCache<Pair<Direction,VoxelShape>, VoxelShape> cache;
    public FlexibleSolarPanelBlock(SolarPanelTiers tier, Properties properties) {
        super(tier,properties);
        cache = CacheBuilder.newBuilder().maximumSize(12).build(new CacheLoader<>() {
            @Override
            public VoxelShape load(Pair<Direction, VoxelShape> key) {
                return VoxelShapeUtil.rotate(key.value(), key.key());
            }
        });
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return cache.getUnchecked(Pair.of(blockState.getValue(BlockStateProperties.FACING).getOpposite(),super.getShape(blockState, blockGetter, blockPos, collisionContext)));
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
