package wily.factocrafty.block.transport.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.CraftyTransaction;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.util.StorageStringUtil;
import wily.factoryapi.util.StorageUtil;

import java.util.List;

public class CableBlock extends FactocraftyConduitBlock<FactocraftyCables,CableBlockEntity> implements IFactocraftyCYEnergyBlock {

    public CableBlock(FactocraftyCables cable, Properties properties) {
         super(cable,properties);
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (conduitType.insulation < 1 && entity instanceof LivingEntity && level.random.nextFloat() <= 0.45  && level.getBlockEntity(blockPos) instanceof CableBlockEntity be && be.energyStorage.getEnergyStored() > 100){
            if (entity.hurt(level.damageSources().lightningBolt(), be.energyStorage.consumeEnergy(new CraftyTransaction((int) Math.min(90 / conduitType.energyTier.getConductivity(), be.energyStorage.getEnergyStored() * (1 - conduitType.insulation)), be.energyStorage.storedTier).reduce(25), false).energy)){
                level.playSound(null,entity.getOnPos(), Registration.ELECTRIC_SHOCK.get(), SoundSource.BLOCKS,1.0F,1.0F);
            }
        }
    }

    @Override
    public void unsupportedTierBurn(Level level, BlockPos pos, FactoryCapacityTiers higherTier) {
        IFactocraftyCYEnergyBlock.super.unsupportedTierBurn(level, pos,higherTier);
        level.removeBlock(pos, true);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(conduitType.getCapacityTier().getEnergyTierComponent(false));
        list.add(StorageStringUtil.getMaxCraftyTransferTooltip(conduitType.maxEnergyTransfer()).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return conduitType.getCapacityTier();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CableBlockEntity(blockPos,blockState);
    }
}
