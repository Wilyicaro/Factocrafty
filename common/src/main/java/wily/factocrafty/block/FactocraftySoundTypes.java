package wily.factocrafty.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

import static wily.factocrafty.init.Registration.*;

public class FactocraftySoundTypes {

    public static final SoundType CABLE = new SoundType(1.0F, 1.0F, CABLE_BROKEN.get(), SoundEvents.WOOL_STEP, CABLE_PLACE.get(), CABLE_DIG.get(), SoundEvents.WOOL_FALL);

}
