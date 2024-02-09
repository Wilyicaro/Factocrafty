package wily.factocrafty.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.NuclearReactorBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.FactocraftySyncMultiBlockPosPacket;
import wily.factoryapi.base.*;

import java.util.Optional;

public class ReactorCasingBlockEntity extends BlockEntity implements IFactoryStorage,ITicker {
    public ReactorCasingBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.REACTOR_CASING_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public Optional<BlockPos> nuclearCorePos = Optional.empty();

    public void setNuclearCorePos(BlockPos nuclearCorePos) {
        this.nuclearCorePos = Optional.ofNullable(nuclearCorePos);
        if (level instanceof ServerLevel l) Factocrafty.NETWORK.sendToPlayers(l.players(),new FactocraftySyncMultiBlockPosPacket(getBlockPos(),nuclearCorePos));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains("nuclearReactorCorePos"))
         setNuclearCorePos(BlockPos.of(compoundTag.getLong("nuclearReactorCorePos")));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        nuclearCorePos.ifPresent(p->compoundTag.putLong("nuclearReactorCorePos",p.asLong()));
    }

    @Override
    public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (nuclearCorePos.isPresent() && level.getBlockEntity(nuclearCorePos.get()) instanceof NuclearReactorBlockEntity be) return be.getStorage(storage,direction);
        return ArbitrarySupplier.empty();
    }

    @Override
    public ArbitrarySupplier<SideList<TransportSide>> getStorageSides(Storages.Storage<?> storage) {
        if (nuclearCorePos.isPresent() && level.getBlockEntity(nuclearCorePos.get()) instanceof NuclearReactorBlockEntity be) return be.getStorageSides(storage);
        return ArbitrarySupplier.empty();
    }
}
