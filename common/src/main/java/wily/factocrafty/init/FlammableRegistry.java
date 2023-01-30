package wily.factocrafty.init;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;


public class FlammableRegistry {



    public static void bootStrap(){

        FireBlock fireBlock = (FireBlock) Blocks.FIRE;
        fireBlock.setFlammable(Registration.RUBBER_LOG.get(), 5, 20);
        fireBlock.setFlammable(Registration.RUBBER_SLAB.get(), 5, 20);
        fireBlock.setFlammable(Registration.RUBBER_FENCE.get(), 5, 20);
        fireBlock.setFlammable(Registration.RUBBER_FENCE_GATE.get(), 5, 20);
        fireBlock.setFlammable(Registration.RUBBER_STAIRS.get(), 5, 20);
        fireBlock.setFlammable(Registration.RUBBER_LEAVES.get(), 30, 60);
        fireBlock.setFlammable(Registration.RUBBER_WOOD.get(), 5, 5);
        fireBlock.setFlammable(Registration.STRIPPED_RUBBER_LOG.get(), 5, 20);
        fireBlock.setFlammable(Registration.STRIPPED_RUBBER_WOOD.get(), 5, 5);
        fireBlock.setFlammable(Registration.RUBBER_PLANKS.get(), 5, 20);

    }
}
