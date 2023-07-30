package wily.factocrafty.block.transport.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.transport.energy.entity.SolidCableBlockEntity;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.SimpleFluidLoggedBlock;
import wily.factoryapi.util.VoxelShapeUtil;

public class SolidCableBlock extends CableBlock implements SimpleFluidLoggedBlock {

    public static final EnumProperty<CableSide> UP = EnumProperty.create("up", CableSide.class);
    public static final EnumProperty<CableSide> DOWN = EnumProperty.create("down", CableSide.class);


    private static final VoxelShape SHAPE_CUBE = Block.box(6, 6, 6, 10, 10, 10);

    public SolidCableBlock(FactocraftyCables tier, Properties properties) {super(tier, properties);}
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return SimpleFluidLoggedBlock.super.getStateForPlacement(defaultBlockState(),ctx);
    }
    @Override
    protected void setDefaultState() {
        super.setDefaultState();
        registerDefaultState(defaultBlockState().setValue(UP, CableSide.NONE).setValue(DOWN,CableSide.NONE).setValue(FLUIDLOGGED(), 0));
        PROPERTY_BY_DIRECTION.put(Direction.UP,UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN,DOWN);
    }


    protected VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = SHAPE_CUBE;

        if (cableTier != null) for (Direction d : Direction.values()) {
            CableSide cableSide = blockState.getValue(PROPERTY_BY_DIRECTION.get(d));
            if (cableSide == CableSide.SIDE) {
                if (Direction.Plane.HORIZONTAL.test(d)) {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotateHorizontal(cableTier.cableShape.shapes[0], d));
                } else {
                    voxelShape = Shapes.or(voxelShape, VoxelShapeUtil.rotate(VoxelShapeUtil.rotate(cableTier.cableShape.shapes[0],d.getOpposite()), Direction.NORTH));
                }
            }
        }
        return voxelShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UP,DOWN,FLUIDLOGGED());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolidCableBlockEntity(cableTier,blockPos, blockState);
    }
}
