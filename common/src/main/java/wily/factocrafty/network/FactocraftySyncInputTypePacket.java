package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;

import java.util.function.Supplier;

public class FactocraftySyncInputTypePacket {

    private final BlockPos pos;
    private final ChangeableInputMachineBlockEntity.InputType inputType;


    public FactocraftySyncInputTypePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), RefinerBlockEntity.InputType.values()[buf.readInt()]);

    }

    public FactocraftySyncInputTypePacket(BlockPos pos, RefinerBlockEntity.InputType inputType) {
        this.pos = pos;
        this.inputType = inputType;


    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(inputType.ordinal());
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.getLevel().getBlockEntity(pos);
            if (player.level.isLoaded(pos)) {
                if (te instanceof ChangeableInputMachineBlockEntity fs) {
                    if (inputType != fs.inputType) fs.inputType = inputType;
                }
                te.setChanged();
            }

        });
    }
}
