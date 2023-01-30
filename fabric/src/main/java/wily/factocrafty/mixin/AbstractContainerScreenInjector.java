package wily.factocrafty.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenInjector {

    @Shadow @Nullable protected abstract Slot findSlot(double d, double e);

    @ModifyVariable(method = ("mouseReleased"), at = @At("STORE"), ordinal = 0)
    private boolean injectMouseReleased(boolean flag, double d, double e, int i){
        Slot slot = findSlot(d, e);
        if (slot != null){
            return false;
        }
        return flag;
    }

}
