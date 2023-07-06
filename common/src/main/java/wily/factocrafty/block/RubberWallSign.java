package wily.factocrafty.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import wily.factocrafty.block.entity.RubberSignBlockEntity;

import java.util.List;

public class RubberWallSign extends WallSignBlock {
    public RubberWallSign(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RubberSignBlockEntity(blockPos, blockState);
    }
    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return ImmutableList.of(new ItemStack(asItem()));
    }
}
