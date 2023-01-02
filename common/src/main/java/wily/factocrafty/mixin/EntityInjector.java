package wily.factocrafty.mixin;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.tag.Fluids;

@Mixin({Entity.class})
public abstract class EntityInjector {

    @Shadow public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d);

    @Shadow @Nullable public abstract Entity getVehicle();

    @Shadow protected boolean wasTouchingWater;

    @Shadow public abstract void resetFallDistance();

    @Shadow abstract void updateInWaterStateAndDoWaterCurrentPushing();

    @Shadow public abstract void clearFire();

    @PlatformOnly(PlatformOnly.FABRIC)
    @Inject(method = ("updateInWaterStateAndDoFluidPushing"), at=@At(value = "TAIL"), cancellable = true)
    public void doCustomFluidPushing(CallbackInfoReturnable<Boolean> info){
        updateInFluidStateAndDoFluidCurrentPushing();
        info.setReturnValue(info.getReturnValueZ());

    }
    void updateInFluidStateAndDoFluidCurrentPushing() {

            if (this.getVehicle() instanceof Boat) {
                this.wasTouchingWater = false;
            } else if (this.updateFluidHeightAndDoFluidPushing(Fluids.PETROLEUM, 0.010)) {
                this.resetFallDistance();
                this.wasTouchingWater = true;
            } else if (this.updateFluidHeightAndDoFluidPushing(Fluids.LATEX, 0.018)) {
                this.resetFallDistance();
                this.wasTouchingWater = true;
                this.clearFire();
            }


    }
}
