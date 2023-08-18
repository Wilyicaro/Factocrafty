package wily.factocrafty.fabric;

import net.fabricmc.api.ModInitializer;
import wily.factocrafty.Factocrafty;

public class FactocraftyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Factocrafty.init();

    }
}
