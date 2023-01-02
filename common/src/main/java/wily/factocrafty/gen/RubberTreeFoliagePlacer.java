package wily.factocrafty.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import wily.factocrafty.init.Registration;

import java.util.function.BiConsumer;

public class RubberTreeFoliagePlacer extends FancyFoliagePlacer {
    public RubberTreeFoliagePlacer(IntProvider p_161356_, IntProvider p_161357_, int p_161358_) {
        super(p_161356_, p_161357_, p_161358_);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return Registration.RUBBER_TREE_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader levelSimulatedReader, BiConsumer<BlockPos, BlockState> biConsumer, RandomSource randomSource, TreeConfiguration treeConfiguration, int i, FoliagePlacer.FoliageAttachment foliageAttachment, int j, int k, int l) {
        for(int m = l; m >= l - j; --m) {
            int n = k + (m != l && m != l - j ? 2 : 0);
            this.placeLeavesRow(levelSimulatedReader, biConsumer, randomSource, treeConfiguration, foliageAttachment.pos(), n, m, foliageAttachment.doubleTrunk());
        }
    }

}