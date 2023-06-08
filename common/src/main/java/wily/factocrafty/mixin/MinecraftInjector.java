package wily.factocrafty.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factocrafty.item.ElectricArmorItem;

@Mixin(Minecraft.class)
public class MinecraftInjector {
    @Shadow @Nullable public Entity cameraEntity;

    @Shadow @Final public GameRenderer gameRenderer;
    boolean insecure = true;

    @Inject(at = @At("HEAD"), method = "getCameraEntity")
    private void init(CallbackInfoReturnable<Entity> info) {

    }
}