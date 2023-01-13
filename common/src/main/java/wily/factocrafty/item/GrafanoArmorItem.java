package wily.factocrafty.item;

import net.minecraft.world.entity.EquipmentSlot;
import wily.factoryapi.base.FactoryCapacityTiers;

public class GrafanoArmorItem extends ElectricArmorItem {
    public GrafanoArmorItem(EquipmentSlot equipmentSlot, Properties properties) {
        super(FactoryCapacityTiers.ADVANCED, 100, FactocraftyArmorMaterials.GRAFANO, equipmentSlot, properties);
    }
}
