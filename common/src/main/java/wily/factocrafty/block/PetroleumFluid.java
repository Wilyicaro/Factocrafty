package wily.factocrafty.block;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class PetroleumFluid extends ArchitecturyFlowingFluid.Flowing {


    public PetroleumFluid(ArchitecturyFluidAttributes attributes) {
        super(attributes);
    }

    @Override
    protected int getSpreadDelay(Level level, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
        return super.getSpreadDelay(level, blockPos, fluidState, fluidState2);

    }

    public static class Source extends ArchitecturyFlowingFluid.Source {

        public Source(ArchitecturyFluidAttributes attributes) {
            super(attributes);
        }

    }

}
