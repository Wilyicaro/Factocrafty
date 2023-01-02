package wily.factocrafty.item;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class BatteryItem extends EnergyItem{
    public BatteryItem(FactoryCapacityTiers tier, int Capacity, Properties properties) {
        super(tier, TransportState.EXTRACT_INSERT, properties.stacksTo(64));
        capacity = Capacity;
    }
    public float getChargedLevel(ItemStack stack){
        int e = getCraftyEnergy(stack).getEnergyStored();
        if (e > 0 ) {
            return e /(float)capacity;
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return capacity > FactoryCapacityTiers.HIGH.energyCapacity && super.isBarVisible(itemStack);
    }
}
