package wily.factocrafty.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.tag.Fluids;

@Mixin(FogRenderer.class)
public class FogRendererInjector {


    @Shadow private static float fogRed;

    @Shadow private static float fogGreen;

    @Shadow private static float fogBlue;

    @Shadow private static long biomeChangedTime;

    @Inject(at= @At("HEAD"), method = ("setupColor"), cancellable = true)
    private static void returnFogType(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfo info){
        if (camera.isInitialized() && camera.getFluidInCamera() == null){
            FluidState fluidState = clientLevel.getFluidState(camera.getBlockPosition());
            if (camera.getBlockPosition().getY() < (double)((float)camera.getBlockPosition().getY() + fluidState.getHeight(clientLevel, camera.getBlockPosition()))) {
                if (fluidState.is(Fluids.PETROLEUM)) {
                    fogRed = 0.1F;
                    fogGreen = 0.1F;
                    fogBlue = 0.1F;
                    biomeChangedTime = -1L;
                }else if (fluidState.is(Fluids.LATEX)){
                    fogRed = 0.85F;
                    fogGreen = 0.85F;
                    fogBlue = 0.85F;
                    biomeChangedTime = -1L;
                }


                RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
            }
            info.cancel();

        }
        }
    @Inject(at= @At("HEAD"), method = ("setupFog"), cancellable = true)
    private static void returnFogType(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo info){
        FogRenderer.FogData fogData = new FogRenderer.FogData(fogMode);
        if (camera.isInitialized() && camera.getFluidInCamera() == null){
            if (camera.getEntity() instanceof LocalPlayer player) {
                FluidState fluidState = player.level.getFluidState(camera.getBlockPosition());
                    if (fluidState.is(Fluids.PETROLEUM) ||  fluidState.is(Fluids.LATEX) && camera.getBlockPosition().getY() < (double) ((float) camera.getBlockPosition().getY() + fluidState.getHeight(player.clientLevel, camera.getBlockPosition()))) {
                        fogData.start = 0.25F;
                        fogData.end = 1.0F;
                        fogData.shape = FogShape.CYLINDER;
                        RenderSystem.setShaderFogStart(fogData.start);
                        RenderSystem.setShaderFogEnd(fogData.end);
                        RenderSystem.setShaderFogShape(fogData.shape);
                    }
            }
            info.cancel();

        }
    }

}
