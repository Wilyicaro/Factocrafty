package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.IFactoryProgressiveStorage;

import java.util.function.Supplier;

public class FactocraftySyncProgressPacket {

    private final int index;
    private final int[] values;

    private final BlockPos pos;

    public FactocraftySyncProgressPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt() , buf.readVarIntArray());

    }

    public FactocraftySyncProgressPacket(BlockPos pos, int index, int[] values) {
        this.pos = pos;
        this.index = index;
        this.values = values;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(index);
        buf.writeVarIntArray(values);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity be = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos) && be instanceof IFactoryProgressiveStorage s) {
                s.getProgresses().get(index).setValues(values);
                be.setChanged();
            }
        });
    }
}
