package wily.factocrafty.block.transport;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import wily.factocrafty.util.registering.IFactocraftyConduit;
import wily.factoryapi.base.SimpleFluidLoggedBlock;
import wily.factoryapi.util.VoxelShapeUtil;

public abstract class FactocraftySolidConduitBlock<T extends Enum<T> & IFactocraftyConduit<T,? extends FactocraftyConduitBlock<T,BE>,BE>, BE extends BlockEntity> extends FactocraftyConduitBlock<T,BE> implements SimpleFluidLoggedBlock {

    public EnumProperty<ConduitSide> UP;
    public EnumProperty<ConduitSide> DOWN;



    public FactocraftySolidConduitBlock(T tier, Properties properties) {
        super(tier, properties);
        registerDefaultState(defaultBlockState().setValue(UP, ConduitSide.NONE).setValue(DOWN, ConduitSide.NONE).setValue(FLUIDLOGGED(), 0));}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.getStateForPlacement(defaultBlockState(),ctx);
    }

    @Override
    public boolean isConnectionTypeValid(ConduitSide conduitSide) {
        return conduitSide != ConduitSide.UP && conduitSide != ConduitSide.DOWN;
    }

    protected abstract VoxelShape getCenterCubeShape();

    @Override
    public FluidState getFluidState(BlockState state) {
        return getSimpleFluidState(state);
    }
    @Override
    protected void setDefaultState() {
        super.setDefaultState();
        registerDefaultState(defaultBlockState().setValue(UP, ConduitSide.NONE).setValue(DOWN, ConduitSide.NONE).setValue(FLUIDLOGGED(), 0));
        PROPERTY_BY_DIRECTION.put(Direction.UP,UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN,DOWN);
    }


    protected VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = getCenterCubeShape();

        if (conduitType != null) for (Direction d : Direction.values()) {
            ConduitSide conduitSide = blockState.getValue(PROPERTY_BY_DIRECTION.get(d));
            if (conduitSide == ConduitSide.SIDE) {
                if (Direction.Plane.HORIZONTAL.test(d)) {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotateHorizontal(conduitType.getConduitShape().shapes[0], d));
                } else {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotate(VoxelShapeUtil.rotate(conduitType.getConduitShape().shapes[0],d.getOpposite()), Direction.NORTH));
                }
            }
        }
        return voxelShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        UP = EnumProperty.create("up", ConduitSide.class,this::isConnectionTypeValid);
        DOWN = EnumProperty.create("down", ConduitSide.class, this::isConnectionTypeValid);
        builder.add(UP,DOWN,FLUIDLOGGED());
    }

}
