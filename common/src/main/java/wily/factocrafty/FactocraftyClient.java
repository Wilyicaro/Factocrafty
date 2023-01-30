package wily.factocrafty;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import wily.factocrafty.block.FactocraftyWoodType;
import wily.factocrafty.block.cable.CableTiers;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.client.renderer.block.FactocraftyFluidTankRenderer;
import wily.factocrafty.client.renderer.block.TreeTapRenderer;
import wily.factocrafty.client.renderer.block.RubberSignRenderer;
import wily.factocrafty.client.renderer.entity.*;
import wily.factocrafty.client.screens.*;
import wily.factocrafty.entity.IFactocraftyBoat;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.BatteryItem;
import wily.factocrafty.item.FluidCellItem;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factocrafty.util.registering.FactocraftyBlocks;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;
import wily.factocrafty.util.registering.FactocraftyFluids;

import java.util.function.Function;
import java.util.function.Supplier;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class FactocraftyClient {

    public static void LeavesColor() {

        BlockColor blockColors = (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null ? BiomeColors.getAverageFoliageColor(blockAndTintGetter, blockPos) : FoliageColor.getEvergreenColor();
        ItemColor itemColors = (a, e) -> FoliageColor.getEvergreenColor();
        ColorHandlerRegistry.registerBlockColors(blockColors, Registration.RUBBER_LEAVES.get());
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.RUBBER_LEAVES.get().asItem());

    }

    public static void fluidItemBlocksColor() {

        ItemColor itemColors = (a, e) -> (e != 0) ? FluidStackHooks.getColor(((FluidCellItem) a.getItem()).getFluidStorage(a).getFluidStack()) : 0xFFFFFF;
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.FLUID_CELL.get());

    }
    public static void dyeItemsColor() {

        ItemColor itemColors = (a, e) -> (e != 0 && a.getItem() instanceof DyeableLeatherItem item ) ? item.getColor(a) : 0xFDFDFD;
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.BASIC_HANG_GLIDER);

    }
    public static final KeyMapping GRAVITY_KEYMAPPING = new KeyMapping(
            "key." + MOD_ID + ".g", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_G, // The default keycode
            "category." + MOD_ID + ".gravity" // The category translation key used to categorize in the Controls screen
    );

    public interface FactocraftyEntityRendererRegistry {
         <T extends Entity> void register(Supplier<? extends EntityType<? extends T>> type, EntityRendererProvider<T> provider);
    }

    public interface FactocraftyModelLayerRegistry {
        void register(ModelLayerLocation location, Supplier<LayerDefinition> definition);
    }
    public interface FactocraftyRenderLayerRegistry {
        <T extends LivingEntity, M extends EntityModel<T>>void register(LivingEntityRenderer<T,  M> renderer, RenderLayer< T, M> renderLayer);
    }

    public static void registerEntityRenderers(FactocraftyEntityRendererRegistry event){

        event.register(Registration.CORRUPTED_ENDERMAN, EndermanRenderer::new);
        event.register(Registration.FACTOCRAFTY_BOAT, (a)-> new FactocraftyBoatRenderer(a,false));
        event.register(Registration.FACTOCRAFTY_CHEST_BOAT, (a)-> new FactocraftyBoatRenderer(a,true));
        event.register(Registration.LASER,LaserRenderer::new);
    }
    public static void registerEntityModelLayers(FactocraftyModelLayerRegistry event){
        for (IFactocraftyBoat.Type r : IFactocraftyBoat.Type.values()) {
            event.register(FactocraftyBoatRenderer.getFactocraftyBoatLayer(r, false), BoatModel::createBodyModel);
            event.register(FactocraftyBoatRenderer.getFactocraftyBoatLayer(r, true), ChestBoatModel::createBodyModel);
        }
        event.register(LaserModel.LAYER_LOCATION, LaserModel::createBodyLayer);
        event.register(JetpackModel.LAYER_LOCATION, JetpackModel::createBodyLayer);
        event.register(HangGliderModel.LAYER_LOCATION, HangGliderModel::createBodyLayer);
    }
    public static void registerEntityRenderLayers(Function<EntityType<? extends LivingEntity>, EntityRenderer<?>> function, EntityModelSet entityModelSet, FactocraftyRenderLayerRegistry event) {
        if (function.apply(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer r)
            event.register(r ,new HangGliderLayer<>(r, entityModelSet));
    }
    public static void init(){
        Factocrafty.LOGGER.info("Initializing Client Side...");
        KeyMappingRegistry.register(GRAVITY_KEYMAPPING);
        FactocraftyWoodType.addWoodType(FactocraftyWoodType.RUBBER);
        for (Block a : Registration.BLOCKS.getRegistrar())
            if (a instanceof InsulatedCableBlock cable && cable.asItem() != null) {
                FactocraftyExpectPlatform.registerModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + BuiltInRegistries.BLOCK.getKey(a).getPath() + "_in_hand"),"inventory"));
            }
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation( new ResourceLocation( "factocrafty:fluid_tank"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:fluid_model"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_bowl"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_latex"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_latex_fall"),""));
        RenderTypeRegistry.register(RenderType.translucent(), FactocraftyFluidTanks.BASIC_FLUID_TANK.get());
        RenderTypeRegistry.register(RenderType.translucent(), FactocraftyFluids.COOLANT.get(),FactocraftyFluids.FLOWING_COOLANT.get(),FactocraftyFluids.GASOLINE.get(),FactocraftyFluids.FLOWING_GASOLINE.get());
        RenderTypeRegistry.register(RenderType.cutoutMipped(), Registration.RUBBER_TREE_SAPLING.get(), Registration.STRIPPED_RUBBER_LOG.get(), Registration.RUBBER_DOOR.get(), Registration.RUBBER_TRAPDOOR.get(), Registration.GENERATOR.get(), FactocraftyBlocks.GEOTHERMAL_GENERATOR.get(), CableTiers.CRYSTAL.getBlock());
        BlockEntityRendererRegistry.register(Registration.RUBBER_SIGN_BLOCK_ENTITY.get(), RubberSignRenderer::new);
        BlockEntityRendererRegistry.register(Registration.TREETAP_BLOCK_ENTITY.get(), TreeTapRenderer::new);

        for (FactocraftyFluidTanks tank : FactocraftyFluidTanks.values())
            BlockEntityRendererRegistry.register(FactocraftyBlockEntities.ofBlock(tank.get()), FactocraftyFluidTankRenderer::new);
        MenuRegistry.registerScreenFactory(Registration.GENERATOR_MENU.get(), GeneratorScreen::new);
        MenuRegistry.registerScreenFactory(Registration.GEOTHERMAL_GENERATOR_MENU.get(), GeothermalGeneratorScreen::new);
        MenuRegistry.registerScreenFactory(Registration.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
        MenuRegistry.registerScreenFactory(Registration.MACERATOR_MENU.get(), BasicMachineScreen::new);
        MenuRegistry.registerScreenFactory(Registration.ENERGY_CELL_MENU.get(), EnergyCellScreen::new);
        MenuRegistry.registerScreenFactory(Registration.FLUID_TANK_MENU.get(), FluidTankScreen::new);
        MenuRegistry.registerScreenFactory(Registration.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        MenuRegistry.registerScreenFactory(Registration.COMPRESSOR_MENU.get(), BasicMachineScreen::create);
        MenuRegistry.registerScreenFactory(Registration.EXTRACTOR_MENU.get(), BasicMachineScreen::new);
        MenuRegistry.registerScreenFactory(Registration.REFINER_MENU.get(), RefinerScreen::new);


        //ClientGuiEvent.DEBUG_TEXT_LEFT.register((e)-> e.);
        fluidItemBlocksColor();
        dyeItemsColor();
        LeavesColor();
        for (ItemLike item : Registration.ITEMS.getRegistrar())
            if (item instanceof BatteryItem)
                ItemPropertiesRegistry.register(item, new ResourceLocation(MOD_ID, "charge_ratio"), (itemStack, clientLevel, livingEntity, i) -> {
                    if (itemStack.getItem() instanceof BatteryItem battery) return battery.getChargedLevel(itemStack);
                    return 0;
                });
    }
}
