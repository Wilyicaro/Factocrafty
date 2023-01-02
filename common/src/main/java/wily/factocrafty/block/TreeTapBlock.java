package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.TreeTapBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.ArrayList;
import java.util.List;

public class TreeTapBlock extends FactocraftyStorageBlock implements IFactocraftyOrientableBlock{
    public TreeTapBlock( Properties properties) {
        super(null, properties.noOcclusion());
        this.registerDefaultState(defaultBlockState().setValue(getFacingProperty(), Direction.NORTH));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TreeTapBlockEntity(blockPos,blockState);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.or( Shapes.join(Block.box(0,0,0,16,8,16),Block.box(2,1,2,14,8,14), BooleanOp.ONLY_FIRST) , VoxelShapeUtil.rotateHorizontal(Shapes.or(Block.box(6,7,10,10,10,13),Block.box(6,8,12,10,11,15),Block.box(6,9,14,10,12,17)),blockState.getValue(getFacingProperty())));
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        List<ItemStack> list = new ArrayList<>();
        Level level = builder.getLevel();
        if (level.random.nextFloat() < 0.9){
            if (level.random.nextFloat() >= 0.5) list.add(new ItemStack(Items.IRON_NUGGET,2));
            if (level.random.nextFloat() >= 0.5) list.add(new ItemStack(Items.STICK,(int) (level.random.nextFloat() * 8)));
        }else list.add(new ItemStack(asItem()));
        return list;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return IFactocraftyOrientableBlock.super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.HORIZONTAL_FACING;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getFacingProperty());
    }
}
