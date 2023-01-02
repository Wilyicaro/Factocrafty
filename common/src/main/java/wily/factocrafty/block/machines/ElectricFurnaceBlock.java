package wily.factocrafty.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.machines.entity.ElectricFurnaceBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class ElectricFurnaceBlock extends FactocraftyMachineBlock {
    public ElectricFurnaceBlock(Properties properties) {
        super(FactoryCapacityTiers.BASIC,properties);
        hasSmokeParticles = true;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ElectricFurnaceBlockEntity(blockPos,blockState);
    }
}
