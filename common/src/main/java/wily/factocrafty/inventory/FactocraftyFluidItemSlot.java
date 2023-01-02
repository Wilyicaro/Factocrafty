package wily.factocrafty.inventory;

import net.minecraft.world.item.ItemStack;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

public class FactocraftyFluidItemSlot extends FactoryItemSlot {


    public TransportState transportState;
    public FactocraftyFluidItemSlot(FactocraftyProcessBlockEntity be, int i, int j, int k, TransportState canIE) {
        super(be.inventory, SlotsIdentifier.GENERIC,canIE,i, j, k);
        transportState = canIE;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (ItemContainerUtil.isFluidContainer(stack)) && super.mayPlace(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
