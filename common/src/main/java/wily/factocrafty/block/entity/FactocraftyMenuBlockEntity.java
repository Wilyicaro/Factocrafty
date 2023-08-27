package wily.factocrafty.block.entity;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.inventory.UpgradeList;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.item.UpgradeType;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.network.FactocraftySyncUpgradeStorage;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class FactocraftyMenuBlockEntity extends FactocraftyStorageBlockEntity implements ExtendedMenuProvider {
    private final MenuType<?> menu;
    public final List<Bearer<Integer>> additionalSyncInt = new ArrayList<>();
    public FactocraftyMenuBlockEntity(MenuType<?> menu, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        this.menu = menu;
        this.energyStorage = new CYEnergyStorage(this,getInitialEnergyCapacity(), (int)(getDefaultEnergyTier().initialCapacity * getDefaultEnergyTier().getConductivity()),getDefaultEnergyTier());
        additionalSyncInt.add(selectedUpgrade);
        this.storedUpgrades = new UpgradeList(){
            @Override
            public void setChanged(int index, boolean removed, ItemStack upgradeStack) {
                if (removed) selectedUpgrade.set(-1);
                if (upgradeStack.getItem() instanceof FactocraftyUpgradeItem upg){
                    if (upg.upgradeType == UpgradeType.ENERGY)
                        energyStorage.capacity = (int) (getInitialEnergyCapacity() * (storedUpgrades.getUpgradeEfficiency(UpgradeType.ENERGY)*15 + 1));
                    if (upg.upgradeType == UpgradeType.TRANSFORMER)
                        energyStorage.setSupportedTier(FactoryCapacityTiers.values()[Math.min(5,getDefaultEnergyTier().ordinal() + (int)(storedUpgrades.getUpgradeEfficiency(UpgradeType.TRANSFORMER)*4))]);
                }
            }
        };
    }


    public int getInitialEnergyCapacity(){
        return getDefaultEnergyTier().getDefaultCapacity() / 2;
    }

    public boolean hasInventory(){
        return true;
    }

    public boolean hasEnergyCell(){
        return true;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        int[] ar = compoundTag.getIntArray("additionalInt");
        for (int i = 0; i < ar.length; i++) additionalSyncInt.get(i).set(ar[i]);
        super.load(compoundTag);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        if (!additionalSyncInt.isEmpty()) compoundTag.putIntArray("additionalInt", additionalSyncInt.stream().map(Bearer::get).toList());
        super.saveAdditional(compoundTag);
    }

    protected FactoryCapacityTiers getDefaultEnergyTier(){
        return this.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock b ? b.getEnergyTier() : FactoryCapacityTiers.BASIC;
    }
    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player){
        storedUpgrades.checkEmptyValues();
        if (hasUpgradeStorage())
            Factocrafty.NETWORK.sendToPlayer(player, new FactocraftySyncUpgradeStorage(getBlockPos(), storedUpgrades));
        for (Bearer<Integer> b : additionalSyncInt) Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncIntegerBearerPacket(getBlockPos(), b.get(),additionalSyncInt.indexOf(b)));
    }
    public void tick() {
        getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)->{
            getSlots(null).forEach(s->{
                if (ArrayUtils.contains(STORAGE_SLOTS,s.getContainerSlot()) && s instanceof FactocraftyCYItemSlot slot) {
                    ItemStack energyItem = inventory.getItem(s.getContainerSlot());
                    if (energyItem.getItem() instanceof ICraftyStorageItem cy) {
                        ICraftyEnergyStorage storage = cy.getEnergyStorage(energyItem);
                        if (storage.getTransport().canInsert() && slot.transportState.canExtract()) transferEnergyTo(this,null,storage);
                        if (storage.getTransport().canExtract() && slot.transportState.canInsert()) transferEnergyFrom(this,null,storage);
                    }
                }});
        });
        getStorage(Storages.ENERGY).ifPresent((e)->
            getSlots(null).forEach(s->{
                if (ArrayUtils.contains(STORAGE_SLOTS,s.getContainerSlot()) && s instanceof FactocraftyCYItemSlot slot && slot.acceptPlatformEnergy) {
                    ItemStack energyItem = inventory.getItem(s.getContainerSlot());
                    if (slot.transportState.canExtract() && ItemContainerUtil.isEnergyContainer(energyItem) && FactoryAPIPlatform.getItemEnergyStorage(energyItem).getSpace() > 0)
                        e.consumeEnergy(ItemContainerUtil.insertEnergy(ItemContainerUtil.getEnergy(energyItem), energyItem).contextEnergy(),false);
                }})
        );
        getTanks().forEach((tank)->{
            getSlots(null).forEach(s->{
                if (ArrayUtils.contains(STORAGE_SLOTS,s.getContainerSlot()) && s instanceof FactocraftyFluidItemSlot slot) {
                    ItemStack stack = inventory.getItem(s.getContainerSlot());
                    if (ItemContainerUtil.isFluidContainer(stack)) {
                        ItemContainerUtil.ItemFluidContext context = null;
                        if (slot.transportState.canInsert() && !ItemContainerUtil.getFluid(stack).isEmpty()) context = ItemContainerUtil.drainItem(tank.fill(ItemContainerUtil.getFluid(stack),false), stack);
                        if (slot.transportState.canExtract() && !tank.getFluidStack().isEmpty()) tank.drain((context = ItemContainerUtil.fillItem(stack,tank.getFluidStack())).fluidStack(),false);
                        if (context != null) inventory.setItem(s.getContainerSlot(),context.container());
                    }
                }
            });
        });
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new FactocraftyStorageMenu<>(menu, i,getBlockPos(),player);
    }


    @Override
    public void saveExtraData(FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
    }
}
