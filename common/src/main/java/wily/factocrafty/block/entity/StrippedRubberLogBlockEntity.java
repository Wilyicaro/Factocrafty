package wily.factocrafty.block.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.block.StrippedRubberLog;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyFluids;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.List;

public class StrippedRubberLogBlockEntity extends FactocraftyStorageBlockEntity{
    public StrippedRubberLogBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Registration.STRIPPED_RUBBER_LOG_BLOCK_ENTITY.get(), blockPos, blockState);
        if (getBlockState().getValue(StrippedRubberLog.LATEX_STATE) && fluidTank.getFluidStack().isEmpty()) fluidTank.setFluid(FluidStack.create( FactocraftyFluids.LATEX.get(),getTankCapacity()));
    }
    public StrippedRubberLogBlockEntity(BlockEntityType<?> blockEntityType,BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> f.getFluid() == FactocraftyFluids.LATEX.get(), SlotsIdentifier.GENERIC, TransportState.EXTRACT_INSERT);
    }

    @Override
    public void tick() {
      if (getBlockState().getValue(StrippedRubberLog.LATEX_STATE) == fluidTank.getFluidStack().isEmpty()) level.setBlock(getBlockPos(),getBlockState().setValue(StrippedRubberLog.LATEX_STATE, !fluidTank.getFluidStack().isEmpty()),3);
    }

    @Override
    protected long getTankCapacity() {
        return 10 * FluidStackHooks.bucketAmount();
    }
    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
    }
}
