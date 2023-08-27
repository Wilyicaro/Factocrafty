package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;

public interface IFactocraftyCYEnergyBlock {



    default boolean produceEnergy(){
        return false;
    }
    default boolean isEnergyReceiver(){
        return true;
    }
    FactoryCapacityTiers getEnergyTier();


    default void unsupportedTierBurn(Level level, BlockPos pos, FactoryCapacityTiers higherTier){
        level.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
        for (float i = 0; i < 0.6; i += 0.2) {
            if (level instanceof ServerLevel serverLevel)
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5 - i / 2, pos.getY() + 0.5, pos.getZ() +0.5 + i / 2, 1,0.0, 0.0, 0.0,1.0D);
        }
    }
    default ItemStack getBurnRepairItem(RecipeManager recipeManager){
        return (getEnergyTier().ordinal() >= FactoryCapacityTiers.ULTIMATE.ordinal() ? Registration.ADVANCED_CIRCUIT_BOARD.get() : Registration.CIRCUIT_BOARD.get()).getDefaultInstance();
    }

}
