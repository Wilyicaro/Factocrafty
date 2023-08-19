package wily.factocrafty.block.transport.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.transport.BlockConnection;
import wily.factocrafty.block.transport.ConduitSide;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.block.transport.FactocraftySolidConduitBlock;
import wily.factocrafty.util.registering.IFactocraftyConduit;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IFactoryExpandedStorage;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.*;
import java.util.function.Consumer;

public abstract class ConduitBlockEntity<T extends IFactocraftyConduit<?,?,?>> extends BlockEntity implements IFactoryExpandedStorage {



    protected VoxelShape sideCollisionBox(Direction d) {return VoxelShapeUtil.rotateHorizontal(Block.box(6.4,0.4,15,9.6,3.6,16),d);};
    public Map<Direction, ConduitSide> connectedBlocks = new HashMap<>();

    public List<BlockConnection<ConduitBlockEntity<?>>> additionalConnections = new ArrayList<>();


    public ConduitBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockState.getBlock() instanceof FactocraftyConduitBlock<?,?> c  ? c.conduitType.getBlockEntity() : null, blockPos, blockState);
    }
    public T getConduitType(){
        return getBlockState().getBlock() instanceof FactocraftyConduitBlock<?,?> type ? (T)type.conduitType : null;
    }
    public FactocraftyConduitBlock<?,?> getBlock(){
        return (FactocraftyConduitBlock<?,?>)getBlockState().getBlock();
    }
    @Override
    public List<Direction> getBlockedSides() {
        List<Direction> list = new ArrayList<>();
        getBlock().PROPERTY_BY_DIRECTION.forEach((d, e)-> {if (getBlockState().getOptionalValue(e).isPresent() && getBlockState().getValue(e) != ConduitSide.SIDE) list.add(d);});
        return list;
    }


    public void updateAllStates(){
        Map<Direction, EnumProperty<ConduitSide>> directionProperty = getBlock().PROPERTY_BY_DIRECTION;

        BlockState blockState = getBlockState();
        for (Direction direction : Direction.values()){
            BlockPos blockPos = getBlockPos().relative(direction);
            BlockState SideBlock = level.getBlockState(blockPos);
            if (Direction.Plane.HORIZONTAL.test(direction)) {
                if (shouldConnectTo(blockPos, direction)){
                    blockState = blockState.setValue(directionProperty.get(direction), ConduitSide.SIDE);
                    connectedBlocks.put(direction, ConduitSide.SIDE);
                }
                else if (shouldConnectBlockBellowSide(blockPos.below(), SideBlock, level.getBlockState(getBlockPos().below()), direction)){
                    blockState = blockState.setValue(directionProperty.get(direction), ConduitSide.DOWN);
                    connectedBlocks.put(direction, ConduitSide.DOWN);
                }
                else if (shouldConnectBlockAboveSide(blockPos.above(), level.getBlockState(getBlockPos().above()), direction)) {
                    blockState =blockState.setValue(directionProperty.get(direction), ConduitSide.UP);
                    connectedBlocks.put(direction, ConduitSide.UP);
                }
                else if (blockState.getValue(directionProperty.get(direction)) != ConduitSide.NONE) {
                    blockState = blockState.setValue(directionProperty.get(direction), ConduitSide.NONE);
                    connectedBlocks.remove(direction);
                }
            } else if (direction == Direction.DOWN)
                if (shouldConnectBlockBellow(blockPos, SideBlock)){
                    connectedBlocks.put(direction, ConduitSide.DOWN);
                }else  connectedBlocks.remove(direction);
        }
        if (blockState != getBlockState()) level.setBlock(getBlockPos(),blockState, 3);
    }
    public @Nullable BlockPos getConnectedBlockPos(Direction direction){
        if (Direction.Plane.HORIZONTAL.test(direction)){
            if (connectedBlocks.get(direction) == ConduitSide.SIDE) return getBlockPos().relative(direction);
            else if (connectedBlocks.get(direction) == ConduitSide.DOWN) return getBlockPos().relative(direction).below();
            else if (connectedBlocks.get(direction) == ConduitSide.UP) return getBlockPos().relative(direction).above();
        }else if (direction == Direction.DOWN && connectedBlocks.get(direction) == ConduitSide.DOWN) return getBlockPos().below();
        return null;
    }
    protected  boolean shouldConnectBlockBellow(BlockPos blockPosBellow, BlockState blockBellow) {
        return (blockBellow.isFaceSturdy(level,blockPosBellow, Direction.UP, SupportType.CENTER) && shouldConnectTo(blockPosBellow));
    }
    protected  boolean shouldConnectBlockBellowSide(BlockPos blockSidesBellow, BlockState blockSides, BlockState blockBellow, Direction direction) {
        return (level.getBlockState(blockSidesBellow).getBlock() instanceof FactocraftyConduitBlock<?,?> && shouldConnectTo(blockSidesBellow, direction) && !(blockBellow.getBlock() instanceof FactocraftyConduitBlock<?,?>) && isAboveBlockValid(blockSides, direction.getOpposite()));
    }
    protected boolean shouldConnectBlockAboveSide(BlockPos blockState, BlockState blockStateUp, Direction direction) {
        return (shouldConnectTo(blockState, direction) &&  isAboveBlockValid(blockStateUp, direction));
    }
    protected boolean isAboveBlockValid(BlockState blockState, Direction direction) {
        VoxelShape shape = blockState.getShape(level, getBlockPos().above());
        VoxelShape shape1 = FactocraftyConduitBlock.getUpShape(direction,getConduitType()).move(0,-1,0);;

        return (!shape.isEmpty() && !(shape.bounds().intersects(shape1.bounds()) ) || shape.isEmpty());
    }
    protected  boolean shouldConnectTo(BlockPos pos) {
        return shouldConnectTo(pos, null);
    }
    protected  boolean shouldConnectTo(BlockPos pos, @Nullable Direction direction) {
        for (BlockConnection<?> connect : additionalConnections)
            if (connect.test(level, pos, direction)) return true;
        BlockState blockState = level.getBlockState(pos);
        if (blockState.isAir()) return false;
        @Nullable BlockEntity blockEntity =  level.getBlockEntity(pos);
        Block conduit = getBlockState().getBlock();
        if (blockEntity != null && shouldConnectToStorage(FactoryAPIPlatform.getPlatformFactoryStorage(blockEntity), direction)) {
            if (blockState.getBlock() instanceof FactocraftyConduitBlock<?, ?> sideConduit && sideConduit.conduitType.sameStorage(getConduitType()) && (sideConduit instanceof FactocraftySolidConduitBlock<?, ?>) == (conduit instanceof FactocraftySolidConduitBlock<?, ?>)) {
                return blockEntity instanceof ConduitBlockEntity<?> be && (direction == null || (Objects.requireNonNullElse(be.getConnectedBlockPos(direction.getOpposite()), pos).equals(getBlockPos()) || be.connectedBlocks.get(direction.getOpposite()) == null));
            }
            VoxelShape blockShape = blockState.getShape(level, pos);
            return direction == null || !blockShape.isEmpty() && blockShape.toAabbs().stream().anyMatch(a -> a.intersects(sideCollisionBox(direction).bounds()));
        }
        return false;
    }
    protected abstract boolean shouldConnectToStorage(IFactoryStorage storage, @Nullable Direction direction);
    public void tick() {
        if (!level.isClientSide) {
            updateAllStates();
            connectedBlocks.forEach(((direction, conduitSide) -> {
                @Nullable BlockPos pos = getConnectedBlockPos(direction);
                if (pos != null) {
                    if (level.getBlockEntity(pos) != null)
                        manageSidedTransference(level.getBlockState(pos), FactoryAPIPlatform.getPlatformFactoryStorage(level.getBlockEntity(pos)), direction);
                    additionalConnections.removeIf(c -> {
                        if (connectedBlocks.entrySet().stream().allMatch(e-> getConnectedBlockPos(e.getKey()) == null || !c.test(level,getConnectedBlockPos(e.getKey()),e.getKey()))) return true;
                        if (c.test(level,pos,direction))  return c.connectionTick(this, pos, direction);
                        return false;
                    });
                }
            }));
        }
    }

    public abstract void manageSidedTransference(BlockState blockState, IFactoryStorage storage, Direction direction);

    @Override
    public void load(CompoundTag compoundTag) {
        loadTag(compoundTag);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        saveTag(compoundTag);
    }
}
