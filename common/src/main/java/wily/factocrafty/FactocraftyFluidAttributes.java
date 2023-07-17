package wily.factocrafty;

import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FactocraftyFluidAttributes extends SimpleArchitecturyFluidAttributes {
    public boolean inLevel = false;
    protected FactocraftyFluidAttributes(Supplier<? extends Fluid> flowingFluid, Supplier<? extends Fluid> sourceFluid) {
        super(flowingFluid, sourceFluid);
    }
    public static FactocraftyFluidAttributes of(Supplier<? extends Fluid> flowingFluid, Supplier<? extends Fluid> sourceFluid) {
        return new FactocraftyFluidAttributes(flowingFluid, sourceFluid);
    }
    public SimpleArchitecturyFluidAttributes colorInLevel(int color, boolean inLevel){
        this.inLevel = inLevel;
        return color(color);
    }

    @Override
    public int getColor() {
        return super.getColor((FluidStack) null, null, null);
    }

    @Override
    public int getColor(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return inLevel ? super.getColor(stack, level, pos) : 0xFFFFFFFF;
    }
}
