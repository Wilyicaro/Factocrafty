package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.IFactoryProcessableStorage;
import wily.factoryapi.base.Progress;

import java.util.function.Supplier;

public class FactocraftySyncProgressPacket {

    private final int index;
    private final int[] progress;

    private final int maxProgress;

    private final BlockPos pos;

    public FactocraftySyncProgressPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt() , buf.readVarIntArray(), buf.readInt());

    }

    public FactocraftySyncProgressPacket(BlockPos pos, int index, int[] progress, int maxProgress) {
        this.pos = pos;
        this.index = index;
        this.progress = progress;
        this.maxProgress = maxProgress;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(index);
        buf.writeVarIntArray(progress);
        buf.writeInt(maxProgress);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity be = player.getLevel().getBlockEntity(pos);
            if (player.level.isLoaded(pos)) {
                assert be != null;
                ((IFactoryProcessableStorage)be).getProgresses().get(index).set(progress);
                ((IFactoryProcessableStorage)be).getProgresses().get(index).maxProgress = maxProgress;
                be.setChanged();
            }
        });
    }
}
