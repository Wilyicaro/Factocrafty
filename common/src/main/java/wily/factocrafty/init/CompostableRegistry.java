package wily.factocrafty.init;

import net.minecraft.world.level.block.ComposterBlock;


public class CompostableRegistry {

    public static void bootStrap(){
        ComposterBlock.add(0.3f, Registration.RUBBER_LEAVES.get());
    }
}
