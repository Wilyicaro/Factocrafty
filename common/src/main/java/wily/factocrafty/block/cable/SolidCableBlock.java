package wily.factocrafty.block.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.cable.entity.SolidCableBlockEntity;
import wily.factoryapi.util.VoxelShapeUtil;

public class SolidCableBlock extends InsulatedCableBlock implements SimpleWaterloggedBlock {

    public static final EnumProperty<CableSide> UP = EnumProperty.create("up", CableSide.class);
    public static final EnumProperty<CableSide> DOWN = EnumProperty.create("down", CableSide.class);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_CUBE = Block.box(6, 6, 6, 10, 10, 10);

    public SolidCableBlock(CableTiers tier, Properties properties) {super(tier, properties);}
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    @Override
    protected void setDefaultState() {
        super.setDefaultState();
        registerDefaultState(defaultBlockState().setValue(UP, CableSide.NONE).setValue(DOWN,CableSide.NONE).setValue(WATERLOGGED, false));
        PROPERTY_BY_DIRECTION.put(Direction.UP,UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN,DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return super.getShape(blockState, blockGetter, blockPos, collisionContext);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean flag = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }
    protected VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = SHAPE_CUBE;

        if (cableTier != null) for (Direction d : Direction.values()) {
            CableSide cableSide = blockState.getValue(PROPERTY_BY_DIRECTION.get(d));
            if (cableSide == CableSide.SIDE) {
                if (Direction.Plane.HORIZONTAL.test(d)) {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotateHorizontal(cableTier.sideShape, d));
                } else {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotate(VoxelShapeUtil.rotate(cableTier.sideShape,d.getOpposite()), Direction.NORTH));
                }
            }
        }
        return voxelShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP,DOWN,WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolidCableBlockEntity(cableTier,blockPos, blockState);
    }
}
