package wily.factocrafty.util.registering;

import net.minecraft.world.level.block.Block;
import wily.factocrafty.init.Registration;

public enum FactocraftyBlocks implements IFactocraftyLazyRegistry<Block> {
    RUBBER_LOG, RUBBER_WOOD, STRIPPED_RUBBER_LOG, STRIPPED_RUBBER_WOOD,RUBBER_LEAVES,RUBBER_PLANKS, RUBBER_SLAB,RUBBER_STAIRS,RUBBER_FENCE,RUBBER_FENCE_GATE,RUBBER_DOOR, RUBBER_TRAPDOOR,RUBBER_PRESSURE_PLATE,RUBBER_BUTTON,RUBBER_SIGN,RUBBER_WALL_SIGN,RUBBER_HANGING_SIGN,RUBBER_WALL_HANGING_SIGN,TREETAP,
    MACHINE_FRAME,ADVANCED_MACHINE_FRAME,NUCLEAR_REACTOR_CASING,NUCLEAR_REACTOR_CHAMBER,REACTOR_CONTAINMENT,REACTOR_GLASS,IRON_FURNACE,ELECTRIC_FURNACE,MACERATOR,COMPRESSOR,EXTRACTOR,RECYCLER,REFINER,ENRICHER,GAS_INFUSER,SAWMILL,FLUID_PUMP,
    GEOTHERMAL_GENERATOR,GENERATOR,NUCLEAR_REACTOR_CORE,
    AMORPHOUS_SOLAR_PANEL,ORGANIC_SOLAR_PANEL,POLY_BASIC_SOLAR_PANEL,MONO_ADVANCED_SOLAR_PANEL,HYBRID_SOLAR_PANEL,ULTIMATE_SOLAR_PANEL,QUANTUM_SOLAR_PANEL,
    TIN_CABLE,INSULATED_TIN_CABLE,COPPER_CABLE,INSULATED_COPPER_CABLE,GOLD_CABLE,INSULATED_GOLD_CABLE,SILVER_CABLE,INSULATED_SILVER_CABLE,CRYSTAL_CABLE,
    TIN_BLOCK,TIN_ORE,DEEPSLATE_TIN_ORE,RAW_TIN_BLOCK,
    PLATINUM_BLOCK,PLATINUM_ORE,RAW_PLATINUM_BLOCK,
    BRONZE_BLOCK,BRONZE_ORE,DEEPSLATE_BRONZE_ORE,RAW_BRONZE_BLOCK,
    URANIUM_BLOCK,URANIUM_ORE,DEEPSLATE_URANIUM_ORE,RAW_URANIUM_BLOCK,
    LEAD_BLOCK,LEAD_ORE,DEEPSLATE_LEAD_ORE,RAW_LEAD_BLOCK,
    RUBY_BLOCK,RUBY_ORE,DEEPSLATE_RUBY_ORE,RAW_RUBY_BLOCK;

    public Block get(){
        return Registration.getRegistrarBlockEntry(getName());
    }
}
