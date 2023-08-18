package wily.factocrafty.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.*;

import java.util.function.Supplier;

public class FactocraftyCYItemSlot extends FactoryItemSlot {


    private final Supplier<FactoryCapacityTiers> energyTier;
    public static ResourceLocation CRAFTY_SLOT_EMPTY = Registration.getModResource("item/crafty_slot");

    public FactocraftyCYItemSlot(FactocraftyMenuBlockEntity be, int i, int j, int k, TransportState canIE,SlotsIdentifier identifier,  Supplier<FactoryCapacityTiers> energyTier) {
        super(be.inventory, identifier,canIE,i, j, k);
        this.energyTier = energyTier;
    }
    public FactocraftyCYItemSlot(FactocraftyMenuBlockEntity be, int i, int j, int k, TransportState canIE, FactoryCapacityTiers energyTier) {
        this(be,i, j, k,canIE,SlotsIdentifier.ENERGY,()-> energyTier);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (stack.getItem() instanceof ICraftyStorageItem cy && (cy.getEnergyStorage(stack).getTransport().canExtract() && transportState.canInsert()|| cy.getEnergyStorage(stack).getTransport().canInsert() && transportState.canExtract())) && cy.getSupportedEnergyTier().supportTier(energyTier.get()) && super.mayPlace(stack);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS,CRAFTY_SLOT_EMPTY);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
