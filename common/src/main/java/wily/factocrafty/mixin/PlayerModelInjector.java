package wily.factocrafty.mixin;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.client.renderer.entity.HangGliderLayer;
import wily.factocrafty.init.Registration;

@Mixin(HumanoidModel.class)
public abstract class PlayerModelInjector {


        @Shadow public HumanoidModel.ArmPose rightArmPose;

        @Shadow public HumanoidModel.ArmPose leftArmPose;

        @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart leftArm;

    @Inject(method = ("setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"), at = @At("TAIL"))
    private void init(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo info){
        if (livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(Registration.BASIC_HANG_GLIDER.get())) {
            if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS){
                this.rightArm.xRot = livingEntity.isFallFlying() ? Mth.clamp(this.head.xRot - 1.9198622F - (livingEntity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.0F) : -1.05F;
                AnimationUtils.bobModelPart(this.rightArm, h + 0.2F, 1.0F);
            }

            if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS){
                this.leftArm.xRot = livingEntity.isFallFlying() ? Mth.clamp(this.head.xRot - 1.9198622F - (livingEntity.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.0F) : -1.05F;
                AnimationUtils.bobModelPart(this.leftArm,h + 0.2F, -1.0F);
            }
        }
    }

}
