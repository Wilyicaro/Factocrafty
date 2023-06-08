package wily.factocrafty.block.entity;

import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.inventory.UpgradeList;
import wily.factocrafty.item.UpgradeType;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factocrafty.network.FactocraftySyncSelectedUpgradePacket;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.List;

public class FactocraftyProcessBlockEntity extends FactocraftyStorageBlockEntity implements ExtendedMenuProvider, IFactoryProcessableStorage {
    private final FactocraftyMenus menu;
    protected final FactoryCapacityTiers defaultEnergyTier;

    public final List<Bearer<Integer>> additionalSyncInt = new ArrayList<>();
    public FactocraftyProcessBlockEntity(FactocraftyMenus menu, FactoryCapacityTiers energyTier, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(blockEntity, blockPos, blockState);
        this.menu = menu;
        defaultEnergyTier = energyTier;
        this.energyStorage = new CYEnergyStorage(this, 0, getInitialEnergyCapacity(), (int)(energyTier.energyCapacity * energyTier.getConductivity()), energyTier);

    }
    public UpgradeList storedUpgrades = UpgradeList.create();
    public int selectedUpgrade = -1;

    public int getInitialEnergyCapacity(){
        return defaultEnergyTier.getDefaultCapacity();
    }
    public int getProgress() {
        return 1;
    }


    public Progress progress = new Progress(Progress.Identifier.DEFAULT, getProgress(), 200);

    @Override
    public void addProgresses(List<Progress> list) {}

    public boolean hasInventory(){
        return true;
    }

    public boolean hasEnergyCell(){
        return true;
    }

    @Override
    public void saveTag(CompoundTag compoundTag) {
        IFactoryProcessableStorage.super.saveTag(compoundTag);
        ListTag upgradeItems = new ListTag();
        storedUpgrades.forEach((i)-> upgradeItems.add(i.save(new CompoundTag())));
        compoundTag.putInt("selectedUpgrade",Math.min(storedUpgrades.size() - 1,selectedUpgrade));
        compoundTag.put("StoredUpgrades", upgradeItems);
        if (!additionalSyncInt.isEmpty()) compoundTag.putIntArray("additionalInt", additionalSyncInt.stream().map(Bearer::get).toList());
    }

    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player){
        Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncSelectedUpgradePacket(getBlockPos(),selectedUpgrade));
        for (Bearer<Integer> b : additionalSyncInt) Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncIntegerBearerPacket(getBlockPos(), b.get(),additionalSyncInt.indexOf(b)));
    }

    @Override
    public void load(CompoundTag compoundTag) {
        IFactoryProcessableStorage.super.loadTag(compoundTag);
        compoundTag.getList("StoredUpgrades",10).forEach((t ->{if (t instanceof CompoundTag cT && storedUpgrades.stream().noneMatch(i-> i.sameItem(ItemStack.of(cT))))storedUpgrades.add(ItemStack.of(cT));}));
        this.selectedUpgrade = Math.min(storedUpgrades.size() - 1,compoundTag.getInt("selectedUpgrade"));
        int[] ar = compoundTag.getIntArray("additionalInt");
        for (int i = 0; i < ar.length; i++) additionalSyncInt.get(i).set(ar[i]);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public void tick() {
        super.tick();
        energyStorage.supportableTier = FactoryCapacityTiers.values()[defaultEnergyTier.ordinal() + (int)storedUpgrades.getUpgradeEfficiency(UpgradeType.TRANSFORMER) * (FactoryCapacityTiers.values().length - 1 -  defaultEnergyTier.ordinal())];
        energyStorage.capacity = getInitialEnergyCapacity() + (int)(storedUpgrades.getUpgradeEfficiency(UpgradeType.ENERGY) * 40000);
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
