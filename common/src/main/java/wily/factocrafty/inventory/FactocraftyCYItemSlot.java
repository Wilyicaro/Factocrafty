package wily.factocrafty.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.*;

public class FactocraftyCYItemSlot extends FactoryItemSlot {


    private final FactoryCapacityTiers energyTier;
    public static ResourceLocation CRAFTY_SLOT_EMPTY = Registration.getModResource("item/crafty");

    public FactocraftyCYItemSlot(FactocraftyProcessBlockEntity be, int i, int j, int k, TransportState canIE, FactoryCapacityTiers energyTier) {
        super(be.inventory, SlotsIdentifier.ENERGY,canIE,i, j, k);
        this.energyTier = energyTier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return (stack.getItem() instanceof ICraftyEnergyItem<?> cy && (cy.getCraftyEnergy(stack).getTransport().canExtract() && transportState.canExtract()|| cy.getCraftyEnergy(stack).getTransport().canInsert() && transportState.canInsert())) && energyTier.supportTier(cy.getEnergyTier()) && super.mayPlace(stack);
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
