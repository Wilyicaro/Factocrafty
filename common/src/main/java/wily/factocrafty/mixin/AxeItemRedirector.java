package wily.factocrafty.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.block.RubberLog;
import wily.factocrafty.block.StrippedRubberLog;
import wily.factocrafty.init.Registration;

import java.util.Map;
import java.util.Optional;

import static wily.factocrafty.block.RubberLog.getAxeStrippedResult;

@Mixin({AxeItem.class})
public abstract class AxeItemRedirector {


    @Shadow @Final
    protected static Map<Block, Block> STRIPPABLES;

    @ModifyVariable(method = ("useOn"), at = @At("STORE"), ordinal = 3)
    private Optional<BlockState> injectUse(Optional<BlockState> optional4,UseOnContext useOnContext){
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockState = level.getBlockState(blockPos);
        Optional<BlockState> optional = Optional.ofNullable( blockState.getBlock() instanceof StrippedRubberLog && !blockState.getValue(StrippedRubberLog.CUT) ? Registration.STRIPPED_RUBBER_LOG.get().defaultBlockState().setValue(StrippedRubberLog.CUT,true).setValue(RubberLog.LATEX_STATE, blockState.getValue(RubberLog.LATEX_STATE)).setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS)) : null);
        if (optional.isPresent() ){
            if (level.isClientSide)
                level.addDestroyBlockEffect(blockPos,optional.get());
            optional4 = optional;
            level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
        return optional4;
    }

    @Inject(method = ("getStripped"), at = @At("HEAD"), cancellable = true)
    private void injectBaseTick(BlockState blockState,CallbackInfoReturnable<Optional<BlockState>> info){
        info.setReturnValue(getAxeStrippedResult(STRIPPABLES,blockState));
    }
}
