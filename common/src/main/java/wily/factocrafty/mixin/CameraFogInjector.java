package wily.factocrafty.mixin;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.tag.Fluids;

@Mixin(Camera.class)
public class CameraFogInjector {
    @Shadow @Final private BlockPos.MutableBlockPos blockPosition;

    @Shadow private BlockGetter level;

    @Shadow private boolean initialized;

    @Inject(at= @At("TAIL"), method = ("getFluidInCamera"), cancellable = true)
    public void returnFogType(CallbackInfoReturnable<FogType> info){
        FluidState fluidState = this.level.getFluidState(this.blockPosition);
        if(fluidState.is(Fluids.PETROLEUM) || fluidState.is(Fluids.LATEX)) info.setReturnValue(null);


    }

}
