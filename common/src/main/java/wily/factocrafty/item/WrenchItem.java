package wily.factocrafty.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.init.Registration;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties.durability(225));
    }


    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        if (level.getBlockEntity(pos) instanceof FactocraftyStorageBlockEntity be){
            level.playSound(null, pos, Registration.WRENCH_TIGHT.get(), SoundSource.PLAYERS,1.0F,1.0F);
            BlockState blockState = be.getBlockState();
            FluidState fluidState = level.getFluidState(pos);
            if (player.isShiftKeyDown()) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? be : null;
                Block.dropResources(blockState, level, pos, blockEntity, player, useOnContext.getItemInHand());
                if (be.hasInventory()) be.inventory.clearContent();
                boolean bl2 = level.setBlock(pos, fluidState.createLegacyBlock(), 3);
                if (bl2) {
                    level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, blockState));
                }
                whenUseWrench(level.random.nextInt(2,6),useOnContext);
            }else {
                be.getLevel().setBlock(be.getBlockPos(), be.getBlockState().rotate(Rotation.CLOCKWISE_90), 3);
                whenUseWrench(1,useOnContext);
            }
            return InteractionResult.SUCCESS;
        }
        return  InteractionResult.CONSUME;
    }
    protected void whenUseWrench(int used, UseOnContext useOnContext){
        useOnContext.getItemInHand().hurtAndBreak(used, useOnContext.getPlayer(), player -> player.broadcastBreakEvent(useOnContext.getHand()));
    }
}
