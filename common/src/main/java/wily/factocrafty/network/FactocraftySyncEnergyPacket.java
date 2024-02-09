package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factoryapi.base.*;

import java.util.function.Supplier;

public class FactocraftySyncEnergyPacket {

    private final int energy;
    private final int capacity;
    private final BlockPos pos;
    private final FactoryCapacityTiers storedTier;

    private final FactoryCapacityTiers supportedTier;

    public FactocraftySyncEnergyPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }
    public FactocraftySyncEnergyPacket(BlockPos pos, IPlatformEnergyStorage st) {
        this(pos,st.getEnergyStored(), st.getMaxEnergyStored(),st instanceof ICraftyEnergyStorage s ? s.getStoredTier().ordinal() :  -1, st instanceof ICraftyEnergyStorage s ? s.getSupportedTier().ordinal() : -1);

    }
    public FactocraftySyncEnergyPacket(BlockPos pos, int energy, int capacity, int storedTier, int supportedTier) {
        this.pos = pos;
        this.energy = energy;
        this.capacity = capacity;
        this.storedTier = storedTier >= 0 ? FactoryCapacityTiers.values()[storedTier] : null;
        this.supportedTier =  supportedTier >= 0 ? FactoryCapacityTiers.values()[supportedTier] : null;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(energy);
        buf.writeInt(capacity);
        buf.writeInt(storedTier == null ? -1 : storedTier.ordinal());
        buf.writeInt(supportedTier == null ? -1 : supportedTier.ordinal());
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity be = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (be instanceof IFactoryStorage fs) {
                    if (supportedTier == null || storedTier == null) fs.getStorage(Storages.ENERGY).ifPresent(e-> e.setEnergyStored(energy));
                    else fs.getStorage(Storages.CRAFTY_ENERGY).ifPresent(e->{
                        e.setEnergyStored(energy);
                        e.setStoredTier(storedTier);
                        e.setSupportedTier(supportedTier);
                        if (e instanceof CYEnergyStorage storage) storage.capacity = capacity;
                    });
                }
                be.setChanged();
            }
        });
    }
}
