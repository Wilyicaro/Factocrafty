package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;

import java.util.function.Supplier;

public class FactocraftySyncSelectedUpgradePacket {

    private final BlockPos pos;
    private final int selectedUpgrade;


    public FactocraftySyncSelectedUpgradePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readInt());

    }

    public FactocraftySyncSelectedUpgradePacket(BlockPos pos, int inputType) {
        this.pos = pos;
        this.selectedUpgrade = inputType;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(selectedUpgrade);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (te instanceof FactocraftyProcessBlockEntity fs) {
                    if (selectedUpgrade != fs.selectedUpgrade) fs.selectedUpgrade = selectedUpgrade;
                }
                te.setChanged();
            }
        });
    }
}
