package wily.factocrafty.fabric;


import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import wily.factocrafty.fabriclike.FactocraftyFabricLike;


public class FactocraftyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactocraftyFabricLike.init();}
}
