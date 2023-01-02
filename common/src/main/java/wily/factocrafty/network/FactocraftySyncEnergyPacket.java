package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.function.Supplier;

public class FactocraftySyncEnergyPacket {

    private int energy;
    private BlockPos pos;
    private FactoryCapacityTiers energyTier;

    public FactocraftySyncEnergyPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(),buf.readInt(), FactoryCapacityTiers.values()[buf.readInt()]);

    }

    public FactocraftySyncEnergyPacket(BlockPos pos, int energy, FactoryCapacityTiers energyTier) {
        this.pos = pos;
        this.energy = energy;
        this.energyTier = energyTier;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(energy);
        buf.writeInt(energyTier.ordinal());
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.getLevel().getBlockEntity(pos);
            if (player.level.isLoaded(pos)) {
                    if (te instanceof FactocraftyProcessBlockEntity fs) {
                    fs.energyStorage.setEnergyStored(energy);
                    fs.energyStorage.setStoredTier(energyTier);
                }
                te.setChanged();
            }
        });
    }
}
