package wily.factocrafty.block.transport;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.util.registering.IFactocraftyConduit;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactocraftyConduitBlock<T extends Enum<T> & IFactocraftyConduit<T,? extends FactocraftyConduitBlock<T,BE>,BE>, BE extends BlockEntity>  extends BaseEntityBlock{
    public EnumProperty<ConduitSide> NORTH;
    public EnumProperty<ConduitSide> EAST;
    public EnumProperty<ConduitSide> SOUTH;
    public EnumProperty<ConduitSide> WEST;

    public Map<Direction, EnumProperty<ConduitSide>> PROPERTY_BY_DIRECTION;

    public boolean isConnectionTypeValid(ConduitSide conduitSide){
        return true;
    }
    public final T conduitType;
    public FactocraftyConduitBlock(T cable, Properties properties) {
        super(properties);
        this.conduitType = cable;
        setDefaultState();
        for (BlockState blockState : this.getStateDefinition().getPossibleStates()) {
            SHAPES_CACHE.put(blockState, this.calculateShape(blockState));
        }
    }

    protected void setDefaultState(){
        this.registerDefaultState(defaultBlockState().setValue(NORTH, ConduitSide.NONE).setValue(EAST, ConduitSide.NONE).setValue(SOUTH, ConduitSide.NONE).setValue(WEST, ConduitSide.NONE));
    }
    private static final VoxelShape SHAPE_CUBE = Block.box(6, 0, 6, 10, 4, 10);

    protected static VoxelShape getSideShape(Direction d, IFactocraftyConduit<?,?,?> tier) {return VoxelShapeUtil.rotateHorizontal(tier.getConduitShape().shapes[0],d);}

    public static VoxelShape getUpShape(Direction d, IFactocraftyConduit<?,?,?> tier) {return VoxelShapeUtil.rotateHorizontal(tier.getConduitShape().shapes[1],d);}
    protected static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();

    protected VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = SHAPE_CUBE;
        if (conduitType != null) for (Direction direction : Direction.Plane.HORIZONTAL) {
            ConduitSide conduitSide = blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (conduitSide == ConduitSide.SIDE || conduitSide == ConduitSide.DOWN) {
                voxelShape = Shapes.or(voxelShape, getSideShape(direction, conduitType));
                continue;
            }
            if (conduitSide != ConduitSide.UP) continue;
            voxelShape = Shapes.or(voxelShape, Shapes.or(getUpShape(direction, conduitType), getSideShape(direction, conduitType)));
        }
        return voxelShape;
    }


    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES_CACHE.get(blockState);
    }



    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return blockState.setValue(NORTH, blockState.getValue(SOUTH)).setValue(EAST, blockState.getValue(WEST)).setValue(SOUTH, blockState.getValue(NORTH)).setValue(WEST, blockState.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return blockState.setValue(NORTH, blockState.getValue(EAST)).setValue(EAST, blockState.getValue(SOUTH)).setValue(SOUTH, blockState.getValue(WEST)).setValue(WEST, blockState.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return blockState.setValue(NORTH, blockState.getValue(WEST)).setValue(EAST, blockState.getValue(NORTH)).setValue(SOUTH, blockState.getValue(EAST)).setValue(WEST, blockState.getValue(SOUTH));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return blockState.setValue(NORTH, blockState.getValue(SOUTH)).setValue(SOUTH, blockState.getValue(NORTH));
            }
            case FRONT_BACK: {
                return blockState.setValue(EAST, blockState.getValue(WEST)).setValue(WEST, blockState.getValue(EAST));
            }
        }
        return super.mirror(blockState, mirror);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        NORTH = EnumProperty.create("north", ConduitSide.class, this::isConnectionTypeValid);
        EAST = EnumProperty.create("east", ConduitSide.class, this::isConnectionTypeValid);
        SOUTH = EnumProperty.create("south", ConduitSide.class, this::isConnectionTypeValid);
        WEST = EnumProperty.create("west", ConduitSide.class, this::isConnectionTypeValid);
        PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
        builder.add(NORTH, EAST, SOUTH, WEST);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return(level1, pos, blockState1, be) -> {
            if (be instanceof ConduitBlockEntity<?> conduit)
                conduit.tick();
        };
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(asItem()));
        return list;
    }

}
