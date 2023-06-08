package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;

public class MachineUpgradeItem extends FactocraftyUpgradeItem{
    public MachineUpgradeItem(Properties properties, UpgradeType Type) {
        super(properties, Type);
    }

    public MachineUpgradeItem(Properties properties, UpgradeType Type, Component tooltip) {
        super(properties, Type, tooltip);
    }

    public boolean isValid(FactocraftyProcessBlockEntity be) {
        return!( be instanceof FactocraftyEnergyStorageBlockEntity || be instanceof FactocraftyFluidTankBlockEntity) && super.isValid(be);
    }
}
