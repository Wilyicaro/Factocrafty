package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.List;

public class FactocraftyMachineBlock extends FactocraftyStorageBlock implements IFactocraftyCYEnergyBlock,IFactocraftyOrientableBlock {


    protected boolean hasFireParticles = false;

    protected boolean hasSmokeParticles = false;

    public FactocraftyMachineBlock(FactoryCapacityTiers tier, Properties properties) {
        super(tier,properties.lightLevel((b) -> b.getValue(ACTIVE) ?  6 : 0));
        this.registerDefaultState(defaultBlockState().setValue(getFacingProperty(), Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return IFactocraftyOrientableBlock.super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return IFactocraftyOrientableBlock.super.mirror(blockState, mirror);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return IFactocraftyOrientableBlock.super.rotate(blockState, rotation);
    }

    @Override
    public void unsupportedTierBurn(Level level, BlockPos pos) {
        IFactocraftyCYEnergyBlock.super.unsupportedTierBurn(level, pos);
        if (level.getBlockEntity(pos) instanceof FactocraftyMenuBlockEntity be) {
            be.energyStorage.storedTier = FactoryCapacityTiers.BURNED;
            be.energyStorage.setEnergyStored(0);
            be.setChanged();
        }
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if(capacityTier !=null)
            list.add(capacityTier.getEnergyTierComponent(false));
    }
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.HORIZONTAL_FACING;
    }
    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if ((hasSmokeParticles || hasFireParticles) && blockState.getValue(ACTIVE)) {
            double d = (double)blockPos.getX() + 0.5;
            double e = (double)blockPos.getY() + 0.1;
            double f = (double)blockPos.getZ() + 0.5;
            if ( randomSource.nextDouble() < 0.1) {
                level.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = blockState.getValue(getFacingProperty());
            Direction.Axis axis = direction.getAxis();
            double g = 0.52;
            double h = randomSource.nextDouble() * 0.6 - 0.3;
            double i = axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : h;
            double j = randomSource.nextDouble() * 6.0 / 16.0;
            double k = axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : h;
            if (hasSmokeParticles) level.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
            if (hasFireParticles) level.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return Registration.BLOCK_ENTITIES.getRegistrar().get(blockState.getBlock().arch$registryName()).create(blockPos,blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty());
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return capacityTier;
    }
}
