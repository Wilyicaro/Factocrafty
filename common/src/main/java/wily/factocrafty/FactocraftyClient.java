package wily.factocrafty;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.Gui;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.block.FactocraftyWoodType;
import wily.factocrafty.block.cable.CableTiers;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.block.entity.FactocraftyLedBlockEntity;
import wily.factocrafty.client.renderer.block.FactocraftyLiquidTankRenderer;
import wily.factocrafty.client.renderer.block.RubberHangingSignRenderer;
import wily.factocrafty.client.renderer.block.TreeTapRenderer;
import wily.factocrafty.client.renderer.block.RubberSignRenderer;
import wily.factocrafty.client.renderer.entity.*;
import wily.factocrafty.client.screens.*;
import wily.factocrafty.entity.IFactocraftyBoat;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.*;
import wily.factocrafty.network.FactocraftyArmorFeaturePacket;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factocrafty.util.registering.FactocraftyBlocks;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;
import wily.factocrafty.util.registering.FactocraftyFluids;
import wily.factoryapi.ItemContainerUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class FactocraftyClient {
    public static boolean isHangGliderModelLayerLoaded;
    private static final ResourceLocation NIGHT_VISION_LOCATION = new ResourceLocation(Factocrafty.MOD_ID,"textures/misc/nightvision.png");

    public static void leavesColor() {

        BlockColor blockColors = (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null ? BiomeColors.getAverageFoliageColor(blockAndTintGetter, blockPos) : FoliageColor.getEvergreenColor();
        ItemColor itemColors = (a, e) -> FoliageColor.getEvergreenColor();
        ColorHandlerRegistry.registerBlockColors(blockColors, Registration.RUBBER_LEAVES.get());
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.RUBBER_LEAVES.get().asItem());


    }
    public static void ledColor() {
        BlockColor blockColors = (blockState, blockAndTintGetter, blockPos, i) -> blockAndTintGetter != null && blockPos != null && blockAndTintGetter.getBlockEntity(blockPos) instanceof FactocraftyLedBlockEntity be && blockState.getValue(FactocraftyLedBlock.LIGHT_VALUE) > 0 && i == 0 ? be.actualRgb.get() : 0xFFFFFF;
        ColorHandlerRegistry.registerBlockColors(blockColors, Registration.RGB_LED_BLOCK.get());
        ColorHandlerRegistry.registerBlockColors(blockColors, Registration.RGB_LED_PANEL.get());

    }
    public static boolean clientPlayerHasNightGoogles() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.player != null && minecraft.player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ElectricArmorItem e && e.hasActiveFeature(ArmorFeatures.NIGHT_VISION,minecraft.player.getItemBySlot(EquipmentSlot.HEAD), true);

    }

    public static void fluidItemBlocksColor() {

        ItemColor itemColors = (a, e) -> (e != 0) ? FluidStackHooks.getColor((ItemContainerUtil.getFluid(a))) : 0xFFFFFF;
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.FLUID_CELL.get(),FactocraftyFluids.NAPHTHA.get().getBucket(),FactocraftyFluids.GASOLINE.get().getBucket(),FactocraftyFluids.COOLANT.get().getBucket());

    }
    public static void dyeItemsColor() {

        ItemColor itemColors = (a, e) -> (e != 0 && a.getItem() instanceof DyeableLeatherItem item ) ? item.getColor(a) : 0xFDFDFD;
        ColorHandlerRegistry.registerItemColors(itemColors, Registration.BASIC_HANG_GLIDER);

    }
    public static final KeyMapping GRAVITY_KEYMAPPING = new KeyMapping(
            "key." + MOD_ID + ".g", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM, // This key mapping is for Keyboards by default
            InputConstants.KEY_G, // The default keycode
            "category." + MOD_ID + ".keyboard.armor_features"  // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping VISION_KEYMAPPING = new KeyMapping(
            "key." + MOD_ID + ".v", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
            InputConstants.KEY_V, // The default keycode
            "category." + MOD_ID + ".keyboard.armor_features"  // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping LEGGINGS_KEYMAPPING = new KeyMapping(
            "key." + MOD_ID + ".l", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
            InputConstants.KEY_L, // The default keycode
            "category." + MOD_ID + ".keyboard.armor_features"  // The category translation key used to categorize in the Controls screen
    );
    public static final KeyMapping BOOTS_KEYMAPPING = new KeyMapping(
            "key." + MOD_ID + ".b", // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
            InputConstants.KEY_B, // The default keycode
            "category." + MOD_ID + ".keyboard.armor_features" // The category translation key used to categorize in the Controls screen
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

        event.register(Registration.CORRUPTED_ENDERMAN, (c)-> new EndermanRenderer(c){
            public ResourceLocation getTextureLocation(EnderMan enderMan) {
                return Registration.getModResource("textures/entity/enderman/corrupted_enderman.png");
            }
        });
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
        isHangGliderModelLayerLoaded = true;
    }
    public static void registerEntityRenderLayers(Function<EntityType<? extends LivingEntity>, EntityRenderer<?>> function, EntityModelSet entityModelSet, FactocraftyRenderLayerRegistry event) {
        if (function.apply(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer r)
            event.register(r ,new HangGliderLayer<>(r, entityModelSet));
    }
    public static void init(){
        Factocrafty.LOGGER.info("Initializing Client Side...");
        KeyMappingRegistry.register(GRAVITY_KEYMAPPING);
        KeyMappingRegistry.register(VISION_KEYMAPPING);
        KeyMappingRegistry.register(LEGGINGS_KEYMAPPING);
        KeyMappingRegistry.register(BOOTS_KEYMAPPING);
    }
    public static void enqueueInit(){
        ClientGuiEvent.RENDER_HUD.register((p,i)->{
            Minecraft minecraft = Minecraft.getInstance();
            Gui gui = minecraft.gui;
            if (minecraft.options.getCameraType().isFirstPerson()) {
                if (!minecraft.player.isScoping()) {
                    gui.scopeScale =0.5F;
                    if (minecraft.player.getItemBySlot(EquipmentSlot.HEAD).is(Registration.NIGHT_VISION_GOGGLES.get())) {
                        if (Platform.isFabric())RenderSystem.enableBlend();
                        gui.renderTextureOverlay(p, NIGHT_VISION_LOCATION, 1.0F);
                    }
                }
            }
        });
        ClientTickEvent.CLIENT_LEVEL_POST.register(m->{
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                ItemStack h = minecraft.player.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack c = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
                ItemStack l = minecraft.player.getItemBySlot(EquipmentSlot.LEGS);
                ItemStack f = minecraft.player.getItemBySlot(EquipmentSlot.FEET);
                if (h.getItem() instanceof ElectricArmorItem e) {
                    if (VISION_KEYMAPPING.consumeClick() && e.hasFeature(ArmorFeatures.NIGHT_VISION, h, false)) Factocrafty.NETWORK.sendToServer(new FactocraftyArmorFeaturePacket(ArmorFeatures.NIGHT_VISION, EquipmentSlot.HEAD, !ArmorFeatures.NIGHT_VISION.isActive(h.getOrCreateTag())));
                }
                if (c.getItem() instanceof ElectricArmorItem e)
                    if (GRAVITY_KEYMAPPING.consumeClick() && e.hasFeature(ArmorFeatures.GRAVITY_0, c, false))
                        Factocrafty.NETWORK.sendToServer(new FactocraftyArmorFeaturePacket(ArmorFeatures.GRAVITY_0, EquipmentSlot.CHEST, !ArmorFeatures.GRAVITY_0.isActive(c.getOrCreateTag())));
                if (l.getItem() instanceof ElectricArmorItem e)
                    if (LEGGINGS_KEYMAPPING.consumeClick() && e.hasFeature(ArmorFeatures.SUPER_SPEED, l, false))
                        Factocrafty.NETWORK.sendToServer(new FactocraftyArmorFeaturePacket(ArmorFeatures.SUPER_SPEED, EquipmentSlot.LEGS, !ArmorFeatures.SUPER_SPEED.isActive(l.getOrCreateTag())));
                if (f.getItem() instanceof ElectricArmorItem e)
                    if (BOOTS_KEYMAPPING.consumeClick() && e.hasFeature(ArmorFeatures.SUPER_JUMP, f, false))
                        Factocrafty.NETWORK.sendToServer(new FactocraftyArmorFeaturePacket(ArmorFeatures.SUPER_JUMP, EquipmentSlot.FEET, !ArmorFeatures.SUPER_JUMP.isActive(f.getOrCreateTag())));
            }
        });
        FactocraftyWoodType.addWoodType(FactocraftyWoodType.RUBBER);
        for (Block a : Registration.BLOCKS.getRegistrar())
            if (a instanceof InsulatedCableBlock)
                FactocraftyExpectPlatform.registerModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + BuiltInRegistries.BLOCK.getKey(a).getPath() + "_in_hand"),"inventory"));


        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:fluid_model"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_bowl"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_latex"),""));
        FactocraftyExpectPlatform.registerModel(new ModelResourceLocation(new ResourceLocation("factocrafty:treetap_latex_fall"),""));
        RenderTypeRegistry.register(RenderType.translucent(), FactocraftyFluids.COOLANT.get(),FactocraftyFluids.FLOWING_COOLANT.get(),FactocraftyFluids.GASOLINE.get(),FactocraftyFluids.FLOWING_GASOLINE.get(),FactocraftyFluids.NAPHTHA.get(),FactocraftyFluids.FLOWING_NAPHTHA.get(),FactocraftyFluids.METHANE.get(),FactocraftyFluids.FLOWING_METHANE.get(),FactocraftyFluids.WATER_VAPOR.get(),FactocraftyFluids.FLOWING_WATER_VAPOR.get(),FactocraftyFluids.OXYGEN.get(),FactocraftyFluids.FLOWING_OXYGEN.get(),FactocraftyFluids.HYDROGEN.get(),FactocraftyFluids.FLOWING_HYDROGEN.get());
        RenderTypeRegistry.register(RenderType.cutoutMipped(), Registration.RGB_LED_BLOCK.get(),Registration.RGB_LED_PANEL.get(),Registration.REINFORCED_GLASS.get(), Registration.REINFORCED_GLASS_PANE.get(), Registration.RUBBER_TREE_SAPLING.get(), Registration.STRIPPED_RUBBER_LOG.get(), Registration.RUBBER_DOOR.get(), Registration.RUBBER_TRAPDOOR.get(), Registration.GENERATOR.get(), FactocraftyBlocks.GEOTHERMAL_GENERATOR.get(), CableTiers.CRYSTAL.getBlock());
        BlockEntityRendererRegistry.register(Registration.RUBBER_SIGN_BLOCK_ENTITY.get(), RubberSignRenderer::new);
        BlockEntityRendererRegistry.register(Registration.RUBBER_HANGING_SIGN_BLOCK_ENTITY.get(), RubberHangingSignRenderer::new);
        BlockEntityRendererRegistry.register(Registration.TREETAP_BLOCK_ENTITY.get(), TreeTapRenderer::new);
        for (FactocraftyFluidTanks tank : FactocraftyFluidTanks.values())
            BlockEntityRendererRegistry.register(FactocraftyBlockEntities.ofBlock(tank.get()), FactocraftyLiquidTankRenderer::new);
        MenuRegistry.registerScreenFactory(Registration.GENERATOR_MENU.get(), GeneratorScreen::new);
        MenuRegistry.registerScreenFactory(Registration.GEOTHERMAL_GENERATOR_MENU.get(), GeothermalGeneratorScreen::new);
        MenuRegistry.registerScreenFactory(Registration.ELECTRIC_FURNACE_MENU.get(), ElectricFurnaceScreen::new);
        MenuRegistry.registerScreenFactory(Registration.MACERATOR_MENU.get(), BasicMachineScreen::new);
        MenuRegistry.registerScreenFactory(Registration.ENERGY_CELL_MENU.get(), EnergyCellScreen::new);
        MenuRegistry.registerScreenFactory(Registration.FLUID_TANK_MENU.get(), FluidTankScreen::new);
        MenuRegistry.registerScreenFactory(Registration.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
        MenuRegistry.registerScreenFactory(Registration.COMPRESSOR_MENU.get(), BasicMachineScreen::new);
        MenuRegistry.registerScreenFactory(Registration.EXTRACTOR_MENU.get(), ChangeableInputMachineScreen::new);
        MenuRegistry.registerScreenFactory(Registration.REFINER_MENU.get(), RefinerScreen::new);
        MenuRegistry.registerScreenFactory(Registration.ENRICHER_MENU.get(), EnricherScreen::new);
        MenuRegistry.registerScreenFactory(Registration.GAS_INFUSER_MENU.get(), GasInfuserScreen::new);
        MenuRegistry.registerScreenFactory(Registration.RGB_MENU.get(), RGBControllerScreen::new);


        //ClientGuiEvent.DEBUG_TEXT_LEFT.register((e)-> e.);
        fluidItemBlocksColor();
        dyeItemsColor();
        leavesColor();
        ledColor();

        Registration.ITEMS.forEach(item->{
            if (item.get() instanceof BatteryItem battery)
                ItemPropertiesRegistry.register(item.get(), new ResourceLocation(MOD_ID, "charge_ratio"), (itemStack, clientLevel, livingEntity, i) -> battery.getChargedLevel(itemStack));
        });
        ItemPropertiesRegistry.register(Registration.RGB_CONTROLLER.get(), new ResourceLocation(MOD_ID, "charge_ratio"), (itemStack, clientLevel, livingEntity, i) -> Registration.RGB_CONTROLLER.get().getChargedLevel(itemStack));
    }
}
