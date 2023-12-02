package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.FactocraftyReactorCasing;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.entity.ReactorCasingBlockEntity;
import wily.factoryapi.base.*;

import java.util.function.Supplier;

public class FactocraftySyncMultiBlockPosPacket {


    private final BlockPos pos;

    private final BlockPos nuclearPos;

    public FactocraftySyncMultiBlockPosPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBlockPos());
    }
    public FactocraftySyncMultiBlockPosPacket(BlockPos pos, BlockPos nuclearPos) {
        this.pos = pos;
        this.nuclearPos = nuclearPos;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBlockPos(nuclearPos == null ? BlockPos.ZERO : nuclearPos);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity be = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (be instanceof ReactorCasingBlockEntity casing) {
                    casing.setNuclearCorePos(nuclearPos);
                }
                be.setChanged();
            }
        });
    }
}
