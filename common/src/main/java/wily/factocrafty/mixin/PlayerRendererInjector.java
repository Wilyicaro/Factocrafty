package wily.factocrafty.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.FactocraftyClient;
import wily.factocrafty.client.renderer.entity.HangGliderLayer;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererInjector extends LivingEntityRenderer<AbstractClientPlayer, EntityModel<AbstractClientPlayer>> {


    public PlayerRendererInjector(EntityRendererProvider.Context arg, EntityModel<AbstractClientPlayer> arg2, float f) {
        super(arg, arg2, f);
    }

    @Inject(method = ("<init>"), at = @At("TAIL"))
    private void init(EntityRendererProvider.Context arg, boolean bl, CallbackInfo info){
        if (FactocraftyClient.isHangGliderModelLayerLoaded)addLayer(new HangGliderLayer<>(this,arg.getModelSet()));
    }

}
