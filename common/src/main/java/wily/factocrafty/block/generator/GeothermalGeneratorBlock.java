package wily.factocrafty.block.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.generator.entity.GeothermalGeneratorBlockEntity;

public class GeothermalGeneratorBlock extends GeneratorBlock {
    public GeothermalGeneratorBlock(Properties properties) {
        super(properties);
        hasFireParticles = hasSmokeParticles = false;
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GeothermalGeneratorBlockEntity(blockPos,blockState);
    }
    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
    }

}

