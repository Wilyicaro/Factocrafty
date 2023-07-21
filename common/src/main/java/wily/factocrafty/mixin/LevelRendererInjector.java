package wily.factocrafty.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererInjector {

    @ModifyVariable(method = ("renderClouds"), at = @At("STORE"), ordinal = 4)
    private double renderClouds(double l, PoseStack poseStack, Matrix4f matrix4f, float f, double d, double e, double g) {
        return f * 0.03f;
    }
}
