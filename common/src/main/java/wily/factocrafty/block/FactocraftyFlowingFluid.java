package wily.factocrafty.block;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class FactocraftyFlowingFluid extends ArchitecturyFlowingFluid.Flowing {
    private final  whenSpreadToFluid whenContactFluid;
    public FactocraftyFlowingFluid(ArchitecturyFluidAttributes attributes, @Nullable whenSpreadToFluid whenContactFluid) {
        super(attributes);
        this.whenContactFluid = whenContactFluid;
    }
    public interface whenSpreadToFluid{
        boolean result(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState);
    }

    public boolean isValidToGetFog(FluidState fluidState){
        return !fluidState.is(FluidTags.WATER) && !fluidState.is(FluidTags.LAVA);
    }
    @Override
    protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (whenContactFluid != null && whenContactFluid.result(levelAccessor, blockPos, blockState, direction, fluidState)) {
            return;
        }
        super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
    }

}
