package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.inventory.UpgradeList;

import java.util.List;
import java.util.function.Supplier;

public class FactocraftySyncUpgradeStorage {

    private final BlockPos pos;
    private final List<ItemStack> itemStackList;


    public FactocraftySyncUpgradeStorage(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readList(FriendlyByteBuf::readItem));

    }

    public FactocraftySyncUpgradeStorage(BlockPos pos, List<ItemStack> stackList) {
        this.pos = pos;
        this.itemStackList = stackList;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeCollection(itemStackList,FriendlyByteBuf::writeItem);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            BlockEntity te = player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                if (te instanceof FactocraftyStorageBlockEntity fs && (fs.storedUpgrades.size() != itemStackList.size() || !fs.storedUpgrades.containsAll(itemStackList))){
                    fs.storedUpgrades.list = itemStackList;
                }
                te.setChanged();
            }
        });
    }
}
