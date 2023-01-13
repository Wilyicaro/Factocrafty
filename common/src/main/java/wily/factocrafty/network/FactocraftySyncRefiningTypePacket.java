package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;

import java.util.function.Supplier;

public class FactocraftySyncRefiningTypePacket {

    private final BlockPos pos;
    private final RefinerBlockEntity.RefiningType refiningType;


    public FactocraftySyncRefiningTypePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), RefinerBlockEntity.RefiningType.values()[buf.readInt()]);

    }

    public FactocraftySyncRefiningTypePacket(BlockPos pos, RefinerBlockEntity.RefiningType refiningType) {
        this.pos = pos;
        this.refiningType = refiningType;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(refiningType.ordinal());
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.getLevel().getBlockEntity(pos);
            if (player.level.isLoaded(pos)) {
                if (te instanceof RefinerBlockEntity fs) {
                    if (refiningType != fs.refiningType) fs.refiningType = refiningType;
                }
                te.setChanged();
            }

        });
    }
}
