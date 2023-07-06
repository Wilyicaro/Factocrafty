package wily.factocrafty.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public interface IFactocraftyOrientableBlock {

     DirectionProperty getFacingProperty();


    default BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = getFacingProperty() != BlockStateProperties.HORIZONTAL_FACING ? ctx.getNearestLookingDirection() : ctx.getHorizontalDirection();
        return this instanceof Block block ? block.defaultBlockState().setValue(getFacingProperty(), facing.getOpposite()) : ctx.getPlayer().getBlockStateOn();
    }
    default BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
        return p_185499_1_.setValue(getFacingProperty(), p_185499_2_.rotate(p_185499_1_.getValue(getFacingProperty())));
    }

    default BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
        return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(getFacingProperty())));
    }

}
