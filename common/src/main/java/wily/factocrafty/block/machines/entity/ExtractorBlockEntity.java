package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyMenus;

public class ExtractorBlockEntity extends CompoundResultMachineBlockEntity {


    public ExtractorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.EXTRACTOR,Registration.EXTRACTOR_RECIPE.get(),Registration.EXTRACTOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

}
