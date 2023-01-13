package wily.factocrafty.block.storage.energy.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

public class FactocraftyEnergyStorageBlockEntity extends FactocraftyProcessBlockEntity {
    public FactocraftyEnergyStorageBlockEntity(FactoryCapacityTiers energyTier, BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.ENERGY_CELL, energyTier, FactocraftyBlockEntities.ofBlock(blockState.getBlock()), blockPos, blockState);
        this.energyStorage = new CYEnergyStorage(this, 0,energyTier.getStorageCapacity(), (int)(energyTier.energyCapacity * energyTier.getConductivity()), energyTier);
        for (BlockSide side : BlockSide.values())
            replaceSidedStorage(side,energySides, TransportState.EXTRACT_INSERT);
        replaceSidedStorage(BlockSide.BACK,energySides, TransportState.EXTRACT);
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.INSERT);
        FILL_SLOT = 0;
        DRAIN_SLOT = 1;
    }

    @Override
    public boolean hasInventory() {
        return true;
    }

    @Override
    public boolean hasEnergyCell() {
        return true;
    }

    @Override
    public Component getDisplayName() {
        String[] s = super.getDisplayName().getString().split(" ");

        return Component.literal(s[s.length -1].replaceAll("[()]","")).setStyle(super.getDisplayName().getStyle());
    }

    @Override
    public void tick() {
        super.tick();
        for  (Direction d : Direction.values()){
            if (level.getBlockEntity(getBlockPos().relative(d)) instanceof IFactoryStorage storage) {
                if (energySides.get(d).canInsert() && storage.energySides().isPresent() && storage.energySides().get().get(d.getOpposite()).canExtract() ) transferEnergyFrom(d,storage.getStorage(Storages.CRAFTY_ENERGY,d.getOpposite()).get());
            }
        }
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {

        slots.add(new FactocraftyCYItemSlot(this, FILL_SLOT, 61,17, TransportState.INSERT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyCYItemSlot(this, DRAIN_SLOT, 61,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
    }
}
