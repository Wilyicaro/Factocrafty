package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.util.registering.IFactocraftyBlockEntityType;
import wily.factoryapi.base.SimpleFluidLoggedBlock;

public class FactocraftyEntityBlock extends FactocraftyBlock implements EntityBlock {
    public FactocraftyEntityBlock(Properties properties) {
        super(properties);
    }
    @Override
    public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int i, int j) {
        super.triggerEvent(blockState, level, blockPos, i, j);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.triggerEvent(i, j);
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        return blockEntity instanceof MenuProvider ? (MenuProvider) blockEntity : null;
    }

    public @Nullable IFactocraftyBlockEntityType getBlockEntityType(){
        return ()->IFactocraftyBlockEntityType.ofBlock(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return  getBlockEntityType() == null ? null : getBlockEntityType().get().create(blockPos,blockState);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return blockEntityType == getBlockEntityType().get()? getBlockEntityType().getTicker() : null;
    }
}
