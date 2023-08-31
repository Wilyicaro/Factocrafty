package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;

import java.util.function.Supplier;

public class FactocraftySyncIntegerBearerPacket {

    private final int index;
    private final int value;
    private final BlockPos pos;


    public FactocraftySyncIntegerBearerPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt());}
    public FactocraftySyncIntegerBearerPacket(BlockPos pos,  int value, int index) {
        this.pos = pos;
        this.value = value;
        this.index = index;
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeVarInt(value);
        buf.writeVarInt(index);
    }
    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity be = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (be instanceof FactocraftyMenuBlockEntity s) {
                    s.additionalSyncInt.get(index).set(value);
                    s.setChanged();
                    player.level().sendBlockUpdated(pos,be.getBlockState(),be.getBlockState(),3);
                }
            }
        });
    }
}
