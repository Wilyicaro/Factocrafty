package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factoryapi.base.FluidSide;
import wily.factoryapi.base.ItemSide;
import wily.factoryapi.base.TransportState;

import java.util.function.Supplier;

public class FactocraftyStateButtonPacket {

    private final int direction;
    private final BlockPos pos;
    private final TransportState transportState;
    private final int sideType;
    private final int slotIdentifier;

    public FactocraftyStateButtonPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt(),buf.readInt(), TransportState.values()[buf.readInt()], buf.readInt());

    }

    public FactocraftyStateButtonPacket(BlockPos pos, int sideType,int direction, TransportState transportState, int slotIdentifier) {
        this.pos = pos;
        this.direction = direction;
        this.transportState = transportState;
        this.sideType = sideType;
        this.slotIdentifier = slotIdentifier;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(sideType);
        buf.writeInt(direction);
        buf.writeInt(transportState.ordinal());
        buf.writeInt(slotIdentifier);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.getLevel().getBlockEntity(pos);
            if (player.level.isLoaded(pos)) {
                if (te instanceof FactocraftyProcessBlockEntity fs) {
                    Direction d = Direction.values()[direction];
                    if (sideType == 0) fs.itemSides.put(d, new ItemSide( fs.getSlotsIdentifiers().get(slotIdentifier), transportState));
                    else if (sideType == 1) fs.energySides.put(d, transportState);
                    else if (sideType == 2)
                        fs.fluidSides.put(d, new FluidSide(fs.getTanks().get(slotIdentifier), transportState));
                }
                te.setChanged();
            }

        });
    }
}
