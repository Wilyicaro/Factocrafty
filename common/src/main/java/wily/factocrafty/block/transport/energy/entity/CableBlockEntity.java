package wily.factocrafty.block.transport.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.entity.FilteredCYEnergyStorage;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageUtil;

import java.util.*;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class CableBlockEntity extends ConduitBlockEntity<FactocraftyCables> {


    public CableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        energyStorage = new CYEnergyStorage(this,getConduitType().energyTier.initialCapacity, getConduitType().maxEnergyTransfer(),getConduitType().energyTier);
    }

    @Override
    protected boolean shouldConnectToStorage(IFactoryStorage storage, @Nullable Direction direction) {
        if (direction != null && (storage.getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).isEmpty())) return false;
        return direction == null || (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty()  || (storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()).isUsable()));
    }

    @Override
    public void manageSidedTransference(BlockState state, IFactoryStorage storage, Direction direction) {

        storage.getStorage(Storages.CRAFTY_ENERGY, direction.getOpposite()).ifPresent((e)->{
            if (storage instanceof ConduitBlockEntity<?>) {
                if (e.getEnergyStored() < energyStorage.getEnergyStored())
                    transferEnergyTo(this,c-> new CraftyTransaction(Math.max(1,(energyStorage.getEnergyStored() - e.getEnergyStored())/2),c.tier), c->c.reduce(getConduitType().transferenceEfficiency()), direction,e);
            }else {
                if ((state.getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && !energyBlock.produceEnergy()) || (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()).canInsert()))
                    transferEnergyTo(this, direction,e);
                if((state.getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && (!energyBlock.isEnergyReceiver() || energyBlock.produceEnergy()))|| (storage.getStorageSides(Storages.CRAFTY_ENERGY).isEmpty() || storage.getStorageSides(Storages.CRAFTY_ENERGY).get().getTransport(direction.getOpposite()) == TransportState.EXTRACT))
                    transferEnergyFrom(this, direction, e);

            }
        });
    }

    public final CYEnergyStorage energyStorage;


    @Override
    public <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.CRAFTY_ENERGY) {
            return ()-> (T) FilteredCYEnergyStorage.of(energyStorage,getBlockedSides().contains(direction) ? TransportState.NONE : TransportState.EXTRACT_INSERT);
        }
        return ArbitrarySupplier.empty();
    }

}
