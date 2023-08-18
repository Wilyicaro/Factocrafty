package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

public class CompressorBlockEntity extends ProcessMachineBlockEntity<FactocraftyMachineRecipe> {


    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.COMPRESSOR_MENU.get(),Registration.COMPRESSOR_RECIPE.get(),Registration.COMPRESSOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.COMPRESSOR_ACTIVE.get();
    }

}
