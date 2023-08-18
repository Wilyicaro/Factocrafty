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
    public SideList<FluidSide> fluidSides = SideList.createSideTypeList(()->new FluidSide(fluidHandler,TransportState.EXTRACT_INSERT));

    public FluidPipeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public Optional<SideList<FluidSide>> fluidSides() {
        return Optional.of(fluidSides);
    }

    @Override
    protected boolean shouldConnectToStorage(IFactoryStorage storage, @Nullable Direction direction) {
        if (storage.getStorage(Storages.FLUID, direction == null ? null : direction.getOpposite()).isEmpty()) return false;
        return direction == null || (storage.fluidSides().isEmpty()  || storage.fluidSides().get().getTransport(direction.getOpposite()).isUsable());
    }
    protected long maxFluidTransfer(){
        return getConduitType().capacityTier.capacityMultiplier * (long)(FluidStack.bucketAmount() * getConduitType().capacityTier.getPowFactor());
    }
    @Override
    public void manageSidedTransference(BlockState state, IFactoryStorage storage, Direction direction) {
        storage.getStorage(Storages.FLUID, direction.getOpposite()).ifPresent((e)->{
            if (storage instanceof ConduitBlockEntity<?>) {
                if ((e.getFluidStack().isFluidEqual(fluidHandler.getFluidStack()) || e.getFluidStack().isEmpty()) && e.getFluidStack().getAmount() < fluidHandler.getFluidStack().getAmount()) {
                    long i = Math.max(1,(fluidHandler.getFluidStack().getAmount() - e.getFluidStack().getAmount()) / 2);
                    fluidHandler.drain(fluidHandler.getFluidStack().copyWithAmount(e.fill(fluidHandler.getFluidStack().copyWithAmount(Math.min(i , maxFluidTransfer())), false)), false);
                }
            }else {
                if (storage.fluidSides().isPresent() && ((fluidSides.get(direction).transportState == TransportState.EXTRACT && storage.fluidSides().get().get(direction.getOpposite()).transportState.canInsert()) || (fluidSides.get(direction).transportState.canExtract() && storage.fluidSides().get().get(direction.getOpposite()).transportState == TransportState.INSERT)))
                    transferFluidTo(this, direction, e);
                if (storage.fluidSides().isPresent() && ((fluidSides.get(direction).transportState.canInsert() && storage.fluidSides().get().get(direction.getOpposite()).transportState == TransportState.EXTRACT)|| (fluidSides.get(direction).transportState == TransportState.INSERT && storage.fluidSides().get().get(direction.getOpposite()).transportState.canExtract())))
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
            BlockState blockState1 = getBlockState();
            if (Platform.isFabric()) {
                int i = ((FluidPipeBlock) blockState1.getBlock()).getLightEmission(blockState1, level, worldPosition);
                if (blockState1.getValue(FactocraftyLedBlock.LIGHT_VALUE) != i){
                    level.setBlock(getBlockPos(),blockState1.setValue(FactocraftyLedBlock.LIGHT_VALUE,i),3);
                }
            }
            level.sendBlockUpdated(getBlockPos(),getBlockState(),blockState1, Block.UPDATE_CLIENTS);
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
    public <T extends IPlatformHandlerApi<?>> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.FLUID) {
                return (Optional<T>) Optional.of(FactoryAPIPlatform.filteredOf(fluidHandler, getBlockedSides().contains(direction) ? TransportState.NONE : fluidSides.getTransport(direction)));
        }
        return Optional.empty();
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
