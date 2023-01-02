package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.FactoryCapacityTiers;

public class CompressorBlockEntity extends FactocraftyMachineBlockEntity {


    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.COMPRESSOR, FactoryCapacityTiers.BASIC,Registration.COMPRESSOR_RECIPE.get(),Registration.COMPRESSOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

}
