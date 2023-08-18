package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

public class SawmillBlockEntity extends CompoundResultMachineBlockEntity<FactocraftyMachineRecipe> {


    public SawmillBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.SAWMILL_MENU.get(),Registration.SAWMILL_RECIPE.get(),Registration.SAWMILL_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.SAWMILL_ACTIVE.get();
    }

}
