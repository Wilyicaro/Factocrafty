package wily.factocrafty.block;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class FactocraftyFluidBlock extends ArchitecturyLiquidBlock {
    public FactocraftyFluidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public MutableComponent getName() {
        return FluidStack.create(fluid,FluidStack.bucketAmount()).getName().copy();
    }
}
