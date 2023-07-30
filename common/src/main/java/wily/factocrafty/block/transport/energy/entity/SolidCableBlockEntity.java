package wily.factocrafty.block.transport.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.transport.energy.CableSide;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factocrafty.block.transport.energy.CableBlock;
import wily.factocrafty.block.transport.energy.SolidCableBlock;

import java.util.Map;

public class SolidCableBlockEntity extends CableBlockEntity {






    public SolidCableBlockEntity(FactocraftyCables tier, BlockPos blockPos, BlockState blockState) {
        this(tier.getBlockEntity(),tier, blockPos, blockState);
    }
    public SolidCableBlockEntity(BlockEntityType<?> type, FactocraftyCables tier, BlockPos blockPos, BlockState blockState) {
        super(type, tier,blockPos, blockState);

    }
@Override
    public void updateAllStates(){
        Map<Direction, EnumProperty<CableSide>> directionProperty = CableBlock.PROPERTY_BY_DIRECTION;

        BlockState blockState = getBlockState();
        for (Direction direction : Direction.values()){
            BlockPos blockPos = getBlockPos().relative(direction);
                if (shouldConnectTo(blockPos, direction)){
                    blockState = blockState.setValue(directionProperty.get(direction), CableSide.SIDE);
                    connectedBlocks.put(direction, CableSide.SIDE);
                }else if (blockState.getValue(directionProperty.get(direction)) != CableSide.NONE) {
                    blockState = blockState.setValue(directionProperty.get(direction), CableSide.NONE);
                    connectedBlocks.remove(direction);
                }
            }
        if (blockState != getBlockState()) level.setBlock(getBlockPos(),blockState, 3);
    }

    @Override
    protected boolean shouldConnectTo(BlockPos pos, @Nullable Direction direction) {
        BlockState blockState = level.getBlockState(pos);
        if (!blockState.isFaceSturdy(level,pos,direction.getOpposite(), SupportType.CENTER) && !(blockState.getBlock() instanceof SolidCableBlock)) return false;
        return super.shouldConnectTo(pos, direction);
    }

    @Override
    public @Nullable BlockPos getConnectedBlockPos(Direction direction){
        return connectedBlocks.get(direction) == CableSide.SIDE ? getBlockPos().relative(direction) : null;
    }

}
