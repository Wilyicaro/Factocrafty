package wily.factocrafty.block.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.IFactoryExpandedStorage;

public interface BlockConnection<T extends BlockEntity & IFactoryExpandedStorage>
{
    boolean test(LevelAccessor level, BlockPos pos, @Nullable Direction direction);
    default boolean connectionTick(T be,BlockPos pos, @Nullable Direction direction){return false;}

}
