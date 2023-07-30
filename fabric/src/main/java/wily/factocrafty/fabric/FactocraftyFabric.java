package wily.factocrafty.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.ArrayUtils;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IFluidItem;
import wily.factoryapi.base.Storages;
import wily.factoryapi.fabric.base.FabricItemFluidStorage;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FactocraftyFabric implements ModInitializer {


    @Override
    public void onInitialize() {
        Factocrafty.init();

        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> (SingleVariantStorage<FluidVariant>) be.fluidTank, Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get());
        ItemLike[] items = new ItemLike[]{Registration.FLUID_CELL.get(),Registration.FLEX_JETPACK.get()};
        for (FactocraftyFluidTanks value : FactocraftyFluidTanks.BASIC_FLUID_TANK.values()) items = ArrayUtils.add(items, value.get().asItem());
        FluidStorage.ITEM.registerForItems((e,a)-> e.getItem() instanceof IFluidItem<?> item ? new FabricItemFluidStorage(a, item.getFluidStorageBuilder()) : null, items);
        ItemStorage.SIDED.registerForBlockEntities((be,direction) ->
                {
                    if (be instanceof IFactoryStorage s)
                        return (Storage<ItemVariant>) s.getStorage(Storages.ITEM,direction).get().getHandler();
                    return null;
                }, Registration.REFINER_BLOCK_ENTITY.get(),Registration.ENRICHER_BLOCK_ENTITY.get(),Registration.EXTRACTOR_BLOCK_ENTITY.get(),Registration.COMPRESSOR_BLOCK_ENTITY.get(),Registration.MACERATOR_BLOCK_ENTITY.get(),Registration.ELECTRIC_FURNACE_BLOCK_ENTITY.get(),Registration.GENERATOR_BLOCK_ENTITY.get(), Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get()
        );

    }
}
