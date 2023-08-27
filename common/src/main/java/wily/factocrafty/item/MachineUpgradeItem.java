package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.block.machines.FactocraftyEnergyTransformerBlock;
import wily.factocrafty.block.machines.entity.FactocraftyEnergyTransformerBlockEntity;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.block.storage.fluid.entity.FactocraftyFluidTankBlockEntity;

public class MachineUpgradeItem extends FactocraftyUpgradeItem{
    public MachineUpgradeItem(Properties properties, UpgradeType Type) {
        super(properties, Type);
    }

    public MachineUpgradeItem(Properties properties, UpgradeType Type, Component tooltip) {
        super(properties, Type, tooltip);
    }

    @Override
    public boolean isValid(FactocraftyStorageBlockEntity be) {
        return be.hasEnergyCell() && !(be instanceof FactocraftyEnergyStorageBlockEntity || be instanceof FactocraftyEnergyTransformerBlockEntity) && super.isValid(be);
    }
}
