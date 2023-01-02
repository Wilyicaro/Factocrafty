package wily.factocrafty.forge.mixin;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static wily.factocrafty.block.RubberLog.getAxeStrippedResult;

@Mixin({AxeItem.class})
public class ForgeAxeItemRedirector {
    @Shadow @Final protected static Map<Block, Block> STRIPPABLES;

    @Inject(method = ("getAxeStrippingState"), at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectBaseTick(BlockState blockState, CallbackInfoReturnable<BlockState> info){
        info.setReturnValue(getAxeStrippedResult(STRIPPABLES, blockState).orElse(null));
    }
}
