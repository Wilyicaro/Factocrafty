package wily.factocrafty.inventory;

import net.minecraft.world.item.ItemStack;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factoryapi.base.*;

public class FactocraftyCYItemSlot extends FactoryItemSlot {


    private final FactoryCapacityTiers energyTier;

    public FactocraftyCYItemSlot(FactocraftyProcessBlockEntity be, int i, int j, int k, TransportState canIE, FactoryCapacityTiers energyTier) {
        super(be.inventory, SlotsIdentifier.AQUA,canIE,i, j, k);
        this.energyTier = energyTier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (stack.getItem() instanceof ICraftyEnergyItem<?> cy && (cy.getCraftyEnergy(stack).getTransport().canExtract() && transportState.canExtract()|| cy.getCraftyEnergy(stack).getTransport().canInsert() && transportState.canInsert())) && energyTier.supportTier(cy.getEnergyTier()) && super.mayPlace(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
