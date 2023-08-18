package wily.factocrafty.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

public class FactocraftyFluidItemSlot extends FactoryItemSlot {



    public static ResourceLocation FLUID_SLOT_EMPTY = Registration.getModResource("item/fluid_slot");
    public FactocraftyFluidItemSlot(FactocraftyMenuBlockEntity be, int i, int j, int k, SlotsIdentifier identifier, TransportState canIE) {
        super(be.inventory, identifier,canIE,i, j, k);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (ItemContainerUtil.isFluidContainer(stack)) && super.mayPlace(stack);
    }
    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, FLUID_SLOT_EMPTY);
    }
    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
