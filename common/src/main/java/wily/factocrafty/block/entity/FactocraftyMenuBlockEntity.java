package wily.factocrafty.block.entity;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
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
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.network.FactocraftySyncUpgradeStorage;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class FactocraftyMenuBlockEntity extends FactocraftyStorageBlockEntity implements ExtendedMenuProvider {
    private final MenuType<?> menu;
    public FactocraftyMenuBlockEntity(MenuType<?> menu, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        this.menu = menu;
        this.energyStorage = new CYEnergyStorage(this, 0, getInitialEnergyCapacity(), (int)(getDefaultEnergyTier().initialCapacity * getDefaultEnergyTier().getConductivity()),getDefaultEnergyTier());
        additionalSyncInt.add(selectedUpgrade);
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


    protected FactoryCapacityTiers getDefaultEnergyTier(){
        return this.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock b ? b.getEnergyTier() : FactoryCapacityTiers.BASIC;
    }
    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player){
        for (Bearer<Integer> b : additionalSyncInt) Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncIntegerBearerPacket(getBlockPos(), b.get(),additionalSyncInt.indexOf(b)));
        if (hasUpgradeStorage()) {
            if (!storedUpgrades.isEmpty()) {
                for (int i = 0; i < storedUpgrades.size(); i++)
                    Factocrafty.NETWORK.sendToPlayer(player, new FactocraftySyncUpgradeStorage(getBlockPos(), storedUpgrades.get(i), i));
            }else Factocrafty.NETWORK.sendToPlayer(player, new FactocraftySyncUpgradeStorage(getBlockPos(), ItemStack.EMPTY, -1));
        }
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
