package wily.factocrafty.block.generator.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.generator.SolarPanelTiers;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

import java.util.List;

public class SolarPanelBlockEntity extends FactocraftyProcessBlockEntity {


    public SolarPanelTiers solarTier;
    public Bearer<Integer> tickEnergy;

    public SolarPanelBlockEntity(SolarPanelTiers tier, BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.SOLAR_PANEL,tier.energyTier,tier.getBlockEntity(), blockPos, blockState);
        FILL_SLOT = 0;
        DRAIN_SLOT = 0;
        this.solarTier = tier;
        this.tickEnergy = Bearer.of(0);
        this.additionalSyncInt.add(tickEnergy);

        if (blockState.isFaceSturdy(level,blockPos,Direction.UP)) energySides.replace(Direction.UP, TransportState.NONE);
    }

    @Override
    public int getInitialEnergyCapacity() {
        return defaultEnergyTier.energyCapacity;
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactocraftyCYItemSlot(this,0,147,53, TransportState.INSERT, FactoryCapacityTiers.BASIC));
    }


    @Override
    public List<Direction> getBlockedSides() {
        return solarTier.ordinal() < SolarPanelTiers.QUANTUM.ordinal() ? List.of(Direction.UP) : super.getBlockedSides();
    }

    protected int getEnergyPerTick(){
        int i = level.getBrightness(LightLayer.SKY, getBlockPos()) - level.getSkyDarken();
        float f = level.getSunAngle(1.0F);
        if (i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            f += (f1 - f) * 0.2F;
            i = Math.round((float)i * Mth.cos(f));
        }
        int max = (int) ((23 * solarTier.efficiency) * solarTier.energyTier.capacityMultiplier);
        i = Mth.clamp(i * max / 15, 0, max);
        return i;
    }
    public void tick() {
        if (!level.isClientSide) {
            tickEnergy.set(energyStorage.receiveEnergy(new ICraftyEnergyStorage.EnergyTransaction(getEnergyPerTick(),solarTier.energyTier), false).energy);
            super.tick();
            for (Direction direction : Direction.values()) {
                BlockPos pos = getBlockPos().relative(direction);
                if (level.getBlockState(pos).getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && energyBlock.isEnergyReceiver() && !(energyBlock instanceof InsulatedCableBlock)) {
                    IFactoryStorage CYEbe = (IFactoryStorage) level.getBlockEntity(pos);
                    if (CYEbe != null)
                        CYEbe.getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).ifPresent((e)-> transferEnergyTo(direction, e));
                }
            }
            if ( tickEnergy.get() > 0 != getBlockState().getValue(FactocraftyMachineBlock.ACTIVE)) level.setBlock(worldPosition,getBlockState().setValue(FactocraftyMachineBlock.ACTIVE,tickEnergy.get() > 0),3);
        }
    }

}
