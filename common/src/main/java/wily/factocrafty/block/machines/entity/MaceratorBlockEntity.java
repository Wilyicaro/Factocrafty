package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

public class MaceratorBlockEntity extends CompoundResultMachineBlockEntity<FactocraftyMachineRecipe> {


    public MaceratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.MACERATOR_MENU.get(),Registration.MACERATOR_RECIPE.get(),Registration.MACERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

}
