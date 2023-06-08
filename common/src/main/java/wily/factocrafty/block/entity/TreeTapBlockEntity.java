package wily.factocrafty.block.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.StrippedRubberLog;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FluidSide;
import wily.factoryapi.base.TransportState;

public class TreeTapBlockEntity extends StrippedRubberLogBlockEntity {
    public TreeTapBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.TREETAP_BLOCK_ENTITY.get(), blockPos, blockState);
        for (Direction d : Direction.values()) fluidSides.put(d, new FluidSide(fluidTank,TransportState.EXTRACT));
    }
    private int latexExtraction;
    private long oldFluidAmount = fluidTank.getFluidStack().getAmount();

    @Override
    protected long getTankCapacity() {
        return 2* FluidStack.bucketAmount();
    }

    @Override
    public void tick() {
        latexExtraction++;
        long i = 0;
        if (oldFluidAmount != fluidTank.getFluidStack().getAmount()){
            oldFluidAmount = fluidTank.getFluidStack().getAmount();
            level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_CLIENTS);
        }
        if (level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite())) instanceof StrippedRubberLogBlockEntity be && be.getBlockState().getBlock() instanceof StrippedRubberLog && be.getBlockState().getValue(StrippedRubberLog.CUT) && !be.fluidTank.getFluidStack().isEmpty() && fluidTank.getTotalSpace() > 0)  {
            boolean l = latexExtraction % 30 != 0;
            i = fluidTank.fill(be.fluidTank.drain((int) Math.min(fluidTank.getTotalSpace(), (FluidStack.bucketAmount() /100)),l),l);
        }
        if (i> 0 != getBlockState().getValue(FactocraftyStorageBlock.ACTIVE)) level.setBlock(worldPosition,getBlockState().setValue(FactocraftyStorageBlock.ACTIVE, i >0),3);

    }
}
