package wily.factocrafty.mixin;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.GameRendererM;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;

@Mixin(GameRenderer.class)
public abstract class GameRendererInjector implements GameRendererM {


    @Shadow  abstract void loadEffect(ResourceLocation resourceLocation);

    @Shadow @Nullable private PostChain postEffect;

    @Inject(at= @At("TAIL"), method = ("checkEntityPostEffect"))
    private void checkEntityPostEffect(Entity entity, CallbackInfo info){
        if (entity instanceof Player p) {
            ItemStack s = p.getItemBySlot(EquipmentSlot.HEAD);
            loadArmorEffects(p,s, false);
        }
    }
    public void loadArmorEffects(Player p, ItemStack s, boolean post){
        if (post) {
            if (this.postEffect != null) {
                this.postEffect.close();
            }

            this.postEffect = null;
        }
        if (!p.isSpectator() && s.getItem() instanceof ElectricArmorItem i && i.hasActiveFeature(ArmorFeatures.NIGHT_VISION,s, true))
            loadEffect(new ResourceLocation(Factocrafty.MOD_ID,"shaders/post/green.json"));
    }

}
