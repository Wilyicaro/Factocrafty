package wily.factocrafty.block.storage.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.IFactocraftyBlockEntityType;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageStringUtil;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class FactocraftyEnergyStorageBlockEntity extends FactocraftyMenuBlockEntity {
    public FactocraftyEnergyStorageBlockEntity(MenuType<?> menu, BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(menu, blockEntityType, blockPos, blockState);
        replaceSidedStorage(BlockSide.BACK,energySides, new TransportSide(SlotsIdentifier.ENERGY,TransportState.EXTRACT));
        replaceSidedStorage(BlockSide.FRONT,energySides, new TransportSide(SlotsIdentifier.ENERGY,TransportState.INSERT));
        STORAGE_SLOTS = new int[]{0,1};
    }
    public FactocraftyEnergyStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Registration.ENERGY_STORAGE_MENU.get(), IFactocraftyBlockEntityType.ofBlock(blockState.getBlock()), blockPos, blockState);
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
        if (!level.isClientSide) {
            for (Direction d : Direction.values()) {
                BlockEntity be = level.getBlockEntity(getBlockPos().relative(d));
                if (be != null) {
                    IFactoryStorage storage = FactoryAPIPlatform.getPlatformFactoryStorage(be);
                    storage.getStorage(Storages.CRAFTY_ENERGY, d.getOpposite()).ifPresent(e->{
                        TransportState state = energySides.get(d).getTransport();
                        if (state == TransportState.INSERT && (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(d.getOpposite()).canExtract()))
                            transferEnergyFrom(this, d,e);
                        if (state == TransportState.EXTRACT && (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(d.getOpposite()).canInsert()))
                            transferEnergyTo(this, d, e);
                    });
                }
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
