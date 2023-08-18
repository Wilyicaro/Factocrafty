package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;

import java.util.function.Supplier;

public class FactocraftySyncUpgradeStorage {

    private final BlockPos pos;
    private final ItemStack itemStack;
    private final int index;


    public FactocraftySyncUpgradeStorage(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readItem(), buf.readVarInt());

    }

    public FactocraftySyncUpgradeStorage(BlockPos pos, ItemStack itemStack, int index) {
        this.pos = pos;
        this.itemStack = itemStack;
        this.index = index;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeItem(itemStack);
        buf.writeVarInt(index);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (te instanceof FactocraftyStorageBlockEntity fs) {
                    if (index <= -1 && itemStack.isEmpty()) fs.storedUpgrades.clear();
                    else if (index >= fs.storedUpgrades.size())fs.storedUpgrades.add(itemStack);
                    else if (fs.storedUpgrades.get(index) != itemStack) fs.storedUpgrades.set(index,itemStack);
                }
                te.setChanged();
            }
        });
    }
}
