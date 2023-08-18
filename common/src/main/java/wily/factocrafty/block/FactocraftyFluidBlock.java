package wily.factocrafty.block;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class FactocraftyFluidBlock extends ArchitecturyLiquidBlock {
    private final boolean isGas;

    public FactocraftyFluidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties, boolean isGas) {
        super(fluid, properties);
        this.isGas = isGas;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        return isGas ? Items.BUCKET.getDefaultInstance(): super.pickupBlock(levelAccessor, blockPos, blockState);
    }

    @Override
    public MutableComponent getName() {
        return FluidStack.create(fluid,FluidStack.bucketAmount()).getName().copy();
    }
}
