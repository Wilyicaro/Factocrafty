package wily.factocrafty.fabric;

import dev.architectury.hooks.level.biome.SpawnProperties;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IFluidItem;
import wily.factoryapi.base.Storages;
import wily.factoryapi.fabriclike.base.FabricItemFluidStorage;

public class FactocraftyFabricLike {

    public static void init() {
        Factocrafty.init();
        FluidStorage.SIDED.registerForBlockEntity((be, direction) -> (SingleVariantStorage<FluidVariant>) be.fluidTank, Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get());
        FluidStorage.ITEM.registerForItems((e,a)-> e.getItem() instanceof IFluidItem<?> item ? new FabricItemFluidStorage(a, item.getFluidStorageBuilder()) : null,Registration.FLUID_CELL.get(),Registration.FLEX_JETPACK.get());
        ItemStorage.SIDED.registerForBlockEntities((be,direction) ->
        {
            if (be instanceof IFactoryStorage s)
                return (Storage<ItemVariant>) s.getStorage(Storages.ITEM,direction).get().getHandler();
            return null;
        }, Registration.MACERATOR_BLOCK_ENTITY.get(),Registration.ELECTRIC_FURNACE_BLOCK_ENTITY.get(),Registration.GENERATOR_BLOCK_ENTITY.get(), Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get()
        );

    }
}
