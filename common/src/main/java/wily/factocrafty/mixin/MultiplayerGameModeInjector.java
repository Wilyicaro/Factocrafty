package wily.factocrafty.mixin;

import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.item.FactocraftyDiggerItem;

@Mixin(MultiPlayerGameMode.class)
public class MultiplayerGameModeInjector {

    @Shadow @Final private Minecraft minecraft;

    @Shadow private BlockPos destroyBlockPos;

    @Shadow private ItemStack destroyingItem;
@PlatformOnly(PlatformOnly.FORGE)
    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void sameDestroyTarget(BlockPos arg,CallbackInfoReturnable<Boolean> info) {
        ItemStack itemstack = this.minecraft.player.getMainHandItem();
        info.setReturnValue(arg.equals(this.destroyBlockPos) && (ItemStack.isSameItemSameTags(itemstack, this.destroyingItem) ||(destroyingItem.getItem() instanceof FactocraftyDiggerItem i && i.allowContinuingBlockBreaking(minecraft.player,destroyingItem, itemstack))));
    }
}
