package wily.factocrafty.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static wily.factocrafty.FactocraftyClient.GRAVITY_KEYMAPPING;


public class QuantumArmor extends ArmorItem {
    public QuantumArmor(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Properties properties) {
        super(armorMaterial, equipmentSlot, properties);
    }
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        if (entity instanceof ServerPlayer player && !player.isCreative()) {
            if (slot.equals(EquipmentSlot.CHEST) && player.getItemBySlot(EquipmentSlot.CHEST).is(itemStack.getItem())){
            if (GRAVITY_KEYMAPPING.consumeClick()) {
                if (player.getAbilities().mayfly) {
                    itemStack.getOrCreateTag().putInt("gravity", 10);
                    player.getAbilities().flying = false;
                } else {
                    itemStack.getOrCreateTag().putInt("gravity", 0);
                }

            }
                if (itemStack.getOrCreateTag().getInt("gravity") == 0) {
                    player.getAbilities().mayfly = true;
                    if (!player.isOnGround()) player.getAbilities().flying = true;
                } else if (itemStack.getOrCreateTag().getInt("gravity") == 10) player.getAbilities().mayfly = false;
            } else {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }


    }
}
