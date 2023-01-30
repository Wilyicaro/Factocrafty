package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;

import java.util.function.Supplier;

public class FactocraftyJetpackUsePacket {
    private final ItemStack stack;

    public FactocraftyJetpackUsePacket(FriendlyByteBuf buf) {
        this(buf.readItem());

    }

    public FactocraftyJetpackUsePacket(ItemStack stack) {
        this.stack = stack;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            player.lerpMotion(Math.sin(Math.toRadians(-player.getYRot())),0.8, Math.cos(Math.toRadians(player.getYRot())));
            Factocrafty.LOGGER.info("OH NO");
            player.setItemSlot(EquipmentSlot.CHEST,stack);
        });
    }
}
