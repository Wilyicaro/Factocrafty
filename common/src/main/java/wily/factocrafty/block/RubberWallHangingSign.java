package wily.factocrafty.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.storage.loot.LootParams;
import wily.factocrafty.block.entity.RubberHangingSignBlockEntity;
import wily.factocrafty.block.entity.RubberSignBlockEntity;

import java.util.List;

public class RubberWallHangingSign extends WallHangingSignBlock {
    public RubberWallHangingSign(Properties properties, WoodType woodType) {
        super(properties, woodType);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RubberHangingSignBlockEntity(blockPos, blockState);
    }
    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return ImmutableList.of(new ItemStack(asItem()));
    }
}
