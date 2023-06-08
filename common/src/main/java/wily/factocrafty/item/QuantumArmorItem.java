package wily.factocrafty.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import wily.factoryapi.base.FactoryCapacityTiers;

import static wily.factocrafty.FactocraftyClient.GRAVITY_KEYMAPPING;


public class QuantumArmorItem extends ElectricArmorItem{
    public QuantumArmorItem(ArmorItem.Type equipmentSlot, Properties properties) {
        super(FactoryCapacityTiers.QUANTUM,2200,FactocraftyArmorMaterials.QUANTUM, equipmentSlot, properties);
    }

}
