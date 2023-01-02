package wily.factocrafty.block.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class GeneratorBlock extends FactocraftyMachineBlock {
    public GeneratorBlock(Properties properties) {
        super(FactoryCapacityTiers.BASIC,properties);
        hasFireParticles = hasSmokeParticles = true;
    }

    @Override
    public boolean produceEnergy() {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GeneratorBlockEntity(blockPos,blockState);
    }


    @Override
    public boolean isEnergyReceiver() {
        return false;
    }

}
