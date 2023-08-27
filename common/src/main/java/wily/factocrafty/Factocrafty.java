package wily.factocrafty;

import com.google.common.base.Suppliers;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.events.ModEvents;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.*;

import java.util.function.Supplier;
import java.util.logging.Logger;


public class Factocrafty {
    public static final String MOD_ID = "factocrafty";
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "main"));


    public static void init() {
        Registration.registerObjects();
        ModEvents.init();
        NETWORK.register(FactocraftySyncFluidPacket.class,FactocraftySyncFluidPacket::encode,FactocraftySyncFluidPacket::new, FactocraftySyncFluidPacket::apply);
        NETWORK.register(FactocraftySyncEnergyPacket.class,FactocraftySyncEnergyPacket::encode,FactocraftySyncEnergyPacket::new, FactocraftySyncEnergyPacket::apply);
        NETWORK.register(FactocraftySyncProgressPacket.class,FactocraftySyncProgressPacket::encode,FactocraftySyncProgressPacket::new, FactocraftySyncProgressPacket::apply);
        NETWORK.register(FactocraftyStateButtonPacket.class,FactocraftyStateButtonPacket::encode,FactocraftyStateButtonPacket::new, FactocraftyStateButtonPacket::apply);
        NETWORK.register(FactocraftyJetpackLaunchPacket.class, FactocraftyJetpackLaunchPacket::encode, FactocraftyJetpackLaunchPacket::new, FactocraftyJetpackLaunchPacket::apply);
        NETWORK.register(FactocraftySyncUpgradeStorage.class, FactocraftySyncUpgradeStorage::encode, FactocraftySyncUpgradeStorage::new, FactocraftySyncUpgradeStorage::apply);
        NETWORK.register(FactocraftySyncIntegerBearerPacket.class, FactocraftySyncIntegerBearerPacket::encode, FactocraftySyncIntegerBearerPacket::new, FactocraftySyncIntegerBearerPacket::apply);
        NETWORK.register(FactocraftyArmorFeaturePacket.class, FactocraftyArmorFeaturePacket::encode, FactocraftyArmorFeaturePacket::new, FactocraftyArmorFeaturePacket::apply);

    }
}
