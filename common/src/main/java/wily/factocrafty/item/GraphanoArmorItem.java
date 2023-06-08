package wily.factocrafty.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import wily.factoryapi.base.FactoryCapacityTiers;

public class GraphanoArmorItem extends ElectricArmorItem {
    public GraphanoArmorItem(Type equipmentSlot, Properties properties) {
        super(FactoryCapacityTiers.ADVANCED, 100, FactocraftyArmorMaterials.GRAPHANO, equipmentSlot, properties);
    }
}
