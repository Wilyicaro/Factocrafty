package wily.factocrafty.quilt;


import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import wily.factocrafty.fabriclike.FactocraftyFabricLike;

public class FactocraftyQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        FactocraftyFabricLike.init();
    }
}
