package wily.factocrafty.block.cable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.cable.entity.CableBlockEntity;
import wily.factoryapi.base.ICraftyEnergyStorage;

public class CableBlock extends InsulatedCableBlock {

    public CableBlock(CableTiers tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);

        if (entity instanceof LivingEntity && level.random.nextFloat() <= 0.45 && level.getBlockEntity(blockPos) instanceof CableBlockEntity be && be.energyStorage.getEnergyStored() > 100){
            entity.hurt(DamageSource.LIGHTNING_BOLT, be.energyStorage.consumeEnergy(new ICraftyEnergyStorage.EnergyTransaction((int) Math.max(100, Math.pow(be.energyStorage.getEnergyStored() , cableTier.energyTier.getConductivity())), be.energyStorage.storedTier).reduce(25), false).energy);
        }
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CableBlockEntity(cableTier,blockPos, blockState);
    }
}
