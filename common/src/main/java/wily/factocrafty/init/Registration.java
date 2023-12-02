package wily.factocrafty.init;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import wily.factocrafty.block.generator.entity.*;
import wily.factocrafty.block.transport.fluid.FluidPipeBlock;
import wily.factocrafty.block.transport.fluid.FluidPipeBlockEntity;
import wily.factocrafty.fluid.FactocraftyFluidAttributes;
import wily.factocrafty.block.*;
import wily.factocrafty.block.transport.energy.CableBlock;
import wily.factocrafty.block.transport.energy.SolidCableBlock;
import wily.factocrafty.fluid.FactocraftyFlowingFluid;
import wily.factocrafty.fluid.FactocraftySourceFluid;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factocrafty.block.transport.energy.entity.SolidCableBlockEntity;
import wily.factocrafty.block.entity.*;
import wily.factocrafty.block.generator.*;
import wily.factocrafty.block.machines.*;
import wily.factocrafty.block.machines.entity.*;
import wily.factocrafty.block.storage.energy.FactocraftyEnergyStorageBlock;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;
import wily.factocrafty.entity.*;
import wily.factocrafty.gen.BasinFeature;
import wily.factocrafty.gen.RubberTreeFoliagePlacer;
import wily.factocrafty.gen.RubberTreeGrower;
import wily.factocrafty.inventory.FactocraftyItemMenuContainer;
import wily.factocrafty.item.*;
import wily.factocrafty.recipes.*;
import wily.factocrafty.tag.Fluids;
import wily.factocrafty.util.BlockEntityUtil;
import wily.factocrafty.util.registering.*;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
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

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);


    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(MOD_ID, Registries.RECIPE_TYPE);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACERS = DeferredRegister.create(MOD_ID, Registries.FOLIAGE_PLACER_TYPE);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(MOD_ID, Registries.FEATURE);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(MOD_ID, Registries.ENTITY_TYPE);

    // Registrar Register
    private static  final Registrar<Block> BLOCKS_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK);

    private static  final Registrar<? extends Block> GENERIC_BLOCKS_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK);

    private static  final Registrar<BlockEntityType<?>> BLOCK_ENTITIES_REGISTRAR = REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);
    public static  final Registrar<Item> ITEMS_REGISTRAR= REGISTRIES.get().get(Registries.ITEM);
    public static List<ResourceLocation> RegistrarBlockItems = new ArrayList<>();

    public static List<ResourceLocation> RegistrarItems = new ArrayList<>();

    public static Map<ResourceLocation, Function<Block,BlockItem>> blockItemOverrides = new HashMap<>();
    
    //
    public static ResourceLocation getModResource(String id){ return new ResourceLocation( MOD_ID, id);}


    static Item.Properties fullStackItemProperties(){
        return new Item.Properties().stacksTo(64);
    }
    static Item.Properties defaultStackItemProperties(){
        return new Item.Properties().stacksTo(1);
    }


    public static final RegistrySupplier<CreativeModeTab> FACTOCRAFTY_TAB = TABS.register("factocrafty_tab",()->CreativeTabRegistry.create((b)->b.title(Component.translatable("itemGroup.factocrafty.tab")).icon(() -> new ItemStack(Registration.MACERATOR.get())).displayItems(
            ((bool,output) -> {
                Iterator var2 = BuiltInRegistries.ITEM.iterator();

                while(var2.hasNext()) {
                    Item item = (Item) var2.next();
                    if (item.arch$registryName().getNamespace().equals(MOD_ID)) {
                        output.accept(item);
                        if (item instanceof ICraftyStorageItem energyItem) {
                            ItemStack charged = new ItemStack(item);
                            ICraftyEnergyStorage storage = energyItem.getEnergyStorage(charged);
                            storage.receiveEnergy(new CraftyTransaction(storage.getMaxEnergyStored(), storage.getSupportedTier()), false);
                            output.accept(charged);
                        } else if (item instanceof FluidCellItem cell) {
                            Registration.FLUIDS.getRegistrar().forEach((f) -> {
                                        if (f.isSource(null)) {
                                            ItemContainerUtil.ItemFluidContext context = ItemContainerUtil.fillItem(new ItemStack(cell), FluidStack.create(f, FluidStack.bucketAmount()));
                                            output.accept(context.container());
                                        }
                                    }
                            );
                        }
                    }
                }
            }))));



    // Audios
    public static final RegistrySupplier<SoundEvent> CABLE_DIG = SOUNDS.register("block.cable.dig",() -> SoundEvent.createVariableRangeEvent((getModResource("block.cable.dig"))));

    public static final RegistrySupplier<SoundEvent> CABLE_BROKEN = SOUNDS.register("block.cable.broken",() -> SoundEvent.createVariableRangeEvent((getModResource("block.cable.broken"))));

    public static final RegistrySupplier<SoundEvent> CABLE_PLACE = SOUNDS.register("block.cable.place",() -> SoundEvent.createVariableRangeEvent(getModResource("block.cable.place")));

    public static final RegistrySupplier<SoundEvent> MACERATOR_ACTIVE = SOUNDS.register("block.macerator",() -> SoundEvent.createVariableRangeEvent((getModResource("block.macerator"))));

    public static final RegistrySupplier<SoundEvent> SAWMILL_ACTIVE = SOUNDS.register("block.sawmill",() -> SoundEvent.createVariableRangeEvent((getModResource("block.sawmill"))));

    public static final RegistrySupplier<SoundEvent> COMPRESSOR_ACTIVE = SOUNDS.register("block.compressor",() -> SoundEvent.createVariableRangeEvent((getModResource("block.compressor"))));

    public static final RegistrySupplier<SoundEvent> EXTRACTOR_ACTIVE = SOUNDS.register("block.extractor",() -> SoundEvent.createVariableRangeEvent((getModResource("block.extractor"))));

    public static final RegistrySupplier<SoundEvent> ELECTRIC_SHOCK = SOUNDS.register("ambient.electric_shock",() -> SoundEvent.createVariableRangeEvent((getModResource("ambient.electric_shock"))));

    public static final RegistrySupplier<SoundEvent> JETPACK_FLIGHT = SOUNDS.register("player.jetpack_flight",() -> SoundEvent.createVariableRangeEvent((getModResource("player.jetpack_flight"))));

    public static final RegistrySupplier<SoundEvent> JETPACK_ENGINE_START = SOUNDS.register("player.jetpack_engine_start",() -> SoundEvent.createVariableRangeEvent((getModResource("player.jetpack_engine_start"))));

    public static final RegistrySupplier<SoundEvent> WRENCH_TIGHT = SOUNDS.register("item.wrench",() -> SoundEvent.createVariableRangeEvent(getModResource("item.wrench")));


    // Machines Derivatives

    public static final RegistrySupplier<Item> ELECTRONIC_CIRCUIT = registerFactocraftyItem("electronic_circuit");

    public static final RegistrySupplier<WeldableItem> CIRCUIT_BOARD = ITEMS.register("circuit_board", ()-> new WeldableItem(fullStackItemProperties()));

    public static final RegistrySupplier<Item> ADVANCED_CIRCUIT = registerFactocraftyItem("advanced_circuit");

    public static final RegistrySupplier<WeldableItem> ADVANCED_CIRCUIT_BOARD = ITEMS.register("advanced_circuit_board", ()-> new WeldableItem(fullStackItemProperties()));

    public static final RegistrySupplier<Item> COIL = registerFactocraftyItem("coil");

    public static final RegistrySupplier<Item> ELECTRIC_MOTOR = registerFactocraftyItem("electric_motor");

    public static final RegistrySupplier<Item> POWER_UNIT = registerFactocraftyItem("power_unit");

    public static final RegistrySupplier<Item> SMALL_POWER_UNIT = registerFactocraftyItem("small_power_unit");

    public static final RegistrySupplier<Item> HEAT_DIFFUSER = registerFactocraftyItem("heat_diffuser");

    public static final RegistrySupplier<Item> SCRAP = registerFactocraftyItem("scrap");

    public static final RegistrySupplier<Item> SCRAP_BOX = ITEMS.register("scrap_box",()-> new ScrapBoxItem(fullStackItemProperties()));

    public static final RegistrySupplier<Item> ADVANCED_ALLOY = ITEMS.register("advanced_alloy",()-> new Item(fullStackItemProperties().rarity(Rarity.UNCOMMON)));

    public static final RegistrySupplier<Item> POLY_SOLAR_PANEL = registerFactocraftyItem("poly_solar_panel");
    public static final RegistrySupplier<Item> MONO_SOLAR_PANEL = ITEMS.register("mono_solar_panel",()-> new Item(fullStackItemProperties().rarity(Rarity.UNCOMMON)));

    public static final RegistrySupplier<Block> REINFORCED_STONE = registerFactocraftyBlockItem(()-> new Block(BlockBehaviour.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops().strength(35.0F, 400.0F)),"reinforced_stone");

    public static final RegistrySupplier<Block> REINFORCED_STONE_SLAB = registerFactocraftyBlockItem(()-> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE_SLAB).requiresCorrectToolForDrops().strength(35.0F, 400.0F)),"reinforced_stone_slab");

    public static final RegistrySupplier<Block> REINFORCED_STONE_STAIRS = registerFactocraftyBlockItem(()-> new StairBlock(REINFORCED_STONE.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.STONE_STAIRS).requiresCorrectToolForDrops().strength(35.0F, 400.0F)),"reinforced_stone_stairs");

    public static final RegistrySupplier<Block> REINFORCED_GLASS = registerFactocraftyBlockItem(()-> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).requiresCorrectToolForDrops().strength(35.0F, 400.0F).noOcclusion().isValidSpawn((a,b,c,d)-> false).isRedstoneConductor((a,b,c)-> false).isSuffocating((a,b,c)-> false).isViewBlocking((a,b,c)-> false)),"reinforced_glass");

    public static final RegistrySupplier<Block> REINFORCED_GLASS_PANE = registerFactocraftyBlockItem(()-> new IronBarsBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).requiresCorrectToolForDrops().strength(25.0F, 300.0F)),"reinforced_glass_pane");

    public static final RegistrySupplier<Block> MACHINE_FRAME_BASE = registerFactocraftyBlockItem(()-> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.MACHINE_FRAME.getName());

    public static final RegistrySupplier<Block> ADVANCED_MACHINE_FRAME_BASE = registerFactocraftyBlockItem(()-> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2.5f)), FactocraftyBlocks.ADVANCED_MACHINE_FRAME.getName());

    public static final RegistrySupplier<Block> NUCLEAR_REACTOR_CASING = registerFactocraftyBlockItem(()-> new FactocraftyReactorCasing(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2.5f)), FactocraftyBlocks.NUCLEAR_REACTOR_CASING.getName());

    public static final RegistrySupplier<Block> NUCLEAR_REACTOR_CHAMBER = registerFactocraftyBlockItem(()-> new FactocraftyReactorCasing(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2.6f)), FactocraftyBlocks.NUCLEAR_REACTOR_CHAMBER.getName());

    public static final RegistrySupplier<Block> REACTOR_CONTAINMENT = registerFactocraftyBlockItem(()-> new FactocraftyReactorCasing(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2.8f)), FactocraftyBlocks.REACTOR_CONTAINMENT.getName());

    public static final RegistrySupplier<Block> REACTOR_GLASS = registerFactocraftyBlockItem(()-> new FactocraftyReactorCasing(BlockBehaviour.Properties.copy(Blocks.GLASS).destroyTime(2f).noOcclusion().isValidSpawn((a,b,c,d)-> false).isRedstoneConductor((a,b,c)-> false).isSuffocating((a,b,c)-> false).isViewBlocking((a,b,c)-> false)){
        public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
            return Shapes.empty();
        }
        public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
            return 1.0f;
        }
        public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
            return true;
        }
        public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
            if (blockState2.is(this)) return true;
            return super.skipRendering(blockState, blockState2, direction);
        }
    }, FactocraftyBlocks.REACTOR_GLASS.getName());

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> MACERATOR_RECIPE = RECIPE_TYPES.register("macerating", () -> new RecipeType<>() {});


    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> COMPRESSOR_RECIPE = RECIPE_TYPES.register("compressing", () -> new RecipeType<>() {});
    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> EXTRACTOR_RECIPE = RECIPE_TYPES.register("extracting", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> REFINER_RECIPE = RECIPE_TYPES.register("refining", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<EnricherRecipe>> ENRICHER_RECIPE = RECIPE_TYPES.register("enriching", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> RECYCLER_RECIPE = RECIPE_TYPES.register("recycling", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<GasInfuserRecipe>> GASEOUS_INFUSION_RECIPE = RECIPE_TYPES.register("gaseous_infusion", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeType<FactocraftyMachineRecipe>> SAWMILL_RECIPE = RECIPE_TYPES.register("sawing", () -> new RecipeType<>() {});

    public static final RegistrySupplier<RecipeSerializer<SolderingCraftingRecipe>> SOLDERING_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("soldering_crafting", ()-> new SolderingCraftingRecipe.SimpleSerializer<>(SolderingCraftingRecipe::new));

    public static final RegistrySupplier<RecipeSerializer<ShapelessTagRecipe>> SHAPELESS_TAG_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("crafting_shapeless", ShapelessTagRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeSerializer<ShapedTagRecipe>> SHAPED_TAG_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("crafting_shaped", ShapedTagRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> MACERATOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("macerating", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("macerating",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> COMPRESSOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("compressing", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("compressing",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<EnricherRecipe>> ENRICHER_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("enriching", () -> new EnricherRecipe.Serializer((resourceLocation -> new EnricherRecipe("enriching",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<GasInfuserRecipe>> GAS_INFUSER_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("gaseous_infusion", () -> new GasInfuserRecipe.Serializer((resourceLocation -> new GasInfuserRecipe("gaseous_infusion",resourceLocation)),200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> RECYCLER_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("recycling", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("recycling",resourceLocation)),150));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> EXTRACTOR_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("extracting", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("extracting",resourceLocation){
        @Override
        public boolean hasFluidIngredient() {
            return true;
        }
    }),250));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> REFINING_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("refining", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("refining",resourceLocation){
        @Override
        public boolean hasFluidResult() {
            return true;
        }

        @Override
        public boolean hasFluidIngredient() {
            return true;
        }
    }),200,200));

    public static final RegistrySupplier<RecipeSerializer<FactocraftyMachineRecipe>> SAWMILL_RECIPE_SERIALIZER = RECIPE_SERIALIZER.register("sawing", () -> new FactocraftyMachineRecipe.Serializer<>((resourceLocation -> new FactocraftyMachineRecipe("sawing",resourceLocation)),200));


    public static final RegistrySupplier<Block> GENERATOR = registerFactocraftyBlockItem(()-> new GeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.GENERATOR.getName());
    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<GeneratorBlockEntity>>> GENERATOR_MENU = MENUS.register(FactocraftyBlocks.GENERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.GENERATOR_MENU.get(),id, buf.readBlockPos(), inventory)));
    public static final RegistrySupplier<BlockEntityType<GeneratorBlockEntity>> GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.GENERATOR.getName(), () -> BlockEntityType.Builder.of(GeneratorBlockEntity::new, GENERATOR.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.GENERATOR.getName())));

    public static final RegistrySupplier<Block> GEOTHERMAL_GENERATOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> new GeothermalGeneratorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> BlockEntityType.Builder.of(GeothermalGeneratorBlockEntity::new, GEOTHERMAL_GENERATOR.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName())));
    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<GeothermalGeneratorBlockEntity>>> GEOTHERMAL_GENERATOR_MENU = MENUS.register(FactocraftyBlocks.GEOTHERMAL_GENERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.GEOTHERMAL_GENERATOR_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<Block> NUCLEAR_REACTOR_CORE = registerFactocraftyBlockItem(()-> new NuclearReactorBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.NUCLEAR_REACTOR_CORE.getName());

    public static final RegistrySupplier<BlockEntityType<NuclearReactorBlockEntity>> NUCLEAR_REACTOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register("nuclear_reactor", () -> BlockEntityType.Builder.of(NuclearReactorBlockEntity::new, NUCLEAR_REACTOR_CORE.get()).build(null));
    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<NuclearReactorBlockEntity>>> NUCLEAR_REACTOR_MENU = MENUS.register("nuclear_reactor", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.NUCLEAR_REACTOR_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<Block> FLUID_PUMP = registerFactocraftyBlockItem(()-> new FluidPumpBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).destroyTime(2f)), FactocraftyBlocks.FLUID_PUMP.getName(), b-> new FactocraftyMachineBlockItem(b,fullStackItemProperties()));
    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<FluidPumpBlockEntity>>> FLUID_PUMP_MENU = MENUS.register(FactocraftyBlocks.FLUID_PUMP.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.FLUID_PUMP_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<BlockEntityType<FluidPumpBlockEntity>> FLUID_PUMP_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.FLUID_PUMP.getName(), () -> BlockEntityType.Builder.of(FluidPumpBlockEntity::new, FLUID_PUMP.get()).build(null));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<ElectricFurnaceBlockEntity>>> ELECTRIC_FURNACE_MENU = MENUS.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.ELECTRIC_FURNACE_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<CompressorBlockEntity>>> COMPRESSOR_MENU = MENUS.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.COMPRESSOR_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<MaceratorBlockEntity>>> MACERATOR_MENU = MENUS.register(FactocraftyBlocks.MACERATOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.MACERATOR_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<ChangeableInputMachineBlockEntity>>> EXTRACTOR_MENU = MENUS.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.EXTRACTOR_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<RecyclerBlockEntity>>> RECYCLER_MENU = MENUS.register(FactocraftyBlocks.RECYCLER.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.RECYCLER_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<RefinerBlockEntity>>> REFINER_MENU = MENUS.register(FactocraftyBlocks.REFINER.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.REFINER_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<EnricherBlockEntity>>> ENRICHER_MENU = MENUS.register(FactocraftyBlocks.ENRICHER.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.ENRICHER_MENU.get(),id, buf.readBlockPos(), inventory,89)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<GasInfuserBlockEntity>>> GAS_INFUSER_MENU = MENUS.register(FactocraftyBlocks.GAS_INFUSER.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.GAS_INFUSER_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<SawmillBlockEntity>>> SAWMILL_MENU = MENUS.register(FactocraftyBlocks.SAWMILL.getName(), () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.SAWMILL_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyItemMenuContainer>> RGB_MENU = MENUS.register("rgb_controller", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyItemMenuContainer(Registration.RGB_MENU.get(),id, inventory.player, buf.readBlockPos())));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<FactocraftyEnergyStorageBlockEntity>>> ENERGY_STORAGE_MENU = MENUS.register("energy_storage", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.ENERGY_STORAGE_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<FactocraftyEnergyTransformerBlockEntity>>> ENERGY_TRANSFORMER_MENU = MENUS.register("energy_transformer", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.ENERGY_TRANSFORMER_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<FactocraftyFluidTankBlockEntity>>> FLUID_TANK_MENU = MENUS.register("fluid_tank", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.FLUID_TANK_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<MenuType<FactocraftyStorageMenu<SolarPanelBlockEntity>>> SOLAR_PANEL_MENU = MENUS.register("solar_panel", () -> MenuRegistry.ofExtended((id, inventory, buf) -> new FactocraftyStorageMenu<>(Registration.SOLAR_PANEL_MENU.get(),id, buf.readBlockPos(), inventory)));

    public static final RegistrySupplier<Block> IRON_FURNACE = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.IRON_FURNACE.getName(), () -> new IronFurnace(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.IRON_FURNACE.getName(), () -> BlockEntityType.Builder.of(IronFurnaceBlockEntity::new, IRON_FURNACE.get()).build(BlockEntityUtil.blockEntityType(FactocraftyBlocks.IRON_FURNACE.getName())));

    public static final RegistrySupplier<Block> ELECTRIC_FURNACE = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> new ElectricFurnaceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<ElectricFurnaceBlockEntity>> ELECTRIC_FURNACE_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.ELECTRIC_FURNACE.getName(), () -> BlockEntityType.Builder.of(ElectricFurnaceBlockEntity::new, ELECTRIC_FURNACE.get()).build(null));



    public static final RegistrySupplier<Block> MACERATOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.MACERATOR.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)){
        public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
            hasTopSmokeParticles = true;
            super.animateTick(blockState, level, blockPos, randomSource);
        }
    });
    public static final RegistrySupplier<BlockEntityType<MaceratorBlockEntity>> MACERATOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.MACERATOR.getName(), () -> BlockEntityType.Builder.of(MaceratorBlockEntity::new, MACERATOR.get()).build(null));

    public static final RegistrySupplier<Block> COMPRESSOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.COMPRESSOR.getName(), () -> BlockEntityType.Builder.of(CompressorBlockEntity::new, COMPRESSOR.get()).build(null));

    public static final RegistrySupplier<Block> EXTRACTOR = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistrySupplier<BlockEntityType<ChangeableInputMachineBlockEntity>> EXTRACTOR_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.EXTRACTOR.getName(), () -> BlockEntityType.Builder.of(ChangeableInputMachineBlockEntity::new, EXTRACTOR.get()).build(null));

    public static final RegistrySupplier<Block> RECYCLER = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.RECYCLER.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<RecyclerBlockEntity>> RECYCLER_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.RECYCLER.getName(), () -> BlockEntityType.Builder.of(RecyclerBlockEntity::new, RECYCLER.get()).build(null));


    public static final RegistrySupplier<Block> REFINER = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.REFINER.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<RefinerBlockEntity>> REFINER_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.REFINER.getName(), () -> BlockEntityType.Builder.of(RefinerBlockEntity::new, REFINER.get()).build(null));


    public static final RegistrySupplier<Block> ENRICHER = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.ENRICHER.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<EnricherBlockEntity>> ENRICHER_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.ENRICHER.getName(), () -> BlockEntityType.Builder.of(EnricherBlockEntity::new, ENRICHER.get()).build(null));

    public static final RegistrySupplier<Block> GAS_INFUSER = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.GAS_INFUSER.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<GasInfuserBlockEntity>> GAS_INFUSER_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.GAS_INFUSER.getName(), () -> BlockEntityType.Builder.of(GasInfuserBlockEntity::new, GAS_INFUSER.get()).build(null));

    public static final RegistrySupplier<Block> SAWMILL = Registration.BLOCKS_ITEMS.register(FactocraftyBlocks.SAWMILL.getName(), () -> new FactocraftyMachineBlock(FactoryCapacityTiers.BASIC,BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<SawmillBlockEntity>> SAWMILL_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register(FactocraftyBlocks.SAWMILL.getName(), () -> BlockEntityType.Builder.of(SawmillBlockEntity::new, SAWMILL.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<ReactorCasingBlockEntity>> REACTOR_CASING_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register("reactor_casing", () -> BlockEntityType.Builder.of(ReactorCasingBlockEntity::new, REACTOR_CONTAINMENT.get(),NUCLEAR_REACTOR_CASING.get(),NUCLEAR_REACTOR_CHAMBER.get()).build(null));


    public static final RegistrySupplier<Block> LED_BLOCK = Registration.BLOCKS_ITEMS.register("led_block", () -> new FactocraftyLedBlock(BlockBehaviour.Properties.copy(Blocks.GLASS), false));

    public static final RegistrySupplier<Block> RGB_LED_BLOCK = Registration.BLOCKS_ITEMS.register("rgb_led_block", () -> new FactocraftyLedBlock(BlockBehaviour.Properties.copy(Blocks.GLASS), true));

    public static final RegistrySupplier<Block> LED_PANEL = Registration.BLOCKS_ITEMS.register("led_panel", () -> new FactocraftyLedPanel(BlockBehaviour.Properties.copy(Blocks.GLASS), false));

    public static final RegistrySupplier<Block> RGB_LED_PANEL = Registration.BLOCKS_ITEMS.register("rgb_led_panel", () -> new FactocraftyLedPanel(BlockBehaviour.Properties.copy(Blocks.GLASS), true));


    public static final RegistrySupplier<BlockEntityType<FactocraftyLedBlockEntity>> LED_BLOCK_ENTITY = Registration.BLOCK_ENTITIES.register("led", () -> BlockEntityType.Builder.of(FactocraftyLedBlockEntity::new, LED_BLOCK.get(), RGB_LED_BLOCK.get(),LED_PANEL.get(), RGB_LED_PANEL.get()).build(null));


    public static final RegistrySupplier<Item> OVERCLOCK_UPGRADE = ITEMS.register("overclock_unit", ()-> new MachineUpgradeItem(fullStackItemProperties(), UpgradeType.OVERCLOCK){});

    public static final RegistrySupplier<Item> TRANSFORMER_UPGRADE = ITEMS.register("transformer_unit", ()-> new MachineUpgradeItem(fullStackItemProperties().stacksTo(4), UpgradeType.TRANSFORMER));

    public static final RegistrySupplier<Item> ENERGY_UPGRADE = ITEMS.register("crafty_unit", ()-> new MachineUpgradeItem(fullStackItemProperties(), UpgradeType.ENERGY));

    public static final RegistrySupplier<Item> WRENCH =  ITEMS.register("wrench", () -> new WrenchItem(defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CUTTER =  ITEMS.register("cutter", () -> new CraftingToolItem(defaultStackItemProperties().durability(300)));

    public static final RegistrySupplier<Item> HAMMER =  ITEMS.register("hammer", () -> new CraftingToolItem(defaultStackItemProperties().durability(375)));

    public static final RegistrySupplier<Item> SOLDERING_IRON =  ITEMS.register("soldering_iron", () -> new ElectricCraftingToolItem(FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CONTROL_ROD =  ITEMS.register("control_rod", () -> new Item(defaultStackItemProperties()));
    public static final RegistrySupplier<Item> URANIUM_ROD =  ITEMS.register("uranium_rod", () -> new Item(fullStackItemProperties()));
    public static final RegistrySupplier<Item> CONTROL_URANIUM_ROD =  ITEMS.register("control_uranium_rod", () -> new Item(defaultStackItemProperties().durability(1024)));
    public static final RegistrySupplier<Item> DUAL_URANIUM_ROD =  ITEMS.register("dual_uranium_rod", () -> new Item(defaultStackItemProperties().durability(2048)));
    public static final RegistrySupplier<Item> TRIPLE_URANIUM_ROD =  ITEMS.register("triple_uranium_rod", () -> new Item(defaultStackItemProperties().durability(3072)));
    public static final RegistrySupplier<Item> QUADRUPLE_URANIUM_ROD =  ITEMS.register("quadruple_uranium_rod", () -> new Item(defaultStackItemProperties().durability(4096)));
    public static final RegistrySupplier<Item> SEXTRUPLE_URANIUM_ROD =  ITEMS.register("sextruple_uranium_rod", () -> new Item(defaultStackItemProperties().durability(6144)));
    public static final RegistrySupplier<RGBControllerItem> RGB_CONTROLLER =  ITEMS.register("rgb_led_controller", () -> new RGBControllerItem(defaultStackItemProperties()));

    public static final RegistrySupplier<Item> DRILL =  ITEMS.register("mining_drill", () -> new DrillItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> DIAMOND_DRILL =  ITEMS.register("diamond_drill", () -> new DrillItem(Tiers.DIAMOND,1, -2.8F, FactoryCapacityTiers.ADVANCED,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> CHAINSAW =  ITEMS.register("chainsaw", () -> new ChainsawItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> DIAMOND_CHAINSAW =  ITEMS.register("diamond_chainsaw", () -> new ChainsawItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.ADVANCED,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> ELECTRIC_HOE =  ITEMS.register("electric_hoe", () -> new ElectricHoeItem(Tiers.IRON,1, -2.8F, FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> ELECTRIC_WRENCH =  ITEMS.register("electric_wrench", () -> new ElectricWrenchItem(FactoryCapacityTiers.BASIC,defaultStackItemProperties()));

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

    public static final RegistrySupplier<Block> RUBBER_FENCE_GATE = registerFactocraftyBlockItem(()-> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_FENCE_GATE), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_FENCE_GATE.getName());

    public static final RegistrySupplier<Block> RUBBER_DOOR = registerFactocraftyBlockItem(()-> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_DOOR), BlockSetType.MANGROVE), FactocraftyBlocks.RUBBER_DOOR.getName());

    public static final RegistrySupplier<Block> RUBBER_TRAPDOOR = registerFactocraftyBlockItem(()-> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_TRAPDOOR), BlockSetType.MANGROVE), FactocraftyBlocks.RUBBER_TRAPDOOR.getName());

    public static final RegistrySupplier<Block> RUBBER_PRESSURE_PLATE = registerFactocraftyBlockItem(()-> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS), BlockSetType.MANGROVE), FactocraftyBlocks.RUBBER_PRESSURE_PLATE.getName());

    public static final RegistrySupplier<Block> RUBBER_BUTTON = registerFactocraftyBlockItem(()-> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY),BlockSetType.MANGROVE,30,true), FactocraftyBlocks.RUBBER_BUTTON.getName());

    public static final RegistrySupplier<Block> RUBBER_SIGN = registerFactocraftyBlock(()-> new RubberSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_SIGN.getName());
    public static final RegistrySupplier<Block> RUBBER_WALL_SIGN = registerFactocraftyBlock(()-> new RubberWallSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_WALL_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_WALL_SIGN.getName());
    public static final RegistrySupplier<Block> RUBBER_HANGING_SIGN = registerFactocraftyBlock(()-> new RubberCeilingHangingSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_HANGING_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_HANGING_SIGN.getName());
    public static final RegistrySupplier<Block> RUBBER_WALL_HANGING_SIGN = registerFactocraftyBlock(()-> new RubberWallHangingSign(BlockBehaviour.Properties.copy(Blocks.MANGROVE_WALL_HANGING_SIGN), FactocraftyWoodType.RUBBER), FactocraftyBlocks.RUBBER_WALL_HANGING_SIGN.getName());

    public static final RegistrySupplier<Item> RUBBER_SIGN_ITEM = ITEMS.register("rubber_sign", () -> new SignItem(fullStackItemProperties(), RUBBER_SIGN.get(), RUBBER_WALL_SIGN.get()));

    public static final RegistrySupplier<Item> RUBBER_HANGING_SIGN_ITEM = ITEMS.register("rubber_hanging_sign", () -> new HangingSignItem( RUBBER_HANGING_SIGN.get(), RUBBER_WALL_HANGING_SIGN.get(),fullStackItemProperties()));
    public static final RegistrySupplier<BlockEntityType<RubberSignBlockEntity>> RUBBER_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.RUBBER_SIGN.getName(), () -> BlockEntityType.Builder.of(RubberSignBlockEntity::new, RUBBER_SIGN.get(), RUBBER_WALL_SIGN.get()).build(null));

    public static final RegistrySupplier<BlockEntityType<RubberHangingSignBlockEntity>> RUBBER_HANGING_SIGN_BLOCK_ENTITY = BLOCK_ENTITIES.register(FactocraftyBlocks.RUBBER_HANGING_SIGN.getName(), () -> BlockEntityType.Builder.of(RubberHangingSignBlockEntity::new, RUBBER_HANGING_SIGN.get(), RUBBER_WALL_HANGING_SIGN.get()).build(null));

    public static final RegistrySupplier<Block> RUBBER_TREE_SAPLING = BLOCKS_ITEMS.register( "rubber_sapling", () -> new SaplingBlock(new RubberTreeGrower(), BlockBehaviour.Properties.copy(Blocks.ACACIA_SAPLING)));

    public static final RegistrySupplier<Item> STICKY_RESIN = registerFactocraftyItem( "sticky_resin");

    public static final RegistrySupplier<Item> ENERGIUM_DUST = registerFactocraftyItem( "energium_dust");


    public static final RegistrySupplier<Item> NIGHT_VISION_GOGGLES = ITEMS.register("night_vision_goggles", ()-> new ElectricArmorItem(FactoryCapacityTiers.BASIC, 28,FactocraftyArmorMaterials.NIGHT_VISION,ArmorItem.Type.HELMET,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_HELMET = ITEMS.register("graphano_helmet", ()-> new GraphanoArmorItem(ArmorItem.Type.HELMET,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_CHEST = ITEMS.register("graphano_chestplate", ()-> new GraphanoArmorItem(ArmorItem.Type.CHESTPLATE,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_LEGGINGS = ITEMS.register("graphano_leggings", ()-> new GraphanoArmorItem(ArmorItem.Type.LEGGINGS,defaultStackItemProperties()));
    public static final RegistrySupplier<Item> GRAFANO_BOOTS = ITEMS.register("graphano_boots", ()-> new GraphanoArmorItem(ArmorItem.Type.BOOTS,defaultStackItemProperties()));

    public static final RegistrySupplier<Item> QUANTUM_CHEST = ITEMS.register("quantum_chestplating", ()-> new QuantumArmorItem(ArmorItem.Type.CHESTPLATE,defaultStackItemProperties()));

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

    public static final RegistrySupplier<Item> CORRUPTED_ENDERMAN_SPAWN_EGG = ITEMS.register("corrupted_enderman_spawn_egg",()-> new ArchitecturySpawnEggItem(CORRUPTED_ENDERMAN,1310994, 0,fullStackItemProperties()));

    public static final RegistrySupplier<EntityType<LaserProjectile>> LASER = ENTITY_TYPES.register("laser", ()-> EntityType.Builder.<LaserProjectile>of((LaserProjectile::new),MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(getModResource("laser").toString()));

    public static final RegistrySupplier<FoliagePlacerType<?>> RUBBER_TREE_FOLIAGE_PLACER = FOLIAGE_PLACERS.register("rubber_fancy_foliage_placer", ()-> new FoliagePlacerType<>(RubberTreeFoliagePlacer.CODEC));


    public static final RegistrySupplier<BasinFeature> BASIN_FEATURE = FEATURES.register("basin_feature", ()-> new BasinFeature(BasinFeature.Configuration.CODEC));


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

    private static void registerAllOreDerivatives(FactocraftyOre.Material oreMaterials) {
        oreMaterials.getDerivative("ore").ifPresent((e)-> {
            if (e instanceof FactocraftyOre.OreDerivative ore) {
                if (ore.common)
                    registrarFactocraftyBlockItem(() -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_ORE), ConstantInt.of((int) (0.6*ore.averageXp))), oreMaterials.getOreName(false));
                if (ore.deep)
                    registrarFactocraftyBlockItem(() -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE),ConstantInt.of(ore.averageXp)), oreMaterials.getOreName(true));
            };});
        oreMaterials.getDerivative(FactocraftyOre.INGOT).ifPresent(d->  registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())), d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.COMMON_DROP).ifPresent(d-> registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())), d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.CRUSHED).ifPresent(d->  registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())), d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.ROD).ifPresent(d-> registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())),d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.PLATE).ifPresent(d-> registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())),  d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.BLOCK).ifPresent(d-> registrarFactocraftyBlockItem(() -> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).mapColor(oreMaterials.getColor())), d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.REFINED).ifPresent(d->registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())),   d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.DUST).ifPresent(d-> registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())),  d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.NUGGET).ifPresent(d-> registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())), d.getName(oreMaterials)));
        oreMaterials.getDerivative(FactocraftyOre.RAW).ifPresent(d-> {
            registrarFactocraftyBlockItem(() -> new FactocraftyBlock(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).mapColor(oreMaterials.getColor())), d.getName(oreMaterials,1));
            registrarFactocraftyItem(() -> new Item(fullStackItemProperties().rarity(oreMaterials.getRarity())), d.getName(oreMaterials,0));
        });
        if (oreMaterials.getArmor() != null ) {
            registrarFactocraftyItem(() -> new ArmorItem(oreMaterials.getArmor(), ArmorItem.Type.BOOTS, defaultStackItemProperties().rarity(oreMaterials.getRarity())), oreMaterials.getName() + "_boots");
            registrarFactocraftyItem(() -> new ArmorItem(oreMaterials.getArmor(), ArmorItem.Type.LEGGINGS, defaultStackItemProperties().rarity(oreMaterials.getRarity())), oreMaterials.getName() + "_leggings");
            registrarFactocraftyItem(() -> new ArmorItem(oreMaterials.getArmor(), ArmorItem.Type.CHESTPLATE, defaultStackItemProperties().rarity(oreMaterials.getRarity())), oreMaterials.getName() + "_chestplate");
            registrarFactocraftyItem(() -> new ArmorItem(oreMaterials.getArmor(), ArmorItem.Type.HELMET, defaultStackItemProperties().rarity(oreMaterials.getRarity())), oreMaterials.getName() + "_helmet");
        }
        if (oreMaterials.getToolTier() != null){
            registrarFactocraftyItem(() -> new SwordItem(oreMaterials.getToolTier(),3, -2.4F , defaultStackItemProperties().rarity(oreMaterials.getRarity())),oreMaterials.getName() + "_sword");
            registrarFactocraftyItem(() -> new PickaxeItem(oreMaterials.getToolTier(),1, -2.8F , defaultStackItemProperties().rarity(oreMaterials.getRarity())),oreMaterials.getName() + "_pickaxe");
            registrarFactocraftyItem(() -> new AxeItem(oreMaterials.getToolTier(),6.0F, -3.1F , defaultStackItemProperties().rarity(oreMaterials.getRarity())),oreMaterials.getName() + "_axe");
            registrarFactocraftyItem(() -> new ShovelItem(oreMaterials.getToolTier(),1.5F, -3.0F , defaultStackItemProperties().rarity(oreMaterials.getRarity())),oreMaterials.getName() + "_shovel");
            registrarFactocraftyItem(() -> new HoeItem(oreMaterials.getToolTier(),-3, 0.0F ,defaultStackItemProperties().rarity(oreMaterials.getRarity())),oreMaterials.getName() + "_hoe");
        }
    }
    private static void registerCable(FactocraftyCables cable) {
        if (cable.cableShape == FactocraftyCables.Shape.SOLID){
            registrarFactocraftyBlockItem(() -> new SolidCableBlock(cable, cableBehaviour()), cable.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(cable.getName()), () -> BlockEntityType.Builder.of(SolidCableBlockEntity::new, cable.get()).build(null));
        }
        else {
            registrarFactocraftyBlockItem(() ->  new CableBlock(cable, cableBehaviour()), cable.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(cable.getName()), () -> BlockEntityType.Builder.of(CableBlockEntity::new, cable.get()).build(null));
        }
    }
    private static void registerFluidPipe(FactocraftyFluidPipes pipe) {
        registrarFactocraftyBlockItem(() ->  new FluidPipeBlock(pipe,BlockBehaviour.Properties.copy(Blocks.GLASS)), pipe.getName());
        BLOCK_ENTITIES_REGISTRAR.register(getModResource(pipe.getName()), () -> BlockEntityType.Builder.of(FluidPipeBlockEntity::new, pipe.get()).build(null));
    }
    private static void registerSolarPanel(SolarPanelTiers tier) {
        RegistrySupplier<Block> b = registrarFactocraftyBlockItem(() -> tier.ordinal() <= 1 ? new FlexibleSolarPanelBlock(tier,BlockBehaviour.Properties.copy(Blocks.BROWN_STAINED_GLASS_PANE)) : new SolarPanelBlock(tier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), tier.getName());
        BLOCK_ENTITIES_REGISTRAR.register(getModResource(tier.getName()), () -> BlockEntityType.Builder.of((bp, bs) -> new SolarPanelBlockEntity(tier, bp, bs), b.get()).build(null));
    }
    private static void registerEnergyStorage(FactocraftyEnergyStorages storage) {

            registrarFactocraftyBlock(() -> new FactocraftyEnergyStorageBlock(storage.capacityTier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), storage.getName());
            registrarFactocraftyItem(()-> new EnergyBlockItem(storage.get(),storage.capacityTier, TransportState.EXTRACT_INSERT, fullStackItemProperties()), storage.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(storage.getName()), () -> BlockEntityType.Builder.of(FactocraftyEnergyStorageBlockEntity::new, storage.get()).build(null));

    }
    private static void registerEnergyTransformer(FactocraftyEnergyTransformers storage) {
        registrarFactocraftyBlockItem(() -> new FactocraftyEnergyTransformerBlock(storage.capacityTier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), storage.getName());
        BLOCK_ENTITIES_REGISTRAR.register(getModResource(storage.getName()), () -> BlockEntityType.Builder.of(FactocraftyEnergyTransformerBlockEntity::new, storage.get()).build(null));

    }
    private static void registerFluidTank(FactocraftyFluidTanks tank) {
            registrarFactocraftyBlock(() -> new FactocraftyFluidTankBlock(tank.capacityTier, BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)), tank.getName());
            registrarFactocraftyItem(()-> new FluidTankItem(tank.get(), fullStackItemProperties()), tank.getName());
            BLOCK_ENTITIES_REGISTRAR.register(getModResource(tank.getName()), () -> BlockEntityType.Builder.of((bp, bs) -> new FactocraftyFluidTankBlockEntity(tank.capacityTier, bp, bs), tank.get()).build(null));

    }
    private static BlockBehaviour.Properties cableBehaviour(){return  BlockBehaviour.Properties.of().sound(CABLE);}

    private static void registerFluid(String name, int viscosity, int density, int tick, int color,boolean colorInLevel,  BlockBehaviour.Properties properties, FactocraftyFlowingFluid.whenSpreadToFluid whenSpreadToFluid){
        boolean isGas = density <= 0;
        ArchitecturyFluidAttributes FLUID_ATTRIBUTE = FactocraftyFluidAttributes.of(()-> getRegistrarFluidEntry("flowing_" + name), ()-> getRegistrarFluidEntry(name)).colorInLevel(color,colorInLevel).lighterThanAir(isGas).blockSupplier(()-> (RegistrySupplier<? extends LiquidBlock>) GENERIC_BLOCKS_REGISTRAR.delegate(getModResource(name))).bucketItem(() -> Optional.ofNullable(isGas ? null : ITEMS_REGISTRAR.get(getModResource(name + "_bucket")))).viscosity(viscosity).density(density).flowingTexture(new ResourceLocation(MOD_ID,"block/fluid/" + (isGas? "generic_gas":  name)+ "_flow")).sourceTexture(new ResourceLocation(MOD_ID, "block/fluid/" + (isGas? "generic_gas":  name) + "_still")).overlayTexture(new ResourceLocation(MOD_ID, "block/fluid/" + (isGas? "generic_gas":  name) + "_still")).tickDelay(tick);

        FLUIDS.getRegistrar().register(getModResource(name),() -> new FactocraftySourceFluid(FLUID_ATTRIBUTE, whenSpreadToFluid, isGas));
        FLUIDS.getRegistrar().register(getModResource("flowing_" + name),() -> new FactocraftyFlowingFluid(FLUID_ATTRIBUTE, whenSpreadToFluid,isGas));
        BLOCKS_REGISTRAR.register(getModResource(name), () -> new FactocraftyFluidBlock(()-> (FlowingFluid) getRegistrarFluidEntry(name), properties, isGas));
        if (!isGas)ITEMS_REGISTRAR.register( getModResource( name+ "_bucket"), () -> new ArchitecturyBucketItem(()-> getRegistrarFluidEntry(name), defaultStackItemProperties() ));

    }
    private static void registerFluid(String name, int viscosity, int density, int tick, int color, BlockBehaviour.Properties properties){
       registerFluid(name, viscosity, density, tick, color, true, properties, null);
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
    private static RegistrySupplier<Block> registerFactocraftyBlockItem(Supplier<Block> block, String name, Function<Block,BlockItem> blockItem){
        RegistrySupplier<Block> supplier = registerFactocraftyBlockItem(block,name);
        blockItemOverrides.put(supplier.getId(), blockItem);
        return supplier;
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
        SimpleFluidLoggedBlock.BLOCK_LOGGABLE_FLUIDS_SUPPLIER.addAll(List.of(FactocraftyFluids.COOLANT, FactocraftyFluids.PETROLEUM,FactocraftyFluids.OXYGEN,FactocraftyFluids.HYDROGEN,FactocraftyFluids.ISOPRENE,FactocraftyFluids.NAPHTHA,FactocraftyFluids.WATER_VAPOR));
        SOUNDS.register();

        BLOCKS_ITEMS.register();
        BLOCKS_ITEMS.forEach((blockEntry) -> {ITEMS_REGISTRAR.register(blockEntry.getId(), () -> blockItemOverrides.getOrDefault(blockEntry.getId(),b-> new BlockItem(b,fullStackItemProperties())).apply(blockEntry.get()));});
        FactocraftyFlowingFluid.whenSpreadToFluid spreadFlammable = (levelAccessor, blockPos, blockState, direction, fluidState) -> {
            FluidState fluidState2 = levelAccessor.getFluidState(blockPos);
            if ((fluidState.is(Fluids.PETROLEUM) || fluidState.is(Fluids.GASOLINE)) && ((fluidState2.is(FluidTags.LAVA) || levelAccessor.getBlockState(blockPos).is(BlockTags.FIRE)))) {
                if (levelAccessor instanceof Level level && level.random.nextFloat() >= 0.6) {
                    level.explode(null,level.damageSources().onFire(),null, blockPos.getCenter(),  (fluidState.is(Fluids.GASOLINE) ? 1.4F : 1.0F) * 1.8F, fluidState.getValue(FlowingFluid.LEVEL) > 4, Level.ExplosionInteraction.BLOCK);
                    level.removeBlock(blockPos,false);
                    return true;
                }
            }
            return false;
        };
        registerFluid(FactocraftyFluids.PETROLEUM.getName(),10000,8000,15,new Color(16, 16, 16).getRGB(),false, FactocraftyBlockProperties.PETROLEUM, spreadFlammable);
        registerFluid(FactocraftyFluids.LATEX.getName(),6000,6000,9,0xFFFFFFFF, FactocraftyBlockProperties.LATEX);
        registerFluid(FactocraftyFluids.COOLANT.getName(),4000,4000,4,new Color(0, 89, 93).getRGB(), FactocraftyBlockProperties.COOLANT);
        registerFluid(FactocraftyFluids.GASOLINE.getName(),6000,4000,6,new Color(140, 96, 0).getRGB(),true, FactocraftyBlockProperties.GASOLINE, spreadFlammable);
        registerFluid(FactocraftyFluids.NAPHTHA.getName(),4000,3000,6,new Color(155, 96, 0).getRGB(),true, FactocraftyBlockProperties.GASOLINE, spreadFlammable);
        registerFluid(FactocraftyFluids.ISOPRENE.getName(),3000,2000,2,new Color(206, 206, 206).getRGB(), FactocraftyBlockProperties.getLiquidProperties());
        registerFluid(FactocraftyFluids.METHANE.getName(),4000,0,2,new Color(132, 176, 255).getRGB(),true, FactocraftyBlockProperties.getGasProperties(), spreadFlammable);
        registerFluid(FactocraftyFluids.WATER_VAPOR.getName(),4000,0,2,new Color(140, 145, 176).getRGB(),true, FactocraftyBlockProperties.getGasProperties(), null);
        registerFluid(FactocraftyFluids.OXYGEN.getName(),2000,0,2,new Color(125, 190, 210).getRGB(),true, FactocraftyBlockProperties.getGasProperties(), null);
        registerFluid(FactocraftyFluids.HYDROGEN.getName(),3000,0,2,new Color(132, 146, 234).getRGB(),true, FactocraftyBlockProperties.getGasProperties(), spreadFlammable);
        MENUS.register();
        FLUIDS.register();
        BLOCKS.register();
        ENTITY_TYPES.register();
        ITEMS.register();
        for (SolarPanelTiers tier : SolarPanelTiers.values()) registerSolarPanel(tier);
        for (FactocraftyOre.Material material : FactocraftyOre.Material.values()) registerAllOreDerivatives(material);
        for (FactocraftyEnergyStorages storage : FactocraftyEnergyStorages.values()) registerEnergyStorage(storage);
        for (FactocraftyEnergyTransformers transformer : FactocraftyEnergyTransformers.values()) registerEnergyTransformer(transformer);
        for (FactocraftyFluidTanks tank : FactocraftyFluidTanks.values()) registerFluidTank(tank);
        for (FactocraftyCables tier : FactocraftyCables.values()) registerCable(tier);
        for (FactocraftyFluidPipes tier : FactocraftyFluidPipes.values()) registerFluidPipe(tier);
        RegistrarBlockItems.forEach((blockEntry) -> {ITEMS_REGISTRAR.register(blockEntry, () -> new BlockItem(Objects.requireNonNull(BLOCKS_REGISTRAR.get(blockEntry)), fullStackItemProperties()));});
        RECIPE_TYPES.register();
        RECIPE_SERIALIZER.register();
        BLOCK_ENTITIES.register();
        FOLIAGE_PLACERS.register();
        FEATURES.register();
        TABS.register();
    }
}
