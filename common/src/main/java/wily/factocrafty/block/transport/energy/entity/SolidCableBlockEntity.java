package wily.factocrafty.block.transport.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.entity.FilteredCYEnergyStorage;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.block.transport.entity.SolidConduitBlockEntity;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.Optional;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class SolidCableBlockEntity extends SolidConduitBlockEntity<FactocraftyCables> {

    public SolidCableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        energyStorage = new CYEnergyStorage(this, getConduitType().energyTier.initialCapacity, maxEnergyTransfer(),getConduitType().energyTier);
    }

    @Override
    protected boolean shouldConnectToStorage(IFactoryStorage storage, @Nullable Direction direction) {
        if (direction != null && storage.getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).isEmpty()) return false;
        return direction == null || (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()).isUsable());
    }

    @Override
    public void manageSidedTransference(BlockState state, IFactoryStorage storage, Direction direction) {

        storage.getStorage(Storages.CRAFTY_ENERGY, direction.getOpposite()).ifPresent((e)->{
            if (storage instanceof ConduitBlockEntity<?>) {
                if (e.getEnergyStored() < energyStorage.getEnergyStored()) {
                    int i = (energyStorage.getEnergyStored() - e.getEnergyStored()) / 2;
                    energyStorage.consumeEnergy((e.receiveEnergy(new CraftyTransaction(Math.min(i, maxEnergyTransfer()), energyStorage.storedTier), false).reduce(getConduitType().transferenceEfficiency())), false);
                }
            }else {
                if ((state.getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && !energyBlock.produceEnergy()) ||  (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()).canInsert()))
                    transferEnergyTo(this, direction,e);
                if((state.getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && (!energyBlock.isEnergyReceiver() || energyBlock.produceEnergy()))|| (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()) == TransportState.EXTRACT))
                    transferEnergyFrom(this, direction, e);
            }
        });
    }

    public final CYEnergyStorage energyStorage;

    public int maxEnergyTransfer() {
        return (int) (getConduitType().energyTier.initialCapacity * getConduitType().energyTier.getConductivity());
    }
    public SideList<TransportSide> energySides =  new SideList<>(()->new TransportSide(SlotsIdentifier.ENERGY, TransportState.EXTRACT_INSERT));

    @Override
    public ArbitrarySupplier<SideList<TransportSide>> getStorageSides(Storages.Storage<?> storage) {
        return storage == Storages.CRAFTY_ENERGY ? () ->energySides : ArbitrarySupplier.empty();
    }

    @Override
    public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.CRAFTY_ENERGY)
            return ()-> (T) (direction != null ?  FactoryAPIPlatform.filteredOf(energyStorage, direction,getBlockedSides().contains(direction) ? TransportState.NONE : getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction),FilteredCYEnergyStorage::of) : energyStorage);
        return ArbitrarySupplier.empty();
    }


}
