package wily.factocrafty.fluid;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class FactocraftySourceFluid extends FactocraftyFlowingFluid {
    public FactocraftySourceFluid(ArchitecturyFluidAttributes attributes, @Nullable FactocraftyFlowingFluid.whenSpreadToFluid whenContactFluid, boolean isGaseous) {
        super(attributes, whenContactFluid, isGaseous);
    }
    public int getAmount(FluidState state) {
        return 8;
    }

    public boolean isSource(FluidState state) {
        return true;
    }
}
