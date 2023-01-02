package wily.factocrafty.fabric;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import wily.factocrafty.fabriclike.FactocraftyFabricLike;
import wily.factocrafty.fabriclike.FactocraftyFabricLikeClient;


public class FactocraftyFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FactocraftyFabricLikeClient.init();}
}
