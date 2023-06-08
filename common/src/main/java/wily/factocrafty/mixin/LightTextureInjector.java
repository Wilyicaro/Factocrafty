package wily.factocrafty.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;

@Mixin(LightTexture.class)
public class LightTextureInjector {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = ("updateLightTexture"), at = @At("STORE"), ordinal = 9)
    private float updateLightTexture(float m, float f){
        ItemStack s =minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
        return s.getItem() instanceof ElectricArmorItem i && i.hasActiveFeature(ArmorFeatures.NIGHT_VISION,s,true) ? 1.0F :m;
    }
}
