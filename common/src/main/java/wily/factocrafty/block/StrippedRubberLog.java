package wily.factocrafty.block;

import com.google.common.collect.ImmutableList;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.block.entity.StrippedRubberLogBlockEntity;
import wily.factocrafty.block.entity.TreeTapBlockEntity;
import wily.factocrafty.init.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StrippedRubberLog extends RubberLog implements EntityBlock {

    public static final BooleanProperty CUT = BooleanProperty.create("cut");


    public StrippedRubberLog(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CUT,false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CUT);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return(level1, pos, blockState1, be) -> {
            if (be instanceof FactocraftyStorageBlockEntity factocraftyMachineBlockEntity)
                factocraftyMachineBlockEntity.tick();

        };
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(asItem());
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag blockStateTag = new CompoundTag();
        blockStateTag.putString(CUT.getName(), blockState.getValue(CUT).toString());
        tag.put(BlockItem.BLOCK_STATE_TAG,blockStateTag);
        stack.setTag(tag);
        return ImmutableList.of(stack);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new StrippedRubberLogBlockEntity(blockPos,blockState);
    }
}
