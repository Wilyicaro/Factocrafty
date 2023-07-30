package wily.factocrafty.mixin;

import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.GameRendererM;
import wily.factocrafty.tag.Fluids;

@Mixin(LivingEntity.class)
public abstract class ClientLivingEntityInjector extends Entity {

    public ClientLivingEntityInjector(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    private ItemStack lastHeadItemStack;

    GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
    @Inject(method = ("baseTick"), at = @At(value = "HEAD"))
    private void tick(CallbackInfo info) {
        if ((Object) this instanceof LocalPlayer p && (lastHeadItemStack== null || lastHeadItemStack != p.getItemBySlot(EquipmentSlot.HEAD))){
            gameRenderer.checkEntityPostEffect(p);
            lastHeadItemStack = p.getItemBySlot(EquipmentSlot.HEAD);
        }
    }

}
