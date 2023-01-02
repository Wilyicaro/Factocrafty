package wily.factocrafty.block.cable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.VoxelShape;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum CableTiers {
    TIN(FactoryCapacityTiers.BASIC,true),COPPER(FactoryCapacityTiers.ADVANCED,true),
    GOLD(FactoryCapacityTiers.HIGH,true), SILVER(FactoryCapacityTiers.ULTIMATE,true),
    CRYSTAL(FactoryCapacityTiers.QUANTUM,false);

    public final FactoryCapacityTiers energyTier;
    public final boolean hasInsulated;

    public VoxelShape sideShape = Shape.COMMON.sideShape;

    public VoxelShape insulatedSideShape = Shape.INSULATED.sideShape;

    public VoxelShape upShape = Shape.COMMON.upShape;

    public VoxelShape insulatedUpShape = Shape.INSULATED.upShape;

    static {
        GOLD.sideShape = Shape.THIN.sideShape;
        SILVER.sideShape = Shape.THIN.sideShape;
        GOLD.upShape = Shape.THIN.upShape;
        SILVER.upShape = Shape.THIN.upShape;
        GOLD.insulatedSideShape = Shape.INSULATED_THIN.sideShape;
        SILVER.insulatedSideShape = Shape.INSULATED_THIN.sideShape;
        GOLD.insulatedUpShape = Shape.INSULATED_THIN.upShape;
        SILVER.insulatedUpShape = Shape.INSULATED_THIN.upShape;
        CRYSTAL.sideShape = Shape.SOLID.sideShape;
    }
    CableTiers(FactoryCapacityTiers energyTier, boolean hasInsulated){
        this.energyTier = energyTier;
        this.hasInsulated = hasInsulated;


    }
    public String getName(){
        return name().toLowerCase() + "_cable";
    }

    public BlockEntityType<?> getBlockEntity(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntityEntry(getName()), "There is no cable with that name");
    }
    public Block getBlock(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntry(getName()), "There is no cable with that name");
    }
    public Block getInsulatedBlock(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntry("insulated_" + getName()), "There is no cable with that name");
    }

    public List<CableTiers> getSupportedCables(FactoryCapacityTiers capacityTier){
        List<CableTiers> list = new ArrayList<>();
        for (CableTiers tier : CableTiers.values()){
            if (tier.energyTier.ordinal() <= capacityTier.ordinal())list.add(tier);
        }
        return  list;
    }

    public enum Shape{
        COMMON(Block.box(6, 0, 0, 10, 4, 6),Block.box(6, 4, 0, 10, 20, 4)),
        INSULATED(Block.box(5.7, 0, 0, 10.3, 4.6, 6),Block.box(5.7, 4.6, 0, 10.3, 20.6, 4.6)),
        THIN(Block.box(6.4, 0.4, 0, 9.6, 3.6, 6),Block.box(6.4, 3.6, 0, 9.6, 19.6, 3.2)),
        INSULATED_THIN(Block.box(6.1, 0.1, 0, 9.9, 3.9, 6),Block.box(6.1, 3.9, 0, 9.9, 19.9, 3.8)),
        SOLID(Block.box(6, 6, 0, 10, 10, 6));

        public final VoxelShape sideShape;
        public final VoxelShape upShape;

        Shape(VoxelShape sideShape, VoxelShape upShape){
            this.sideShape = sideShape;
            this.upShape = upShape;
        }
        Shape(VoxelShape sideShape){
            this.sideShape = sideShape;
            this.upShape = null;
        }

    }
}
