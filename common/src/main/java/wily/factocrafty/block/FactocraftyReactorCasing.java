package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.ReactorCasingBlockEntity;
import wily.factocrafty.init.Registration;

public class FactocraftyReactorCasing extends FactocraftyBlock implements EntityBlock {

    public FactocraftyReactorCasing(Properties properties) {
        super(properties.isValidSpawn((var1, var2, var3, var4)-> var2.getBlockEntity(var3) instanceof ReactorCasingBlockEntity r && r.nuclearCorePos.isEmpty()));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ReactorCasingBlockEntity(blockPos,blockState);
    }
    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!player.isShiftKeyDown()){
            if (level.getBlockEntity(pos) instanceof ReactorCasingBlockEntity be && be.nuclearCorePos.isPresent())
                return level.getBlockState(be.nuclearCorePos.get()).use(level,player,hand,blockHitResult.withPosition(be.nuclearCorePos.get()));
        }
        return InteractionResult.PASS;
    }
}
