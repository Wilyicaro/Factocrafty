package wily.factocrafty.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.machines.entity.CompressorBlockEntity;
import wily.factocrafty.block.machines.entity.MaceratorBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class CompressorBlock extends FactocraftyMachineBlock {
    public CompressorBlock(Properties properties) {
        super(FactoryCapacityTiers.BASIC,properties);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CompressorBlockEntity(blockPos,blockState);
    }
}
