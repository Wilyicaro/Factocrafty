package wily.factocrafty.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RubberLog extends RotatedPillarBlock {

    public static final BooleanProperty LATEX_STATE = BooleanProperty.create("latex");

    public RubberLog(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LATEX_STATE, false));
    }


    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return ImmutableList.of(new ItemStack(asItem()));
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LATEX_STATE);
    }
    public static Optional<BlockState> getAxeStrippedResult(Map<Block,Block> STRIPPABLES, BlockState blockState){
        return Optional.ofNullable(STRIPPABLES.get(blockState.getBlock())).map((block) -> {
            BlockState state = block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
            if (blockState.getBlock() instanceof RubberLog) state = state.setValue(RubberLog.LATEX_STATE,blockState.getValue(RubberLog.LATEX_STATE));
            return state;
        });

    }
}
