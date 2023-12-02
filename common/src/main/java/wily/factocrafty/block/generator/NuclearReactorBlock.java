package wily.factocrafty.block.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factocrafty.block.generator.entity.NuclearReactorBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class NuclearReactorBlock extends FactocraftyMachineBlock {
    public NuclearReactorBlock(Properties properties) {
        super(FactoryCapacityTiers.ADVANCED,properties.lightLevel((b) -> b.getValue(ACTIVE) ?  6 : 0));
    }

    @Override
    public boolean produceEnergy() {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new NuclearReactorBlockEntity(blockPos,blockState);
    }

    @Override
    public boolean isEnergyReceiver() {
        return false;
    }

}
