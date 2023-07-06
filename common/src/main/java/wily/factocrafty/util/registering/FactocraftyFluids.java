package wily.factocrafty.util.registering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import wily.factocrafty.init.Registration;

public enum FactocraftyFluids implements IFactocraftyLazyRegistry<Fluid> {
    COOLANT,FLOWING_COOLANT,LATEX,FLOWING_LATEX,PETROLEUM,FLOWING_PETROLEUM,GASOLINE,FLOWING_GASOLINE,NAPHTHA,FLOWING_NAPHTHA,METHANE,FLOWING_METHANE,ISOPRENE,FLOWING_ISOPRENE,OXYGEN,FLOWING_OXYGEN,HYDROGEN,FLOWING_HYDROGEN,WATER_VAPOR,FLOWING_WATER_VAPOR;

    @Override
    public Fluid get() {
        return Registration.getRegistrarFluidEntry(getName());
    }

    public Block getBlock() {
        return Registration.getRegistrarBlockEntry(getName());
    }
}
