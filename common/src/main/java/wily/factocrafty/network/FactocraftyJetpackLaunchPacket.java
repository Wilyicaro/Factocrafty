package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class FactocraftyJetpackLaunchPacket {
    private final ItemStack stack;
    private final boolean mayFly;

    public FactocraftyJetpackLaunchPacket(FriendlyByteBuf buf) {
        this(buf.readItem(), buf.readBoolean());

    }

    public FactocraftyJetpackLaunchPacket(ItemStack stack, Boolean mayFly) {
        this.stack = stack;
        this.mayFly = mayFly;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
        buf.writeBoolean(mayFly);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            if (!stack.isEmpty()) {
                ItemStack jet = player.getItemBySlot(EquipmentSlot.CHEST);
                jet.setTag(stack.getOrCreateTag());
            }
            player.getAbilities().mayfly = mayFly;
        });
    }
}
