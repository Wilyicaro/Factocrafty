package wily.factocrafty.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import wily.factocrafty.Factocrafty;

public class FactocraftyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Factocrafty.init();

    }
}
