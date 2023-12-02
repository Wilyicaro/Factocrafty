package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.*;

import java.util.function.Supplier;

public class FactocraftyStorageSidesPacket {

    private final int direction;
    private final BlockPos pos;
    private final TransportState transportState;
    private final int sideType;
    private final int index;

    public FactocraftyStorageSidesPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt(),buf.readInt(), TransportState.values()[buf.readInt()], buf.readInt());

    }

    public FactocraftyStorageSidesPacket(BlockPos pos, int sideType, int direction, TransportState transportState, int index) {
        this.pos = pos;
        this.direction = direction;
        this.transportState = transportState;
        this.sideType = sideType;
        this.index = index;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(sideType);
        buf.writeInt(direction);
        buf.writeInt(transportState.ordinal());
        buf.writeInt(index);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (te instanceof IFactoryExpandedStorage fs) {
                    Direction d = Direction.values()[direction];
                    if (sideType == 0) fs.getStorageSides(Storages.ITEM).ifPresent((i)-> i.get(d).withTransport(transportState).withSlotIdentifier(fs.getItemSlotsIdentifiers().get(index)));
                    else if (sideType == 1) fs.getStorageSides(Storages.CRAFTY_ENERGY).ifPresent(i-> i.setTransport(transportState,d));
                    else if (sideType == 2) fs.getStorageSides(Storages.FLUID).ifPresent(i-> i.get(d).withTransport(transportState).withSlotIdentifier(fs.getTanks().get(index).identifier()));
                }
                te.setChanged();
            }

        });
    }
}
