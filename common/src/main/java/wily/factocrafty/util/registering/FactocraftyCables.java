package wily.factocrafty.util.registering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.VoxelShape;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum FactocraftyCables implements IFactocraftyLazyRegistry<Block> {
    TIN_CABLE(FactoryCapacityTiers.BASIC,0,Shape.COMMON),INSULATED_TIN_CABLE(FactoryCapacityTiers.BASIC,1.0F,Shape.INSULATED),
    COPPER_CABLE(FactoryCapacityTiers.ADVANCED,0,Shape.COMMON), INSULATED_COPPER_CABLE(FactoryCapacityTiers.ADVANCED,1.0F,Shape.INSULATED),
    GOLD_CABLE(FactoryCapacityTiers.HIGH,0,Shape.THIN),INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,0.6F,Shape.INSULATED_THIN),MEDIUM_INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,0.8F,Shape.COMMON),HIGH_INSULATED_GOLD_CABLE(FactoryCapacityTiers.HIGH,1.0F,Shape.INSULATED),
    SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,0,Shape.THIN), INSULATED_SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,0.6F,Shape.INSULATED_THIN),MEDIUM_INSULATED_SILVER_CABLE(FactoryCapacityTiers.HIGH,0.8F,Shape.COMMON), HIGH_INSULATED_SILVER_CABLE(FactoryCapacityTiers.ULTIMATE,1.0F,Shape.INSULATED),
    CRYSTAL_CABLE(FactoryCapacityTiers.QUANTUM,1.0F,Shape.SOLID);

    public final FactoryCapacityTiers energyTier;
    public final float insulation;

    public final Shape cableShape;



    FactocraftyCables(FactoryCapacityTiers energyTier, float insulation, Shape cableShape){
        this.energyTier = energyTier;
        this.insulation = insulation;
        this.cableShape = cableShape;
    }


    public BlockEntityType<?> getBlockEntity(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntityEntry(getName()), "There is no cable with that name");
    }
    public Block get(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntry(getName()), "There is no cable with that name");
    }

    public List<FactocraftyCables> getSupportedCables(FactoryCapacityTiers capacityTier){
        List<FactocraftyCables> list = new ArrayList<>();
        for (FactocraftyCables tier : FactocraftyCables.values()){
            if (tier.energyTier.ordinal() <= capacityTier.ordinal())list.add(tier);
        }
        return  list;
    }
    public double transferenceEfficiency() {
        return Math.min(Math.pow(energyTier.getConductivity(), 0.16 / (insulation + 1)), 0.99);
    }

    public enum Shape{
        COMMON(Block.box(6, 0, 0, 10, 4, 6),Block.box(6, 4, 0, 10, 20, 4)),
        INSULATED(Block.box(5.7, 0, 0, 10.3, 4.6, 6),Block.box(5.7, 4.6, 0, 10.3, 20.6, 4.6)),
        THIN(Block.box(6.4, 0.4, 0, 9.6, 3.6, 6),Block.box(6.4, 3.6, 0, 9.6, 19.6, 3.2)),
        INSULATED_THIN(Block.box(6.1, 0.1, 0, 9.9, 3.9, 6),Block.box(6.1, 3.9, 0, 9.9, 19.9, 3.8)),
        SOLID(Block.box(6, 6, 0, 10, 10, 6));

        public final VoxelShape[] shapes;

        Shape(VoxelShape... sideShape){
            this.shapes = sideShape;
        }

    }
}
