package wily.factocrafty.mixin;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;
import wily.factocrafty.tag.Fluids;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerInjector {


    @Debug(print = true)
    @ModifyVariable(method = ("updateAutoJump"), at=@At(value = "STORE",ordinal = 1), ordinal = 5)
    private float doCustomFluidPushing(float f, float g, float p){
        if ((Object)this instanceof Player player) {
            ItemStack s = player.getItemBySlot(EquipmentSlot.FEET);
            if (s.getItem() instanceof ElectricArmorItem e && e.hasActiveFeature(ArmorFeatures.SUPER_JUMP,s,true)) return p+0.4F;
        }
        return p;
    }
}
