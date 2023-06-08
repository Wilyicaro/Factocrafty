package wily.factocrafty.block;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class FactocraftySourceFluid extends FactocraftyFlowingFluid {
    public FactocraftySourceFluid(ArchitecturyFluidAttributes attributes, @Nullable FactocraftyFlowingFluid.whenSpreadToFluid whenContactFluid) {
        super(attributes, whenContactFluid);
    }
    public int getAmount(FluidState state) {
        return 8;
    }

    public boolean isSource(FluidState state) {
        return true;
    }
}
