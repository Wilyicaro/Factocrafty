package wily.factocrafty.network;

import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factoryapi.base.IFactoryExpandedStorage;
import wily.factoryapi.base.IFactoryStorage;

import java.util.function.Supplier;

public class FactocraftySyncFluidPacket {
    private FluidStack stack;
    private int tankIdentifier;
    private BlockPos pos;

    public FactocraftySyncFluidPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt(), FluidStack.read(buf) );

    }

    public FactocraftySyncFluidPacket(BlockPos pos, int identifier, FluidStack stack) {
        this.pos = pos;
        this.tankIdentifier = identifier;
        this.stack = stack;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(tankIdentifier);
        stack.write(buf);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                ((IFactoryExpandedStorage)te).getTanks().get(tankIdentifier).setFluid(stack);
                te.setChanged();
            }
        });
    }
}
