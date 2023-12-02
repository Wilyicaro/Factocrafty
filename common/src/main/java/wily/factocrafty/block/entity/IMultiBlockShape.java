package wily.factocrafty.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IMultiBlockShape {
    static List<BlockPos> getCenteredMultiBlockShape(CenteredPredicate predicate, Vec3i maxDistance, LevelAccessor accessor, BlockPos initialPos) {
        List<BlockPos> posCache = new ArrayList<>();
        for (int x = 0; x <= maxDistance.getX(); x++)
            for (Direction.AxisDirection xD : Direction.AxisDirection.values())
                for (int y = 0; y <= maxDistance.getY(); y++)
                    for (Direction.AxisDirection yD : Direction.AxisDirection.values())
                        for (int z = 0; z <= maxDistance.getZ(); z++)
                            for (Direction.AxisDirection zD : Direction.AxisDirection.values()) {
                                BlockPos pos = initialPos.mutable().move(x * xD.getStep(), y * yD.getStep(), z * zD.getStep());
                                if (predicate.test(pos, accessor.getBlockState(pos), new Vec3i(x, y, z), maxDistance)) posCache.add(pos);
                                else return Collections.emptyList();
                            }
        return posCache;
    }
    @FunctionalInterface
    interface CenteredPredicate {
        boolean test(BlockPos pos, BlockState state, Vec3i actualDistance, Vec3i maxDistance);
    }
}
