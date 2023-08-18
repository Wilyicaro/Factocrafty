package wily.factocrafty.block.storage.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageStringUtil;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;

public class FactocraftyEnergyStorageBlockEntity extends FactocraftyMenuBlockEntity {
    public FactocraftyEnergyStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.ENERGY_STORAGE_MENU.get(), FactocraftyBlockEntities.ofBlock(blockState.getBlock()), blockPos, blockState);
        for (BlockSide side : BlockSide.values())
            replaceSidedStorage(side,energySides, TransportState.EXTRACT_INSERT);
        replaceSidedStorage(BlockSide.BACK,energySides, TransportState.EXTRACT);
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.INSERT);
        STORAGE_SLOTS = new int[]{0,1};
    }

    @Override
    public int getInitialEnergyCapacity() {
        return getDefaultEnergyTier().getStorageCapacity();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal(StorageStringUtil.getBetweenParenthesis(super.getDisplayName().getString())).setStyle(super.getDisplayName().getStyle());
    }

    @Override
    public void tick() {
        super.tick();
        for  (Direction d : Direction.values()){
            if (level.getBlockEntity(getBlockPos().relative(d)) instanceof IFactoryStorage storage) {
                if (energySides.get(d).canInsert() && storage.energySides().isPresent() && storage.energySides().get().get(d.getOpposite()).canExtract() ) transferEnergyFrom(this, d,storage.getStorage(Storages.CRAFTY_ENERGY,d.getOpposite()).get());
            }
        }
    }

    @Override
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots= super.getSlots(player);
        slots.add(new FactocraftyCYItemSlot(this, 0, 61,53, TransportState.INSERT, SlotsIdentifier.INPUT,()->energyStorage.getStoredTier()));
        slots.add(new FactocraftyCYItemSlot(this, 1, 61,17, TransportState.EXTRACT, SlotsIdentifier.OUTPUT,()->energyStorage.getStoredTier()));
        return slots;
    }
}
