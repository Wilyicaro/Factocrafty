package wily.factocrafty.block.transport.energy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.CraftyTransaction;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CableBlock extends BaseEntityBlock implements IFactocraftyCYEnergyBlock {
    public static final EnumProperty<CableSide> NORTH = EnumProperty.create("north", CableSide.class);
    public static final EnumProperty<CableSide> EAST = EnumProperty.create("east", CableSide.class);
    public static final EnumProperty<CableSide> SOUTH = EnumProperty.create("south", CableSide.class);
    public static final EnumProperty<CableSide> WEST = EnumProperty.create("west", CableSide.class);

    public static final Map<Direction, EnumProperty<CableSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));


    protected FactocraftyCables cableTier;
    public CableBlock(FactocraftyCables cable, Properties properties) {
        super(properties);
        this.cableTier = cable;
        setDefaultState();
        for (BlockState blockState : this.getStateDefinition().getPossibleStates()) {
            SHAPES_CACHE.put(blockState, this.calculateShape(blockState));
        }
    }
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
    protected void setDefaultState(){
        this.registerDefaultState(defaultBlockState().setValue(NORTH, CableSide.NONE).setValue(EAST, CableSide.NONE).setValue(SOUTH, CableSide.NONE).setValue(WEST, CableSide.NONE));
    }
    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
        if (cableTier.insulation < 1 && entity instanceof LivingEntity && level.random.nextFloat() <= 0.45  && level.getBlockEntity(blockPos) instanceof CableBlockEntity be && be.energyStorage.getEnergyStored() > 100){
            if (entity.hurt(level.damageSources().lightningBolt(), be.energyStorage.consumeEnergy(new CraftyTransaction((int) Math.min(400, Math.pow(be.energyStorage.getEnergyStored() * (1 - cableTier.insulation), cableTier.energyTier.getConductivity())), be.energyStorage.storedTier).reduce(25), false).energy)){
                level.playSound(null,entity.getOnPos(), Registration.ELECTRIC_SHOCK.get(), SoundSource.BLOCKS,1.0F,1.0F);
            }
        }
    }
    private static final VoxelShape SHAPE_CUBE = Block.box(6, 0, 6, 10, 4, 10);

    protected static VoxelShape getSideShape(Direction d, FactocraftyCables tier) {return VoxelShapeUtil.rotateHorizontal(tier.cableShape.shapes[0],d);}

    public static VoxelShape getUpShape(Direction d, FactocraftyCables tier) {return VoxelShapeUtil.rotateHorizontal(tier.cableShape.shapes[1],d);}
    protected static final Map<BlockState, VoxelShape> SHAPES_CACHE = Maps.newHashMap();

    protected VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = SHAPE_CUBE;
        boolean insulated = !(this instanceof CableBlock);
        if (cableTier != null) for (Direction direction : Direction.Plane.HORIZONTAL) {
            CableSide cableSide = blockState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (cableSide == CableSide.SIDE || cableSide == CableSide.DOWN) {
                voxelShape = Shapes.or(voxelShape, getSideShape(direction,cableTier));
                continue;
            }
            if (cableSide != CableSide.UP) continue;
            voxelShape = Shapes.or(voxelShape, Shapes.or(getUpShape(direction,cableTier), getSideShape(direction,cableTier)));
        }
        return voxelShape;
    }


    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState blockState2, boolean bl) {
        super.onRemove(blockState, level, pos, blockState2, bl);

    }

    @Override
    public void unsupportedTierBurn(Level level, BlockPos pos) {
        IFactocraftyCYEnergyBlock.super.unsupportedTierBurn(level, pos);
        level.removeBlock(pos, true);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPES_CACHE.get(blockState);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(cableTier.energyTier.getEnergyTierComponent(false));
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
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CableBlockEntity(cableTier ,blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return(level1, pos, blockState1, be) -> {
            if (be instanceof CableBlockEntity cableBlockEntity)
                cableBlockEntity.tick();

        };
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(asItem()));
        return list;
    }


    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return cableTier.energyTier;
    }
}
