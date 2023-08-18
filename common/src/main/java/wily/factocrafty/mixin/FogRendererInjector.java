package wily.factocrafty.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.fluid.FactocraftyFlowingFluid;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;

@Mixin(FogRenderer.class)
public class FogRendererInjector {


    @Shadow private static float fogRed;

    @Shadow private static float fogGreen;

    @Shadow private static float fogBlue;

    @Shadow private static long biomeChangedTime;

    @Inject(at= @At("HEAD"), method = ("setupColor"), cancellable = true)
    private static void returnFogType(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfo info){
        if (camera.isInitialized() ) {
            if (camera.getFluidInCamera() == null) {
                FluidState fluidState = clientLevel.getFluidState(camera.getBlockPosition());
                if (camera.getBlockPosition().getY() < (double) ((float) camera.getBlockPosition().getY() + fluidState.getHeight(clientLevel, camera.getBlockPosition()))) {
                    if (fluidState.getType() instanceof FactocraftyFlowingFluid fluid) {
                        int color = FluidStackHooks.getColor(fluid);
                        fogRed = ((color & 0xFF0000) >> 16) / 255F;
                        fogGreen = ((color & 0xFF00) >> 8) / 255F;
                        fogBlue = (color & 0xFF) / 255F;
                        biomeChangedTime = -1L;
                        info.cancel();
                    }
                }
            } else if (camera.getFluidInCamera().equals(FogType.NONE)){
                if (camera.getEntity() instanceof LivingEntity livingEntity2) {
                    // e.getMaterial() == FactocraftyArmorMaterials.GRAPHANO ||
                    ItemStack s = livingEntity2.getItemBySlot(EquipmentSlot.HEAD);
                    if (!livingEntity2.hasEffect(MobEffects.DARKNESS) && s.getItem() instanceof ElectricArmorItem e && e.hasActiveFeature(ArmorFeatures.NIGHT_VISION,s,true)) {
                        fogRed = 0.0F;
                        fogGreen =0.65F;
                        fogBlue = 0.2F;
                        biomeChangedTime = -1L;
                        info.cancel();
                    }
                }
            }
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
        }
    @Inject(at= @At("HEAD"), method = ("setupFog"), cancellable = true)
    private static void returnFogType(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo info){
        FogRenderer.FogData fogData = new FogRenderer.FogData(fogMode);
        Entity entity = camera.getEntity();
        if (camera.isInitialized() && camera.getFluidInCamera() == null){
                FluidState fluidState = entity.level().getFluidState(camera.getBlockPosition());
                    if (fluidState.getType() instanceof FactocraftyFlowingFluid fluid && fluid.isValidToGetFog(fluidState) && camera.getBlockPosition().getY() < (double) ((float) camera.getBlockPosition().getY() + fluidState.getHeight(entity.level(), camera.getBlockPosition()))) {
                        if (entity.isSpectator()) {
                            fogData.start = -8.0f;
                            fogData.end = f * 0.5f;
                        }else {
                            fogData.start = 0.25F;
                            fogData.end = 1.0F;
                        }
                        fogData.shape = FogShape.CYLINDER;
                        RenderSystem.setShaderFogStart(fogData.start);
                        RenderSystem.setShaderFogEnd(fogData.end);
                        RenderSystem.setShaderFogShape(fogData.shape);
                        info.cancel();
                    }

            }

    }

}
