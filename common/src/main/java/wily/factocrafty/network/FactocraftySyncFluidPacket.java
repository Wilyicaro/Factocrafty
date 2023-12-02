package wily.factocrafty.network;

import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.IFactoryExpandedStorage;

import java.util.function.Supplier;

public class FactocraftySyncFluidPacket {
    private final FluidStack stack;

    private final long capacity;
    private final int index;
    private final BlockPos pos;

    public FactocraftySyncFluidPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readLong() ,buf.readInt(), FluidStack.read(buf) );

    }

    public FactocraftySyncFluidPacket(BlockPos pos,long capacity, int index, FluidStack stack) {
        this.pos = pos;
        this.capacity = capacity;
        this.index = index;
        this.stack = stack;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeLong(capacity);
        buf.writeInt(index);
        stack.write(buf);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                ((IFactoryExpandedStorage)te).getTanks().get(index).setFluid(stack);
                ((IFactoryExpandedStorage)te).getTanks().get(index).setCapacity(capacity);
                te.setChanged();
            }
        });
    }
}
