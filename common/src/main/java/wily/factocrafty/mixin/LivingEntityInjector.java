package wily.factocrafty.mixin;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wily.factocrafty.tag.Fluids;

@Mixin({LivingEntity.class})
public abstract class LivingEntityInjector extends Entity {

    public LivingEntityInjector(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
@PlatformOnly(PlatformOnly.FABRIC)
    @Redirect(method = ("baseTick"), at = @At(value = "INVOKE", target = ("Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z")))
    public boolean injectBaseTick(LivingEntity entity,TagKey<Fluid> tagKey){
        return entity.isEyeInFluid(FluidTags.WATER) || entity.isEyeInFluid(Fluids.PETROLEUM);
    }
}
