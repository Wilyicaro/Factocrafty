package wily.factocrafty.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.item.ArmorFeatures;
import wily.factocrafty.item.ElectricArmorItem;

import java.util.function.Supplier;

public class FactocraftyArmorFeaturePacket {
    private final ArmorFeatures feature;

    private final EquipmentSlot slot;
    private final boolean active;

    public FactocraftyArmorFeaturePacket(FriendlyByteBuf buf) {
        this(ArmorFeatures.values()[buf.readVarInt()], EquipmentSlot.values()[buf.readVarInt()], buf.readBoolean());

    }

    public FactocraftyArmorFeaturePacket(ArmorFeatures feature, EquipmentSlot slot,Boolean active) {
        this.feature = feature;
        this.slot = slot;
        this.active = active;

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(feature.ordinal());
        buf.writeVarInt(slot.ordinal());
        buf.writeBoolean(active);
    }

    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        ctx.get().queue(() -> {
            Player player = ctx.get().getPlayer();
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                CompoundTag tag = stack.getOrCreateTag();
                if (feature.isActive(tag) != active) tag.putBoolean(feature.getName(), active);
            }
        });
    }
}
