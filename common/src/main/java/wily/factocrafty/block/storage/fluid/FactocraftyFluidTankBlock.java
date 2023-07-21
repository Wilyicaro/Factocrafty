package wily.factocrafty.block.storage.fluid;

import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.SimpleFluidLoggedBlock;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.List;

public class FactocraftyFluidTankBlock extends FactocraftyStorageBlock implements SimpleFluidLoggedBlock, IFactocraftyOrientableBlock {
    
    public FactocraftyFluidTankBlock(FactoryCapacityTiers tier, Properties properties) {
        super(tier, properties.noOcclusion().lightLevel((b)-> Platform.isFabric()? b.getValue(FactocraftyLedBlock.LIGHT_VALUE) : 0));
        BlockState state = defaultBlockState().setValue(FLUIDLOGGED(), 0).setValue(getFacingProperty(), Direction.UP);
        if (Platform.isFabric()) state.setValue(FactocraftyLedBlock.LIGHT_VALUE,0);
        registerDefaultState(state);
    }
    public static final VoxelShape BASIC_TANK_DEFAULT_SHAPE = Shapes.or( Block.box(1, 0, 1, 15, 16, 15),Block.box(1.2, 15.4, 1.2, 14.8, 16.4, 14.8));

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FactocraftyFluidTankBlockEntity(capacityTier,blockPos,blockState);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return SimpleFluidLoggedBlock.super.getStateForPlacement(IFactocraftyOrientableBlock.super.getStateForPlacement(ctx),ctx);
    }



    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return VoxelShapeUtil.rotate(BASIC_TANK_DEFAULT_SHAPE, blockState.getValue(BlockStateProperties.FACING).getOpposite());
    }

    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
        return (getter.getBlockEntity(pos) instanceof FactocraftyFluidTankBlockEntity fbe) ? FluidStackHooks.getLuminosity(fbe.fluidTank.getFluidStack(), null, null) : 0;
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty(), FLUIDLOGGED());
        if (Platform.isFabric()) builder.add(FactocraftyLedBlock.LIGHT_VALUE);
    }
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }

}
