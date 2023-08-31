package wily.factocrafty.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.FactocraftySyncEnergyPacket;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.TransportState;

import javax.swing.*;
import java.util.Map;
import java.util.Optional;

import static wily.factocrafty.block.FactocraftyLedBlock.LIGHT_VALUE;

public class FactocraftyLedBlockEntity extends FactocraftyMenuBlockEntity {
    public Bearer<Integer> actualRgb = Bearer.of(0xFFFFFF);
    public Bearer<Integer> savedLightValue = Bearer.of(0);
    private final FactocraftyLedBlock block;

    private int lightTime;
    public FactocraftyLedBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.RGB_MENU.get(),Registration.LED_BLOCK_ENTITY.get(), blockPos, blockState);
        this.block = (FactocraftyLedBlock) blockState.getBlock();
        this.energyStorage = new CYEnergyStorage(this, getDefaultEnergyTier().initialCapacity, getDefaultEnergyTier().initialCapacity, block.getEnergyTier());
        if (block.hasRGB) additionalSyncInt.add(actualRgb);
        additionalSyncInt.add(savedLightValue);
    }

    @Override
    public boolean hasUpgradeStorage() {
        return false;
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            lightTime++;
            BlockState blockState = getBlockState();
            int lightValue = 0;
            if (energyStorage.getEnergyStored() > 0) {
                lightValue = savedLightValue.get();
                if (blockState.getValue(LIGHT_VALUE) > 0 && level.random.nextFloat() >= 0.9F / blockState.getValue(LIGHT_VALUE) && lightTime % 10 == 0)
                    energyStorage.consumeEnergy(1, false);
            }
            if (blockState.getValue(LIGHT_VALUE) != lightValue)
                level.setBlock(getBlockPos(), blockState.setValue(LIGHT_VALUE, lightValue), 3);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
    }

    @Override
    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player) {
        super.syncAdditionalMenuData(menu, player);
        Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncEnergyPacket(getBlockPos(),energyStorage));
    }

}
