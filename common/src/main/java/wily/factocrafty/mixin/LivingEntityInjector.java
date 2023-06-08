package wily.factocrafty.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;
import wily.factocrafty.item.FactocraftyArmorMaterials;
import wily.factocrafty.tag.Fluids;

@Mixin({LivingEntity.class})
public abstract class LivingEntityInjector extends Entity {

    public LivingEntityInjector(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    @Inject(method = ("checkFallDamage"), at = @At(value = "TAIL"))
    private void injectFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo info) {
        for (ItemStack i : getArmorSlots()) if (i.getItem() instanceof ArmorItem item && item.getMaterial().equals(FactocraftyArmorMaterials.RUBBER) && item.getEquipmentSlot() == EquipmentSlot.FEET) fallDistance /= 1.4;
    }
    @Inject(method = ("getJumpBoostPower"), at = @At(value = "RETURN"), cancellable = true)
    private void injectJumpBoost(CallbackInfoReturnable<Double> info) {
        if ((Object)this instanceof Player p) {
            ItemStack s = p.getItemBySlot(EquipmentSlot.FEET);
            if (s.getItem() instanceof ElectricArmorItem e && e.hasActiveFeature(ArmorFeatures.SUPER_JUMP,s,true)) info.setReturnValue(0.4+ info.getReturnValue());
        }
    }
}
