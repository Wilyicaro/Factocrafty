package wily.factocrafty.block.transport.fluid;

import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.block.transport.FactocraftySolidConduitBlock;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.util.registering.FactocraftyFluidPipes;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class FluidPipeBlock extends FactocraftySolidConduitBlock<FactocraftyFluidPipes, FluidPipeBlockEntity> {
    public FluidPipeBlock(FactocraftyFluidPipes tier, Properties properties) {
        super(tier, properties.lightLevel((b)-> Platform.isFabric()? b.getValue(FactocraftyLedBlock.LIGHT_VALUE) : 0));
        if (Platform.isFabric()) registerDefaultState(defaultBlockState().setValue(FactocraftyLedBlock.LIGHT_VALUE,0));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidPipeBlockEntity(blockPos,blockState);
    }
    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {
        return (getter.getBlockEntity(pos) instanceof FluidPipeBlockEntity fbe) ? FluidStackHooks.getLuminosity(fbe.fluidHandler.getFluidStack(), null, null) : 0;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add((conduitType.getCapacityTier().getTierComponent(false)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if(Platform.isFabric())builder.add(FactocraftyLedBlock.LIGHT_VALUE);
    }

    private static final VoxelShape SHAPE_CUBE = Block.box(5, 5, 5, 11, 11, 11);
    @Override
    protected VoxelShape getCenterCubeShape() {
        return SHAPE_CUBE;
    }
}
