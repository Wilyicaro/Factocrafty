package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;

public class MaceratorBlockEntity extends CompoundResultMachineBlockEntity<FactocraftyMachineRecipe> {


    public MaceratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.MACERATOR,Registration.MACERATOR_RECIPE.get(),Registration.MACERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

}
