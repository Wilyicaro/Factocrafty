package wily.factocrafty.block.transport.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.transport.energy.*;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.*;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.*;

public class CableBlockEntity extends BlockEntity implements IFactoryStorage {



    private VoxelShape collisionBox(Direction d) {return VoxelShapeUtil.rotateHorizontal(Block.box(6.4,0.4,15,9.6,3.6,16),d);};
    public Map<Direction, CableSide> connectedBlocks = new HashMap<>();

    public FactocraftyCables cableTier;

    public CableBlockEntity(FactocraftyCables tier, BlockPos blockPos, BlockState blockState) {
        this(tier.getBlockEntity(),tier, blockPos, blockState);
    }
    public CableBlockEntity(BlockEntityType<?> type, FactocraftyCables tier, BlockPos blockPos, BlockState blockState) {
        super(type, blockPos, blockState);
        this.cableTier = tier;
        energyStorage = new CYEnergyStorage(this, 0,cableTier.energyTier.energyCapacity, maxEnergyTransfer(),cableTier.energyTier);
    }

    public final CYEnergyStorage energyStorage;

    public int maxEnergyTransfer() {
        return (int) (cableTier.energyTier.energyCapacity * cableTier.energyTier.getConductivity());
    }


    public void updateAllStates(){
        Map<Direction, EnumProperty<CableSide>> directionProperty = CableBlock.PROPERTY_BY_DIRECTION;

        BlockState blockState = getBlockState();
        for (Direction direction : Direction.values()){
            BlockPos blockPos = getBlockPos().relative(direction);
            BlockState SideBlock = level.getBlockState(blockPos);
            if (Direction.Plane.HORIZONTAL.test(direction)) {
                if (shouldConnectTo(blockPos, direction)){
                    blockState = blockState.setValue(directionProperty.get(direction), CableSide.SIDE);
                    connectedBlocks.put(direction, CableSide.SIDE);
                }
                else if (shouldConnectBlockBellowSide(blockPos.below(), SideBlock, level.getBlockState(getBlockPos().below()), direction)){
                    blockState = blockState.setValue(directionProperty.get(direction), CableSide.DOWN);
                    connectedBlocks.put(direction, CableSide.DOWN);
                }
                else if (shouldConnectBlockAboveSide(blockPos.above(), level.getBlockState(getBlockPos().above()), direction)) {
                    blockState =blockState.setValue(directionProperty.get(direction), CableSide.UP);
                    connectedBlocks.put(direction, CableSide.UP);
                }
                else if (blockState.getValue(directionProperty.get(direction)) != CableSide.NONE) {
                    blockState = blockState.setValue(directionProperty.get(direction), CableSide.NONE);
                    connectedBlocks.remove(direction);
                }
            } else if (direction == Direction.DOWN)
                if (shouldConnectBlockBellow(blockPos, SideBlock)){
                connectedBlocks.put(direction, CableSide.DOWN);
            }else  connectedBlocks.remove(direction);
            }
        if (blockState != getBlockState()) level.setBlock(getBlockPos(),blockState, 3);
    }
    public @Nullable BlockPos getConnectedBlockPos(Direction direction){
        if (Direction.Plane.HORIZONTAL.test(direction)){
            if (connectedBlocks.get(direction) == CableSide.SIDE) return getBlockPos().relative(direction);
            else if (connectedBlocks.get(direction) == CableSide.DOWN) return getBlockPos().relative(direction).below();
            else if (connectedBlocks.get(direction) == CableSide.UP) return getBlockPos().relative(direction).above();
        }else if (direction == Direction.DOWN && connectedBlocks.get(direction) == CableSide.DOWN) return getBlockPos().below();
        return null;
    }
    protected  boolean shouldConnectBlockBellow(BlockPos blockPosBellow, BlockState blockBellow) {
        return (blockBellow.isFaceSturdy(level,blockPosBellow, Direction.UP, SupportType.CENTER) && shouldConnectTo(blockPosBellow));
    }
    protected  boolean shouldConnectBlockBellowSide(BlockPos blockSidesBellow, BlockState blockSides, BlockState blockBellow, Direction direction) {
        return (level.getBlockState(blockSidesBellow).getBlock() instanceof CableBlock && shouldConnectTo(blockSidesBellow, direction) && !(blockBellow.getBlock() instanceof CableBlock) && isAboveBlockValid(blockSides, direction.getOpposite()));
    }
    protected boolean shouldConnectBlockAboveSide(BlockPos blockState, BlockState blockStateUp, Direction direction) {
        return (shouldConnectTo(blockState, direction) &&  isAboveBlockValid(blockStateUp, direction));
    }
    protected boolean isAboveBlockValid(BlockState blockState, Direction direction) {
        VoxelShape shape = blockState.getShape(level, getBlockPos().above());
        VoxelShape shape1 = CableBlock.getUpShape(direction,cableTier);
        AABB  aabb = new AABB(new Vec3(shape1.bounds().minX, shape1.bounds().minY - 1,shape1.bounds().minZ), new Vec3(shape1.bounds().maxX, shape1.bounds().maxY - 1,shape1.bounds().maxZ));
        boolean bl = !shape.isEmpty();

        return (bl && !(shape.bounds().intersects(aabb) ) || shape.isEmpty());
    }
    protected  boolean shouldConnectTo(BlockPos pos) {
        return shouldConnectTo(pos, null);
    }
    protected  boolean shouldConnectTo(BlockPos pos, @Nullable Direction direction) {
        BlockState blockState = level.getBlockState(pos);
        Block cable = getBlockState().getBlock();
        VoxelShape blockShape = blockState.getShape(level,pos);

        if (blockState.getBlock() instanceof CableBlock otherCable) {
            if ((otherCable instanceof SolidCableBlock) && !(cable instanceof SolidCableBlock)) return false;
            return level.getBlockEntity(pos) instanceof CableBlockEntity be && (direction == null || (Objects.requireNonNullElse(be.getConnectedBlockPos(direction.getOpposite()),pos).equals(getBlockPos()) || be.connectedBlocks.get(direction.getOpposite()) == null));
        }
        if (blockState.getBlock() instanceof IFactocraftyCYEnergyBlock) {
            if (direction != null && !blockShape.isEmpty() && !blockShape.bounds().intersects(collisionBox(direction).bounds())) return false;
            return level.getBlockEntity(pos) instanceof IFactoryStorage storage && (direction == null || (storage.energySides().isPresent()  && (storage.energySides().get().getOrDefault(direction.getOpposite(), TransportState.NONE).isUsable() && !(blockState.getShape(level,pos).getFaceShape(direction.getOpposite()).isEmpty()))));
        }
        return false;
    }
    public void tick() {
        updateAllStates();
        if (!level.isClientSide)
            for (Direction direction : Direction.values()) {
                if (getConnectedBlockPos(direction) == null) continue;
            if (level.getBlockState(getConnectedBlockPos(direction)).getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock) {
                IFactoryStorage CYEbe = (IFactoryStorage) level.getBlockEntity(getConnectedBlockPos(direction));
                if (CYEbe != null) {
                    CYEbe.getStorage(Storages.CRAFTY_ENERGY, direction.getOpposite()).ifPresent((e)->{

                    if (CYEbe instanceof CableBlockEntity ) {
                        if (e.getEnergyStored() < energyStorage.getEnergyStored()) {

                            int i = (energyStorage.getEnergyStored() - e.getEnergyStored()) / 2;
                            energyStorage.consumeEnergy((e.receiveEnergy(new CraftyTransaction(Math.min(i, maxEnergyTransfer()), energyStorage.storedTier), false).reduce(cableTier.transferenceEfficiency())), false);
                        }
                    }else {
                        if (!energyBlock.produceEnergy())
                            transferEnergyTo(direction,e);
                        if(!energyBlock.isEnergyReceiver() || energyBlock.produceEnergy() || (CYEbe.energySides().isPresent() && !CYEbe.energySides().get().get(direction.getOpposite()).canInsert())) {
                            transferEnergyFrom(direction, e);
                        }
                    }
                    });

                }
            }
        }
    }



    @Override
    public <T extends IPlatformHandlerApi> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.CRAFTY_ENERGY) return (Optional<T>) Optional.of(energyStorage);
        return Optional.empty();
    }


    @Override
    public void load(CompoundTag compoundTag) {
        loadTag(compoundTag);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        saveTag(compoundTag);
    }
}
