package wily.factocrafty.block.entity;

import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FactocraftyProcessBlockEntity extends FactocraftyStorageBlockEntity implements ExtendedMenuProvider, IFactoryProcessableStorage {
    private final FactocraftyMenus menu;
    public FactocraftyProcessBlockEntity(FactocraftyMenus menu, FactoryCapacityTiers energyTier, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        this.menu = menu;
        this.energyStorage = new CYEnergyStorage(this, 0,energyTier.getDefaultCapacity(), (int)(energyTier.energyCapacity * energyTier.getConductivity()), energyTier);

    }


    public int getProgress() {
        return 1;
    }


    public Progress progress = new Progress(FactocraftyProgressType.PROGRESS, getProgress(), 200);

    @Override
    public void addProgresses(List<Progress> list) {}

    public boolean hasInventory(){
        return true;
    }

    public boolean hasEnergyCell(){
        return true;
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        IFactoryProcessableStorage.super.saveAdditional(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        IFactoryProcessableStorage.super.load(compoundTag);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FactocraftyProcessMenu<>(menu, i,getBlockPos(),player);
    }


    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
    }
}
