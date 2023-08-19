package wily.factocrafty.block.transport.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.transport.ConduitSide;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.block.transport.FactocraftySolidConduitBlock;
import wily.factocrafty.util.registering.IFactocraftyConduit;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.Map;

public abstract class SolidConduitBlockEntity<C extends IFactocraftyConduit<?,?,?>> extends ConduitBlockEntity<C> {


    public SolidConduitBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public void updateAllStates(){
        Map<Direction, EnumProperty<ConduitSide>> directionProperty = getBlock().PROPERTY_BY_DIRECTION;

        BlockState blockState = getBlockState();
        for (Direction direction : Direction.values()){
            BlockPos blockPos = getBlockPos().relative(direction);
            if (shouldConnectTo(blockPos, direction)){
                blockState = blockState.setValue(directionProperty.get(direction), ConduitSide.SIDE);
                connectedBlocks.put(direction, ConduitSide.SIDE);
            }else if (blockState.getValue(directionProperty.get(direction)) != ConduitSide.NONE) {
                blockState = blockState.setValue(directionProperty.get(direction), ConduitSide.NONE);
                connectedBlocks.remove(direction);
            }
        }
        if (blockState != getBlockState()) level.setBlock(getBlockPos(),blockState, 3);
    }

    @Override
    public @Nullable BlockPos getConnectedBlockPos(Direction direction){
        return connectedBlocks.get(direction) == ConduitSide.SIDE ? getBlockPos().relative(direction) : null;
    }
    protected VoxelShape sideCollisionBox(Direction d) {return VoxelShapeUtil.rotate(Block.box(6,15,6,10,16,10),d);};

}
