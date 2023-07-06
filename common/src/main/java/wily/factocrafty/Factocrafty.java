package wily.factocrafty;

import com.google.common.base.Suppliers;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.RegistrarManager;
import net.fabricmc.api.EnvType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.events.ModEvents;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.FluidCellItem;
import wily.factocrafty.network.*;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.ICraftyEnergyItem;

import java.util.Iterator;
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
        NETWORK.register(FactocraftySyncInputTypePacket.class, FactocraftySyncInputTypePacket::encode, FactocraftySyncInputTypePacket::new, FactocraftySyncInputTypePacket::apply);
        NETWORK.register(FactocraftyJetpackLaunchPacket.class, FactocraftyJetpackLaunchPacket::encode, FactocraftyJetpackLaunchPacket::new, FactocraftyJetpackLaunchPacket::apply);
        NETWORK.register(FactocraftySyncSelectedUpgradePacket.class, FactocraftySyncSelectedUpgradePacket::encode, FactocraftySyncSelectedUpgradePacket::new, FactocraftySyncSelectedUpgradePacket::apply);
        NETWORK.register(FactocraftySyncIntegerBearerPacket.class, FactocraftySyncIntegerBearerPacket::encode, FactocraftySyncIntegerBearerPacket::new, FactocraftySyncIntegerBearerPacket::apply);
        NETWORK.register(FactocraftyArmorFeaturePacket.class, FactocraftyArmorFeaturePacket::encode, FactocraftyArmorFeaturePacket::new, FactocraftyArmorFeaturePacket::apply);

    }
}
