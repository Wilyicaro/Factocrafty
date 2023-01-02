package wily.factocrafty.quilt;


import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import wily.factocrafty.fabriclike.FactocraftyFabricLike;
import wily.factocrafty.fabriclike.FactocraftyFabricLikeClient;

public class FactocraftyQuiltClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        FactocraftyFabricLikeClient.init();
    }
}
