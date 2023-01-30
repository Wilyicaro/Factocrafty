package wily.factocrafty.init;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import wily.factocrafty.block.*;
import wily.factocrafty.block.cable.CableBlock;
import wily.factocrafty.block.cable.CableTiers;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.block.cable.SolidCableBlock;
import wily.factocrafty.block.cable.entity.CableBlockEntity;
import wily.factocrafty.block.cable.entity.SolidCableBlockEntity;
import wily.factocrafty.block.entity.*;
import wily.factocrafty.block.generator.*;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factocrafty.block.generator.entity.GeothermalGeneratorBlockEntity;
import wily.factocrafty.block.generator.entity.SolarPanelBlockEntity;
import wily.factocrafty.block.machines.*;
import wily.factocrafty.block.machines.entity.*;
import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.entity.*;
import wily.factocrafty.gen.RubberTreeFoliagePlacer;
import wily.factocrafty.gen.RubberTreeGrower;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.item.*;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.BlockEntityUtil;
import wily.factocrafty.util.registering.*;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static wily.factocrafty.Factocrafty.MOD_ID;
import static wily.factocrafty.Factocrafty.REGISTRIES;
import static wily.factocrafty.block.FactocraftySoundTypes.CABLE;

public class Registration {


    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MOD_ID, Registries.SOUND_EVENT);
    public static final DeferredRegister<Block> BLOCKS_ITEMS = DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(MOD_ID, Registries.FLUID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(MOD_ID, Registries.MENU);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);

    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(MOD_ID, Registries.RECIPE_TYPE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(MOD_ID, Registries.FOLIAGE_PLACER_TYPE);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(MOD_ID, Registries.ENTITY_TYPE);

    // Registrar Register
    private static  final Registrar<Block> BLOCKS_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK);

    private static  final Registrar<? extends Block> GENERIC_BLOCKS_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK);

    private static  final Registrar<BlockEntityType<?>> BLOCK_ENTITIES_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);
    private static  final Registrar<Item> ITEMS_REGISTRAR= REGISTRIES.get().get(Registries.ITEM);
    public static List<ResourceLocation> RegistrarBlockItems = new ArrayList<>();

    public static List<ResourceLocation> RegistrarItems = new ArrayList<>();
    
    //
    public static ResourceLocation getModResource(String id){ return new ResourceLocation( MOD_ID, id);}


    static Item.Properties fullStackItemProperties(){
        return new Item.Properties().stacksTo(64);
    }
    static Item.Properties defaultStackItemProperties(){
        return new Item.Properties().stacksTo(1);
    }

    // Audios
    public static final RegistrySupplier<SoundEvent> CABLE_DIG = SOUNDS.register("block.cable.dig",() -> SoundEvent.createVariableRangeEvent((getModResource("block.cable.dig"))));

    public static final RegistrySupplier<SoundEvent> CABLE_BROKEN = SOUNDS.register("block.cable.broken",() -> SoundEvent.createVariableRangeEvent((getModResource("block.cable.broken"))));

    public static final RegistrySupplier<SoundEvent> CABLE_PLACE = SOUNDS.register("block.cable.place",() -> SoundEvent.createVariableRangeEvent(getModResource("block.cable.place")));

    public static final RegistrySupplier<SoundEvent> MACERATOR_ACTIVE = SOUNDS.register("block.macerator",() -> SoundEvent.createVariableRangeEvent((getModResource("block.macerator"))));


    public static final RegistrySupplier<SoundEvent> WRENCH_TIGHT = SOUNDS.register("item.wrench",() -> SoundEvent.createVariableRangeEvent(getModResource("item.wrench")));


    // Machines Derivatives

    public static final RegistrySupplier<Item> ELECTRONIC_CIRCUIT = registerFactocraftyItem("electronic_circuit");

    public static final RegistrySupplier<Item> CIRCUIT_BOARD = registerFactocraftyItem("circuit_board");

    public static final RegistrySupplier<Item> ADVANCED_CIRCUIT = registerFactocraftyItem("advanced_circuit");

    public static final RegistrySupplier<Item> ADVANCED_CIRCUIT_IMPRESS = registerFactocraftyItem("advanced_circuit_board");



    public static final RegistrySupplier<Block> MACHINE_FRAME_BASE = registerFactocraftyBlockItem(()-> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.MACHINE_FRAME.getName());

    public static final RegistrySupplier<Block> ADVANCED_MACHINE_FRAME_BASE = registerFactocraftyBlockItem(()-> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.ADVANCED_MACHINE_FRAME.getName());

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> MACERATOR_RECIPE = RECIPES.register("macerating", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> COMPRESSOR_RECIPE = RECIPES.register("compressing", () -> new RecipeType<>() {});
    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> EXTRACTOR_RECIPE = RECIPES.register("extracting", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> REFINER_ITEM_RECIPE = RECIPES.register("refining_item", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> REFINER_FLUID_RECIPE = RECIPES.register("refining_fluid", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> MACERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("macerating", () -> new FactocraftyMachineRecipe.Serializer((resourceLocation -> new FactocraftyMachineRecipe("macerating",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> COMPRESSOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("compressing", () -> new FactocraftyMachineRecipe.Serializer((resourceLocation -> new FactocraftyMachineRecipe("compressing",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> EXTRACTOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("extracting", () -> new FactocraftyMachineRecipe.Serializer((resourceLocation -> new FactocraftyMachineRecipe("extracting",resourceLocation)),250));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> REFINING_FLUID_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("refining_fluid", () -> new FactocraftyMachineRecipe.Serializer((resourceLocation -> new FactocraftyMachineRecipe("refining_fluid",resourceLocation){
        @Override
        public boolean hasFluidResult() {
            return true;
        }

        @Override
        public boolean hasFluidIngredient() {
            return true;
        }
    }),200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> REFINING_ITEM_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("refining_item", () -> new FactocraftyMachineRecipe.Serializer((resourceLocation -> new FactocraftyMachineRecipe("refining_item",resourceLocation){
        @Override
        public boolean hasFluidResult() {
            return true;
        }

    }),200));
    public static final RegistrySupplier<Block> GENERATOR = registerFactocraftyBlockItem(()-> new GeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.GENERATOR.getName());

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<GeneratorBlockEntity>>> GENERATOR_MENU = MENUS.register(FactocraftyBlocks.GENERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.GENERATOR,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<GeneratorBlockEntity>>> GEOTHERMAL_GENERATOR_MENU = MENUS.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.GEOTHERMAL_GENERATOR,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<ElectricFurnaceBlockEntity>>> ELECTRIC_FURNACE_MENU = MENUS.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.ELECTRIC_FURNACE,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyMachineBlockEntity>>> COMPRESSOR_MENU = MENUS.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.COMPRESSOR,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyMachineBlockEntity>>> MACERATOR_MENU = MENUS.register(FactocraftyBlocks.MACERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.MACERATOR,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyMachineBlockEntity>>> EXTRACTOR_MENU = MENUS.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.EXTRACTOR,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyMachineBlockEntity>>> REFINER_MENU = MENUS.register(FactocraftyBlocks.REFINER.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.REFINER,id, buf.readBlockPos(), inventory.player)));
    public static final RegistrySupplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.GENERATOR.getName(), () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, GENERATOR.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.GENERATOR.getName())));

    public static final RegistrySupplier<Block> GEOTHERMAL_GENERATOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> new GeothermalGeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> BlockEntityType.Builder.of(GeothermalGeneratorBlockEntity::new, GEOTHERMAL_GENERATOR.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName())));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyEnergyStorageBlockEntity>>> ENERGY_CELL_MENU = MENUS.register(FactocraftyMenus.ENERGY_CELL.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.ENERGY_CELL,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<FactocraftyFluidTankBlockEntity>>> FLUID_TANK_MENU = MENUS.register(FactocraftyMenus.FLUID_TANK.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.FLUID_TANK,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<MenuType<FactocraftyProcessMenu<SolarPanelBlockEntity>>> SOLAR_PANEL_MENU = MENUS.register(FactocraftyMenus.SOLAR_PANEL.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyProcessMenu<>(FactocraftyMenus.SOLAR_PANEL,id, buf.readBlockPos(), inventory.player)));

    public static final RegistrySupplier<Block> IRON_FURNACE = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.IRON_FURNACE.getName(), () -> new IronFurnace(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.IRON_FURNACE.getName(), () -> BlockEntityType.Builder.of(IronFurnaceBlockEntity::new, IRON_FURNACE.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.IRON_FURNACE.getName())));

    public static final RegistrySupplier<Block> ELECTRIC_FURNACE = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, ELECTRIC_FURNACE.get()).build(null));



    public static final RegistrySupplier<Block> MACERATOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.MACERATOR.getName(), () -> new MaceratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<MaceratorBlockEntity>> MACERATOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.MACERATOR.getName(), () -> BlockEntityType.Builder.of(MaceratorBlockEntity::new, MACERATOR.get()).build(null));

    public static final RegistrySupplier<Block> COMPRESSOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> new CompressorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> BlockEntityType.Builder.of(CompressorBlockEntity::new, COMPRESSOR.get()).build(null));

    public static final RegistrySupplier<Block> EXTRACTOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> new ExtractorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistrySupplier<BlockEntityType<ExtractorBlockEntity>> EXTRACTOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> BlockEntityType.Builder.of(ExtractorBlockEntity::new, EXTRACTOR.get()).build(null));

    public static final RegistrySupplier<Block> REFINER = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.REFINER.getName(), () -> new RefinerBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<RefinerBlockEntity>> REFINER_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.REFINER.getName(), () -> BlockEntityType.Builder.of(RefinerBlockEntity::new, REFINER.get()).build(null));

    public static final RegistrySupplier<Item> WRENCH =  ITEMS.register("wrench", () -> new WrenchItem(defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CUTTER =  ITEMS.register("cutter", () -> new CraftingToolItem(defaultStackItemProperties().durability(300)));

    public static final RegistrySupplier<Item> HAMMER =  ITEMS.register("hammer", () -> new CraftingToolItem(defaultStackItemProperties().durability(375)));

    public static final RegistrySupplier<Item> DRILL =  ITEMS.register("mining_drill", () -> new DrillItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> DIAMOND_DRILL =  ITEMS.register("diamond_drill", () -> new DrillItem(Tiers.DIAMOND,1, -2.8F, FactoryCapacityTiers.ADVANCED,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CHAINSAW =  ITEMS.register("chainsaw", () -> new ChainsawItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> DIAMOND_CHAINSAW =  ITEMS.register("diamond_chainsaw", () -> new ChainsawItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> MINING_LASER =  ITEMS.register("mining_laser", () -> new MiningLaserItem(FactoryCapacityTiers.HIGH,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> BATTERY =  ITEMS.register("battery", () -> new BatteryItem(FactoryCapacityTiers.BASIC,1000,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> ADVANCED_BATTERY =  ITEMS.register("advanced_battery", () -> new BatteryItem(FactoryCapacityTiers.ADVANCED,4000,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> ENERGY_CRYSTAL =  ITEMS.register("energy_crystal", () -> new BatteryItem(FactoryCapacityTiers.HIGH,30000,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> LAPOTRON_CRYSTAL =  ITEMS.register("lapotron_crystal", () -> new BatteryItem(FactoryCapacityTiers.ULTIMATE,100000,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> FLUID_CELL =  ITEMS.register("fluid_cell", () -> new FluidCellItem(defaultStackItemProperties().stacksTo(16)));

    // Rubber Wood Derivatives
    public static final RegistrySupplier<Block> RUBBER_LOG = registerFactocraftyBlockItem(()-> new RubberLog(BlockBehaviour.Properties.copy(Blocks.MANGROVE_LOG)), FactocraftyBlocks.RUBBER_LOG.getName());

    public static final RegistrySupplier<Block> RUBBER_WOOD= registerFactocraftyBlockItem(()-> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_WOOD)), FactocraftyBlocks.RUBBER_WOOD.getName());
    public static final RegistrySupplier<Block> STRIPPED_RUBBER_LOG = registerFactocraftyBlockItem(()-> new StrippedRubberLog(BlockBehaviour.Properties.copy(Blocks.STRIPPED_MANGROVE_LOG)), FactocraftyBlocks.STRIPPED_RUBBER_LOG.getName());

    public static final RegistrySupplier<BlockEntityType<StrippedRubberLogBlockEntity>> STRIPPED_RUBBER_LOG_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.STRIPPED_RUBBER_LOG.getName(), () -> BlockEntityType.Builder.of(StrippedRubberLogBlockEntity::new, STRIPPED_RUBBER_LOG.get()).build(null));


    public static final RegistrySupplier<Block> TREETAP_BOWL= registerFactocraftyBlockItem(()-> new TreeTapBlock(BlockBehaviour.Properties.copy(Blocks.COMPOSTER)), FactocraftyBlocks.TREETAP.getName());

    public static final RegistrySupplier<BlockEntityType<TreeTapBlockEntity>> TREETAP_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.TREETAP.getName(), () -> BlockEntityType.Builder.of(TreeTapBlockEntity::new, TREETAP_BOWL.get()).build(null));

    public static final RegistrySupplier<Block> STRIPPED_RUBBER_WOOD= registerFactocraftyBlockItem(()-> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_MANGROVE_WOOD)), FactocraftyBlocks.STRIPPED_RUBBER_WOOD.getName());
    public static final RegistrySupplier<Block> RUBBER_LEAVES = registerFactocraftyBlockItem(()-> new LeavesBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_LEAVES)), FactocraftyBlocks.RUBBER_LEAVES.getName());

    public static final RegistrySupplier<Block> RUBBER_PLANKS = registerFactocraftyBlockItem(()-> new Block(BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS)), FactocraftyBlocks.RUBBER_PLANKS.getName());

    public static final RegistrySupplier<Block> RUBBER_SLAB = registerFactocraftyBlockItem(()-> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_SLAB)), FactocraftyBlocks.RUBBER_SLAB.getName());

    public static final RegistrySupplier<Block> RUBBER_STAIRS = registerFactocraftyBlockItem(()-> new StairBlock(RUBBER_PLANKS.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.MANGROVE_STAIRS)), FactocraftyBlocks.RUBBER_STAIRS.getName());

    public static final RegistrySupplier<Block> RUBBER_FENCE = registerFactocraftyBlockItem(()-> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_FENCE)), FactocraftyBlocks.RUBBER_FENCE.getName());

    public static final RegistrySupplier<Block> RUBBER_FENCE_GATE = registerFactocraftyBlockItem(()-> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_FENCE_GATE), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN), FactocraftyBlocks.RUBBER_FENCE_GATE.getName());

    public static final RegistrySupplier<Block> RUBBER_DOOR = registerFactocraftyBlockItem(()-> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_DOOR), SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN), FactocraftyBlocks.RUBBER_DOOR.getName());

    public static final RegistrySupplier<Block> RUBBER_TRAPDOOR = registerFactocraftyBlockItem(()-> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_TRAPDOOR), SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_CLOSE), FactocraftyBlocks.RUBBER_TRAPDOOR.getName());

    public static final RegistrySupplier<Block> RUBBER_PRESSURE_PLATE = registerFactocraftyBlockItem(()-> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS), SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON), FactocraftyBlocks.RUBBER_PRESSURE_PLATE.getName());

    public static final RegistrySupplier<Block> RUBBER_BUTTON = registerFactocraftyBlockItem(()-> new ButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD),1,true, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON), FactocraftyBlocks.RUBBER_BUTTON.getName());

    public static final RegistrySupplier<Block> RUBBER_SIGN = registerFactocraftyBlock(()-> new RubberSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_SIGN.getName());
    public static final RegistrySupplier<Block> RUBBER_WALL_SIGN = registerFactocraftyBlock(()-> new RubberWallSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_WALL_SIGN.getName());

    public static final RegistrySupplier<Item> RUBBER_SIGN_ITEM = ITEMS.register("rubber_sign", () -> new SignItem(fullStackItemProperties(), RUBBER_SIGN.get(), RUBBER_WALL_SIGN.get()));
    public static final RegistrySupplier<BlockEntityType<RubberSignBlockEntity>> RUBBER_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.RUBBER_SIGN.getName(), () -> BlockEntityType.Builder.of(RubberSignBlockEntity::new, RUBBER_SIGN.get(), RUBBER_WALL_SIGN.get()).build(null));

    public static final RegistrySupplier<Block> RUBBER_TREE_SAPLING = BLOCKS_ITEMS.register( "rubber_sapling", () -> new SaplingBlock(new RubberTreeGrower(), BlockBehaviour.Properties.copy(Blocks.ACACIA_SAPLING)));

    public static final RegistrySupplier<Item> STICKY_RESIN = registerFactocraftyItem( "sticky_resin");

    public static final RegistrySupplier<Item> ENERGIUM_DUST = registerFactocraftyItem( "energium_dust");


    public static final RegistrySupplier<Item> GRAFANO_HELMET = ITEMS.register("grafano_helmet", ()-> new GrafanoArmorItem(EquipmentSlot.HEAD,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_CHEST = ITEMS.register("grafano_chestplate", ()-> new GrafanoArmorItem(EquipmentSlot.CHEST,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_LEGGINGS = ITEMS.register("grafano_leggings", ()-> new GrafanoArmorItem(EquipmentSlot.LEGS,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_BOOTS = ITEMS.register("grafano_boots", ()-> new GrafanoArmorItem(EquipmentSlot.FEET,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CARBON_PLATE = registerFactocraftyItem( "carbon_plate");

    public static final RegistrySupplier<Item> CARBON_FIBERS = registerFactocraftyItem( "carbon_fibers");

    public static final RegistrySupplier<Item> COMBINED_CARBON = registerFactocraftyItem( "combined_carbon_fibers");

    public static final RegistrySupplier<Item> BASIC_HANG_GLIDER = ITEMS.register("basic_hang_glider", ()-> new HangGliderItem(defaultStackItemProperties()));

    public static final RegistrySupplier<Item> ELECTRIC_JETPACK = ITEMS.register("electric_jetpack", ()-> new ElectricJetpackItem(FactoryCapacityTiers.ADVANCED,FactocraftyArmorMaterials.JETPACK,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> FLEX_JETPACK = ITEMS.register("flex_jetpack", ()-> new FlexJetpackItem(FactocraftyArmorMaterials.JETPACK,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> RUBBER_BOAT_ITEM = ITEMS.register("rubber_boat", () -> new FactocraftyBoatItem(false,IFactocraftyBoat.Type.RUBBER, defaultStackItemProperties()));

    public static final RegistrySupplier<Item> RUBBER_CHEST_BOAT_ITEM = ITEMS.register("rubber_chest_boat", () -> new FactocraftyBoatItem(true, IFactocraftyBoat.Type.RUBBER, defaultStackItemProperties()) );

    public static final RegistrySupplier<EntityType<FactocraftyChestBoat>> FACTOCRAFTY_CHEST_BOAT = ENTITY_TYPES.register("factocrafty_chest_boat", ()-> EntityType.Builder.<FactocraftyChestBoat>of(FactocraftyChestBoat::new,MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(getModResource("factocrafty_chest_boat").toString()));

    public static final RegistrySupplier<EntityType<FactocraftyBoat>> FACTOCRAFTY_BOAT = ENTITY_TYPES.register("factocrafty_boat", ()-> EntityType.Builder.<FactocraftyBoat>of(FactocraftyBoat::new,MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(getModResource("factocrafty_boat").toString()));

    public static final RegistrySupplier<EntityType<CorruptedEnderMan>> CORRUPTED_ENDERMAN = ENTITY_TYPES.register("corrupted_enderman", ()-> EntityType.Builder.of(CorruptedEnderMan::new,MobCategory.MONSTER).sized(0.6F, 2.9F).clientTrackingRange(6).build(getModResource("corrupted_enderman").toString()));

    public static final RegistrySupplier<Item> CORRUPTED_ENDERMAN_SPAWN_EGG = ITEMS.register("corrupted_enderman_spawn_egg",()-> new ArchitecturySpawnEggItem(CORRUPTED_ENDERMAN,161816,280428,fullStackItemProperties()));

    public static final RegistrySupplier<EntityType<LaserProjectile>> LASER = ENTITY_TYPES.register("laser", ()-> EntityType.Builder.<LaserProjectile>of((LaserProjectile::new),MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(getModResource("laser").toString()));

    public static final RegistrySupplier<FoliagePlacerType<?>> RUBBER_TREE_FOLIAGE_PLACER = FOLIAGE_PLACERS.register("rubber_fancy_foliage_placer", ()-> new FoliagePlacerType(RubberTreeFoliagePlacer.CODEC));


    // Register Util Methods
    public static Block getRegistrarBlockEntry(String id ){
        return BLOCKS_REGISTRAR.get(getModResource(id));
    }

    public static Fluid getRegistrarFluidEntry(String id ){
        return FLUIDS.getRegistrar().get(getModResource(id));
    }


    public static <T extends BlockEntity> BlockEntityType<T>  getRegistrarBlockEntityEntry(String id ){
        return (BlockEntityType<T>) BLOCK_ENTITIES_REGISTRAR.get(getModResource(id));
    }

    public static MenuType<?> getRegistrarMenuEntry(String id ){
        return MENUS.getRegistrar().get(getModResource(id));
    }

    public static Item getRegistrarItemEntry(String id ){
        return ITEMS_REGISTRAR.get(getModResource(id));
    }

    private static void registerAllOreDerivatives(FactocraftyOre.Tier oreTiers) {
        oreTiers.getDerivative("ore").ifPresent((e)-> {
            if (e instanceof FactocraftyOre.Derivative.OreDerivative ore) {
                if (ore.common)
                    registrarFactocraftyBlockItem(() -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE), ConstantInt.of((int) (0.6*ore.averageXp))), oreTiers.getOreName(false));
                if (ore.deep)
                    registrarFactocraftyBlockItem(() -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE),ConstantInt.of(ore.averageXp)), oreTiers.getOreName(true));
            };});
        if (oreTiers.hasDerivative(FactocraftyOre.INGOT)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()), oreTiers.getName() + "_ingot");
        if (oreTiers.hasDerivative(FactocraftyOre.COMMON_DROP)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()), oreTiers.getName());
        if (oreTiers.hasDerivative(FactocraftyOre.CRUSHED)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()), "crushed_" + oreTiers.getName());
        if (oreTiers.hasDerivative(FactocraftyOre.PLATE)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()),  oreTiers.getName() + "_plate");
        if (oreTiers.hasDerivative(FactocraftyOre.BLOCK)) registrarFactocraftyBlockItem(() -> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).color(oreTiers.getColor())), oreTiers.getName() + "_block");
        if (oreTiers.hasDerivative(FactocraftyOre.REFINED))registrarFactocraftyItem(() -> new Item(fullStackItemProperties()),  "refined_" + oreTiers.getName());
        if (oreTiers.hasDerivative(FactocraftyOre.DUST)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()),  oreTiers.getName() +  "_dust");
        if (oreTiers.hasDerivative(FactocraftyOre.NUGGET)) registrarFactocraftyItem(() -> new Item(fullStackItemProperties()), oreTiers.getName() + "_nugget");
        if (oreTiers.hasDerivative(FactocraftyOre.RAW)) {
            registrarFactocraftyBlockItem(() -> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).color(oreTiers.getColor())), "raw_" + oreTiers.getName() + "_block");
            registrarFactocraftyItem(() -> new Item(fullStackItemProperties()), "raw_" + oreTiers.getName());
        }
        if (oreTiers.getArmor() != null ) {
            registrarFactocraftyItem(() -> new ArmorItem(oreTiers.getArmor(), EquipmentSlot.FEET, defaultStackItemProperties()), oreTiers.getName() + "_boots");
            registrarFactocraftyItem(() -> new ArmorItem(oreTiers.getArmor(), EquipmentSlot.LEGS, defaultStackItemProperties()), oreTiers.getName() + "_leggings");
            registrarFactocraftyItem(() -> new ArmorItem(oreTiers.getArmor(), EquipmentSlot.CHEST, defaultStackItemProperties()), oreTiers.getName() + "_chestplate");
            registrarFactocraftyItem(() -> new ArmorItem(oreTiers.getArmor(), EquipmentSlot.HEAD, defaultStackItemProperties()), oreTiers.getName() + "_helmet");
        }
        if (oreTiers.getToolTier() != null){
            registrarFactocraftyItem(() -> new SwordItem(oreTiers.getToolTier(),3, -2.4F , defaultStackItemProperties()),oreTiers.getName() + "_sword");
            registrarFactocraftyItem(() -> new PickaxeItem(oreTiers.getToolTier(),1, -2.8F , defaultStackItemProperties()),oreTiers.getName() + "_pickaxe");
            registrarFactocraftyItem(() -> new AxeItem(oreTiers.getToolTier(),6.0F, -3.1F , defaultStackItemProperties()),oreTiers.getName() + "_axe");
            registrarFactocraftyItem(() -> new ShovelItem(oreTiers.getToolTier(),1.5F, -3.0F , defaultStackItemProperties()),oreTiers.getName() + "_shovel");
            registrarFactocraftyItem(() -> new HoeItem(oreTiers.getToolTier(),-3, 0.0F ,defaultStackItemProperties()),oreTiers.getName() + "_hoe");
        }
    }
    private static void registerCables(CableTiers tier) {
        if (tier.hasInsulated) {
            registrarFactocraftyBlockItem(() -> new CableBlock(tier, cableBehaviour()), tier.getName());
            registrarFactocraftyBlockItem(() -> new InsulatedCableBlock(tier, cableBehaviour()), "insulated_" + tier.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(tier.getName()), () -> BlockEntityType.Builder.of((blockPos, blockState) -> new CableBlockEntity(tier, blockPos, blockState), tier.getBlock(), tier.getInsulatedBlock()).build(null));
        } else {
            registrarFactocraftyBlockItem(() -> new SolidCableBlock(tier, cableBehaviour()), tier.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(tier.getName()), () -> BlockEntityType.Builder.of((blockPos, blockState) -> new SolidCableBlockEntity(tier, blockPos, blockState), tier.getBlock()).build(null));
        }
    }
    private static void registerSolarPanel(SolarPanelTiers tier) {
        RegistrySupplier<Block> b = registrarFactocraftyBlockItem(() -> tier.ordinal() <= 1 ? new FlexibleSolarPanelBlock(tier,BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS_PANE)) : new SolarPanelBlock(tier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), tier.getName());
        BLOCK_ENTITIES_REGISTRAR.register(getModResource(tier.getName()), () -> BlockEntityType.Builder.of((bp, bs) -> new SolarPanelBlockEntity(tier, bp, bs), b.get()).build(null));
    }
    private static void registerEnergyStorage(FactocraftyEnergyStorages storage) {

            registrarFactocraftyBlock(() -> new FactocraftyEnergyStorageBlock(storage.capacityTier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), storage.getName());
            registrarFactocraftyItem(()-> new EnergyBlockItem(storage.get(),storage.capacityTier, TransportState.EXTRACT_INSERT, fullStackItemProperties()), storage.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(storage.getName()), () -> BlockEntityType.Builder.of((bp, bs) -> new FactocraftyEnergyStorageBlockEntity(storage.capacityTier, bp, bs), storage.get()).build(null));

    }
    private static void registerFluidTank(FactocraftyFluidTanks tank) {
            registrarFactocraftyBlock(() -> new FactocraftyFluidTankBlock(tank.capacityTier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), tank.getName());
            registrarFactocraftyItem(()-> new FluidBlockItem(tank.get(), fullStackItemProperties()), tank.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(tank.getName()), () -> BlockEntityType.Builder.of((bp, bs) -> new FactocraftyFluidTankBlockEntity(tank.capacityTier, bp, bs), tank.get()).build(null));

    }
    private static BlockBehaviour.Properties cableBehaviour(){return  BlockBehaviour.Properties.of(Material.CLOTH_DECORATION).sound(CABLE);}

    private static void registerFluid(String name, int viscosity, int density, int tick, int color, Material material){

        ArchitecturyFluidAttributes FLUID_ATTRIBUTE = SimpleArchitecturyFluidAttributes.of(()-> getRegistrarFluidEntry("flowing_" + name), ()-> getRegistrarFluidEntry(name)).blockSupplier(()-> (RegistrySupplier<? extends LiquidBlock>) GENERIC_BLOCKS_REGISTRAR.delegate(getModResource(name))).bucketItemSupplier(() -> ITEMS_REGISTRAR.delegate(getModResource(name + "_bucket"))).viscosity(viscosity).density(density).flowingTexture(new ResourceLocation(MOD_ID,"block/fluid/" + name + "_flow")).sourceTexture(new ResourceLocation(MOD_ID, "block/fluid/" + name + "_still")).overlayTexture(new ResourceLocation(MOD_ID, "block/fluid/" + name + "_still")).tickDelay(tick).color(color);
        FLUIDS.getRegistrar().register(getModResource(name),() -> new ArchitecturyFlowingFluid.Source(FLUID_ATTRIBUTE));
        FLUIDS.getRegistrar().register(getModResource("flowing_" + name), () -> new ArchitecturyFlowingFluid.Flowing(FLUID_ATTRIBUTE));
        BLOCKS_REGISTRAR.register(getModResource(name), () -> new ArchitecturyLiquidBlock(()-> (FlowingFluid) getRegistrarFluidEntry(name), BlockBehaviour.Properties.of(material).noCollission().randomTicks().strength(100.0F).noLootTable()));
        ITEMS_REGISTRAR.register( getModResource( name+ "_bucket"), () -> new ArchitecturyBucketItem(()-> getRegistrarFluidEntry(name), defaultStackItemProperties() ));

    }

    private static RegistrySupplier<Block>  registrarFactocraftyBlockItem(Supplier<Block> block, String name){
        RegistrarBlockItems.add(getModResource(name));
        return registrarFactocraftyBlock(block,name);
    }
    private static RegistrySupplier<Block> registrarFactocraftyBlock(Supplier<Block> block, String name){
        return BLOCKS_REGISTRAR.register( getModResource(name),block);
    }


    private static void registrarFactocraftyItem(Supplier<Item> item, String name){
        ITEMS_REGISTRAR.register( getModResource(name),item);
        RegistrarItems.add(getModResource(name));
    }

    private static RegistrySupplier<Block> registerFactocraftyBlockItem(Supplier<Block> block, String name){
        return BLOCKS_ITEMS.register(name, block);
    }

    private static RegistrySupplier<Block> registerFactocraftyBlock(Supplier<Block> block, String name){
        return BLOCKS.register(name, block);
    }

    private static RegistrySupplier<Item> registerFactocraftyItem(String name){
        return ITEMS.register( name, () -> new Item(fullStackItemProperties()));
    }

    // Main Register
    public static void registerObjects(){
        WoodType.register(FactocraftyWoodType.RUBBER);
        SOUNDS.register();
        BLOCKS_ITEMS.register();
        BLOCKS_ITEMS.forEach((blockEntry) -> {ITEMS_REGISTRAR.register(blockEntry.getId(), () -> new BlockItem(blockEntry.get(), fullStackItemProperties()));});
        registerFluid(FactocraftyFluids.PETROLEUM.getName(),10000,8000,15,new Color(16, 16, 16).getRGB(), FactocraftyMaterials.PETROLEUM);
        registerFluid(FactocraftyFluids.LATEX.getName(),6000,6000,9,0xFFFFFFFF, FactocraftyMaterials.LATEX);
        registerFluid(FactocraftyFluids.COOLANT.getName(),4000,4000,4,new Color(0, 89, 93).getRGB(), Material.WATER);
        registerFluid(FactocraftyFluids.GASOLINE.getName(),6000,4000,6,new Color(140, 96, 0).getRGB(), FactocraftyMaterials.GASOLINE);
        MENUS.register();
        FLUIDS.register();
        BLOCKS.register();
        ENTITY_TYPES.register();
        ITEMS.register();
        for (SolarPanelTiers tier : SolarPanelTiers.values()) registerSolarPanel(tier);
        for (FactocraftyOre.Tier tier : FactocraftyOre.Tier.values()) registerAllOreDerivatives(tier);
        for (FactocraftyEnergyStorages storage : FactocraftyEnergyStorages.values()) registerEnergyStorage(storage);
        for (FactocraftyFluidTanks tank : FactocraftyFluidTanks.values()) registerFluidTank(tank);
        for (CableTiers tier : CableTiers.values()) registerCables(tier);
        RegistrarBlockItems.forEach((blockEntry) -> {ITEMS_REGISTRAR.register(blockEntry, () -> new BlockItem(Objects.requireNonNull(BLOCKS_REGISTRAR.get(blockEntry)), fullStackItemProperties()));});
        RECIPES.register();
        RECIPE_SERIALIZER.register();
        BLOCK_ENTITIES.register();
        FOLIAGE_PLACERS.register();
    }
}
