package wily.factocrafty.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.IronFurnaceBlockEntity;
import wily.factocrafty.init.Registration;

import java.util.List;
import java.util.Objects;

public class IronFurnace extends FurnaceBlock {
    public IronFurnace(Properties properties) {
        super(properties);
    }
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IronFurnaceBlockEntity(blockPos, blockState);
    }
    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof IronFurnaceBlockEntity) {
            player.openMenu((MenuProvider)blockEntity);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }

    }
    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(asItem()));
    }
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createFurnaceTicker(level, blockEntityType, Registration.IRON_FURNACE_BLOCK_ENTITY.get());
    }
    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createFurnaceTicker(Level level, BlockEntityType<T> blockEntityType, BlockEntityType<? extends AbstractFurnaceBlockEntity> blockEntityType2) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, blockEntityType2, (level1,blockPos,blockState,abstractFurnaceBlockEntity) -> ((IronFurnaceBlockEntity) Objects.requireNonNull(level.getBlockEntity(blockPos))).tick(level,blockPos,blockState,abstractFurnaceBlockEntity));
    }

}
