package wily.factocrafty.fabric;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.mixin.item.ArmorItemMixin;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ArmorItem;
import wily.factocrafty.fabriclike.FactocraftyFabricLike;


public class FactocraftyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactocraftyFabricLike.init();}
}
