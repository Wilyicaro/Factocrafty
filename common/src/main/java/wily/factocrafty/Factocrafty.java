package wily.factocrafty;

import com.google.common.base.Suppliers;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
    // Registering a new creative tab
    public static final CreativeTabRegistry.TabSupplier FACTOCRAFTY_TAB = CreativeTabRegistry.create(new ResourceLocation( MOD_ID,"tab"), (b)-> b.icon(() -> new ItemStack(Registration.GENERATOR.get())).displayItems(
            ((featureFlagSet, output,bool) -> {
                Iterator var2 = BuiltInRegistries.ITEM.iterator();

                while(var2.hasNext()) {

                    Item item = (Item)var2.next();
                    if (item.arch$registryName().getNamespace().equals(MOD_ID))
                        output.accept(item);
                    if (item instanceof ICraftyEnergyItem<?> energyItem){
                        ItemStack charged = new ItemStack(item);
                        energyItem.getCraftyEnergy(charged).receiveEnergy(energyItem.getCraftyEnergy(charged).getMaxEnergyStored(), false);
                        output.accept(charged);
                    } else if (item instanceof FluidCellItem cell) {
                        Registration.FLUIDS.getRegistrar().forEach((f)->{
                                if (f.isSource(null)) {
                                    ItemContainerUtil.ItemFluidContext context = ItemContainerUtil.fillItem(new ItemStack(cell), FluidStack.create(f, FluidStack.bucketAmount()));
                                    output.accept(context.container());
                                }
                                }
                        );
                    }
                }
            })));


    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "main"));


    public static void init() {
        Registration.registerObjects();
        ModEvents.init();
        NETWORK.register(FactocraftySyncFluidPacket.class,FactocraftySyncFluidPacket::encode,FactocraftySyncFluidPacket::new, FactocraftySyncFluidPacket::apply);
        NETWORK.register(FactocraftySyncEnergyPacket.class,FactocraftySyncEnergyPacket::encode,FactocraftySyncEnergyPacket::new, FactocraftySyncEnergyPacket::apply);
        NETWORK.register(FactocraftySyncProgressPacket.class,FactocraftySyncProgressPacket::encode,FactocraftySyncProgressPacket::new, FactocraftySyncProgressPacket::apply);
        NETWORK.register(FactocraftyStateButtonPacket.class,FactocraftyStateButtonPacket::encode,FactocraftyStateButtonPacket::new, FactocraftyStateButtonPacket::apply);
        NETWORK.register(FactocraftySyncRefiningTypePacket.class, FactocraftySyncRefiningTypePacket::encode, FactocraftySyncRefiningTypePacket::new, FactocraftySyncRefiningTypePacket::apply);
        NETWORK.register(FactocraftyJetpackUsePacket.class, FactocraftyJetpackUsePacket::encode, FactocraftyJetpackUsePacket::new, FactocraftyJetpackUsePacket::apply);
        System.out.println(FactocraftyExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
