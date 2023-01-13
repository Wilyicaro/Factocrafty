package wily.factocrafty.block.generator;

import net.minecraft.world.level.block.entity.BlockEntityType;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.Objects;

public enum SolarPanelTiers {
    AMORPHOUS(FactoryCapacityTiers.BASIC,0.08, 1.5),ORGANIC(FactoryCapacityTiers.BASIC,0.12, 1.5),
    POLY_BASIC(FactoryCapacityTiers.BASIC,0.17, 2), MONO_ADVANCED(FactoryCapacityTiers.ADVANCED,0.18,8),
    HYBRID(FactoryCapacityTiers.HIGH,0.23,12),ULTIMATE(FactoryCapacityTiers.ULTIMATE,0.27,16),QUANTUM(FactoryCapacityTiers.QUANTUM,0.32,16);

    public final double efficiency;
    public final double heightSize;

    public final FactoryCapacityTiers energyTier;

    SolarPanelTiers(FactoryCapacityTiers energyTier, double efficiency, double heightSize){
        this.energyTier = energyTier;
        this.efficiency = efficiency;
        this.heightSize = heightSize;
    }
    public String getName(){
        return name().toLowerCase() + "_solar_panel";
    }

    public BlockEntityType<?> getBlockEntity(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntityEntry(getName()), "There is no solar panel with that name");
    }
}
