package wily.factocrafty.util.registering;

import net.minecraft.world.level.material.Fluid;
import wily.factocrafty.init.Registration;

public enum FactocraftyFluids implements IFactocraftyLazyRegister<Fluid>{
    COOLANT,FLOWING_COOLANT,LATEX,FLOWING_LATEX,PETROLEUM,FLOWING_PETROLEUM;

    @Override
    public Fluid get() {
        return Registration.getRegistrarFluidEntry(getName());
    }
}
