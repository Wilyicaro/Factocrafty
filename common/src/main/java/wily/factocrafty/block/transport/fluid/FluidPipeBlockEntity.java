package wily.factocrafty.block.transport.fluid;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.block.transport.entity.SolidConduitBlockEntity;
import wily.factocrafty.util.registering.FactocraftyFluidPipes;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.*;

import static wily.factoryapi.util.StorageUtil.*;

public class FluidPipeBlockEntity extends SolidConduitBlockEntity<FactocraftyFluidPipes> {
    public IPlatformFluidHandler<?> fluidHandler = FactoryAPIPlatform.getFluidHandlerApi(getConduitType().capacityTier.capacityMultiplier * FluidStack.bucketAmount(),this,(f)-> true,SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT);
    public SideList<? super ISideType<?>> fluidSides = new SideList<>(()->new TransportSide(fluidHandler.identifier(),TransportState.EXTRACT_INSERT));

    public FluidPipeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }


    @Override
    protected boolean shouldConnectToStorage(IFactoryStorage storage, @Nullable Direction direction) {
        if (storage.getStorage(Storages.FLUID, direction == null ? null : direction.getOpposite()).isEmpty()) return false;
        return direction == null || (storage.getStorageSides(Storages.FLUID).isEmpty()  || storage.getStorageSides(Storages.FLUID).get().getTransport(direction.getOpposite()).isUsable());
    }

    @Override
    public void manageSidedTransference(BlockState state, IFactoryStorage storage, Direction direction) {
        storage.getStorage(Storages.FLUID, direction.getOpposite()).ifPresent((e)->{
            if (storage instanceof ConduitBlockEntity<?>) {
                if ((e.getFluidStack().isFluidEqual(fluidHandler.getFluidStack()) || e.getFluidStack().isEmpty()) && e.getFluidStack().getAmount() < fluidHandler.getFluidStack().getAmount()) {
                    long i = Math.max(1,(fluidHandler.getFluidStack().getAmount() - e.getFluidStack().getAmount()) / 2);
                    fluidHandler.drain(fluidHandler.getFluidStack().copyWithAmount(e.fill(fluidHandler.getFluidStack().copyWithAmount(Math.min(i , getConduitType().maxFluidTransfer())), false)), false);
                }
            }else {
                if (!fluidHandler.getFluidStack().isEmpty() && ((fluidSides.get(direction).getTransport() == TransportState.EXTRACT && (storage.getStorageSides(Storages.FLUID).isEmpty() || storage.getStorageSides(Storages.FLUID).get().get(direction.getOpposite()).getTransport().canInsert())) || (fluidSides.get(direction).getTransport().canExtract() && storage.getStorageSides(Storages.FLUID).isPresentAnd(f-> f.getTransport(direction.getOpposite()) == TransportState.INSERT))))
                    transferFluidTo(this, direction, e);
                if (!e.getFluidStack().isEmpty() && ((fluidSides.get(direction).getTransport().canInsert() && storage.getStorageSides(Storages.FLUID).isPresentAnd(f-> f.getTransport(direction.getOpposite()) == TransportState.EXTRACT))|| (fluidSides.get(direction).getTransport() == TransportState.INSERT && (storage.getStorageSides(Storages.FLUID).isEmpty() || storage.getStorageSides(Storages.FLUID).get().get(direction.getOpposite()).getTransport().canExtract()))))
                    transferFluidFrom(this, direction, e);

            }
        });
    }

    public long smoothFluidAmount = 0;
    private long oldFluidAmount = fluidHandler.getFluidStack().getAmount();
    @Override
    public void tick() {
        if (smoothFluidAmount != fluidHandler.getFluidStack().getAmount())
            smoothFluidAmount = (long) Mth.clamp((long) (smoothFluidAmount + Math.pow(oldFluidAmount, -0.02) * fluidHandler.getMaxFluid() / 16), 0, oldFluidAmount);
        if (oldFluidAmount != fluidHandler.getFluidStack().getAmount()){
            oldFluidAmount = fluidHandler.getFluidStack().getAmount();
            level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_CLIENTS);
            level.getProfiler().push("queueCheckLight");
            level.getChunkSource().getLightEngine().checkBlock(worldPosition);
            level.getProfiler().pop();
        }
        super.tick();
    }

    @Override
    public List<IPlatformFluidHandler<?>> getTanks() {
        return List.of(fluidHandler);
    }

    @Override
    public <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.FLUID)
                return ()-> (T) FactoryAPIPlatform.filteredOf(fluidHandler, getBlockedSides().contains(direction) ? TransportState.NONE : fluidSides.getTransport(direction));
        return ArbitrarySupplier.empty();
    }

    @Override
    public ArbitrarySupplier<SideList<? super ISideType<?>>> getStorageSides(Storages.Storage<?> storage) {
        if (storage == Storages.FLUID) return ()->fluidSides;
        return super.getStorageSides(storage);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {return ClientboundBlockEntityDataPacket.create(this);}

    public void handleUpdateTag(CompoundTag tag){
        if (tag != null)
            load(tag);

    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
