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

public class FactocraftyLedBlockEntity extends FactocraftyStorageBlockEntity {
    public Bearer<Integer> actualRgb = Bearer.of(0xFFFFFF);
    public int savedLightValue;
    private final FactocraftyLedBlock block;

    private int lightTime;
    public FactocraftyLedBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.LED_BLOCK_ENTITY.get(), blockPos, blockState);
        this.block = (FactocraftyLedBlock) blockState.getBlock();
        this.energyStorage = new CYEnergyStorage(this, 0, 800, block.getEnergyTier());
        if (block.hasRGB) additionalSyncInt.add(actualRgb);
    }

    @Override
    public boolean hasEnergyCell() {
        return true;
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            lightTime++;
            BlockState blockState = getBlockState();
            int lightValue = 0;
            if (energyStorage.getEnergyStored() > 0) {
                lightValue = savedLightValue;
                if (blockState.getValue(LIGHT_VALUE) > 0 && level.random.nextFloat() >= 0.9F / blockState.getValue(LIGHT_VALUE) && lightTime % 10 == 0)
                    energyStorage.consumeEnergy(1, false);
            }
            if (blockState.getValue(LIGHT_VALUE) != lightValue)
                level.setBlock(getBlockPos(), blockState.setValue(LIGHT_VALUE, lightValue), 3);
        }
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        savedLightValue = compoundTag.getShort("savedLightValue");
    }

    @Override
    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player) {
        super.syncAdditionalMenuData(menu, player);
        Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncEnergyPacket(getBlockPos(),energyStorage.getEnergyStored(),energyStorage.getStoredTier()));
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putShort("savedLightValue", (short) savedLightValue);
    }
}
