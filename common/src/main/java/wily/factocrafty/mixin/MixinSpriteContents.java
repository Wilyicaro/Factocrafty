package wily.factocrafty.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.client.renderer.SpriteAnimatedContents;

@Mixin(SpriteContents.class)
public class MixinSpriteContents implements SpriteAnimatedContents {
    public AnimationMetadataSection animationMetadataSection;
    @Inject(at= @At("TAIL"), method = ("<init>"))
    public void returnFogType(ResourceLocation resourceLocation, FrameSize frameSize, NativeImage nativeImage, AnimationMetadataSection animationMetadataSection, CallbackInfo info){
        this.animationMetadataSection = animationMetadataSection;
    }

    @Override
    public AnimationMetadataSection getAnimatedMetadataSection() {
        return animationMetadataSection;
    }
}
