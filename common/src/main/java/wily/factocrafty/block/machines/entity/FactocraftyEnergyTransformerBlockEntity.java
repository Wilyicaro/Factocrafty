package wily.factocrafty.block.machines.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.item.UpgradeType;
import wily.factocrafty.util.registering.IFactocraftyBlockEntityType;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;
import java.util.Optional;

import static wily.factoryapi.util.StorageUtil.transferEnergyFrom;
import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class FactocraftyEnergyTransformerBlockEntity extends FactocraftyEnergyStorageBlockEntity implements IFactoryProgressiveStorage{
    public FactocraftyEnergyTransformerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.ENERGY_TRANSFORMER_MENU.get(), IFactocraftyBlockEntityType.ofBlock(blockState.getBlock()), blockPos, blockState);
        additionalSyncInt.add(conversionMode);
        energyStorage = new CYEnergyStorage(this,getInitialEnergyCapacity(), (int)(getDefaultEnergyTier().initialCapacity * getDefaultEnergyTier().getConductivity()),getDefaultEnergyTier()){
            @Override
            public CraftyTransaction consumeEnergy(CraftyTransaction transaction, boolean simulate) {
                if (transaction.isEmpty()) return CraftyTransaction.EMPTY;
                transaction.tier = getConversionTier().supportTier(transaction.tier)? transaction.tier : getConversionTier();
                CraftyTransaction transaction1 = super.consumeEnergy(transaction, simulate);

                return new CraftyTransaction(transaction1.energy,transaction.tier);
            }
        };
    }
    public enum ConversionMode{
        LOWER_TIER,PLATFORM_ENERGY;
        public String getName(){
            return name().toLowerCase();
        }
        public boolean isPlatform(){
            return this == PLATFORM_ENERGY;
        }
        public Component getComponent(){
            return this == PLATFORM_ENERGY ? FactoryAPIPlatform.getPlatformEnergyComponent() : Component.translatable("tooltip.factocrafty.config.conversion_mode." + getName()).withStyle(ChatFormatting.LIGHT_PURPLE);
        }
    }
    public Progress progress = new Progress(Progress.Identifier.DEFAULT,80,39,20);
    public Bearer<Integer> conversionMode = Bearer.of(0);
    public ConversionMode getConversionMode(){
        return ConversionMode.values()[conversionMode.get()];
    }

    public IPlatformEnergyStorage<?> platformEnergyStorage = FactoryAPIPlatform.getEnergyStorageApi(getInitialEnergyCapacity(),this);
    @Override
    public int getInitialEnergyCapacity() {
        return getDefaultEnergyTier().initialCapacity;
    }

    public FactoryCapacityTiers getConversionTier(){
        return FactoryCapacityTiers.values()[Math.max(0,getDefaultEnergyTier().ordinal() - 1)];
    }
    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal(StorageStringUtil.getBetweenParenthesis(super.getDisplayName().getString())).setStyle(super.getDisplayName().getStyle());
    }

    @Override
    public void saveTag(CompoundTag compoundTag) {
        IFactoryProgressiveStorage.super.saveTag(compoundTag);
    }

    @Override
    public void loadTag(CompoundTag compoundTag) {
        IFactoryProgressiveStorage.super.loadTag(compoundTag);
    }
    @Override
    public <T extends IPlatformHandlerApi<?>> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
        if (storage == Storages.ENERGY && getConversionMode().isPlatform())
            return ()-> (T) FactoryAPIPlatform.filteredOf(platformEnergyStorage,energySides.getTransportOrDefault(direction,TransportState.EXTRACT));
        return super.getStorage(storage, direction);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            if (getConversionMode().isPlatform() && platformEnergyStorage.getSpace() > 0  && energyStorage.getEnergyStored() > 0){
                progress.first().add((int) Math.pow(20,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK)));
                if (progress.first().get() >= progress.first().maxProgress) {
                    progress.first().set(0);
                    if (getConversionMode().isPlatform()) {
                        energyStorage.consumeEnergy(platformEnergyStorage.receiveEnergy(energyStorage.getMaxConsume(), false), false);
                    }
                }

            }else progress.first().set(0);
        }
    }

    @Override
    public List<Progress> getProgresses() {
        return List.of(progress);
    }

    @Override
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = NonNullList.create();
        slots.add(new FactocraftyCYItemSlot(this, 0, 61,17, TransportState.EXTRACT, SlotsIdentifier.INPUT,this::getConversionTier,true){
            public int getCustomX() {
                return getConversionMode().isPlatform() ?  148 : 61;
            }
            public int getCustomY() {
                return getConversionMode().isPlatform() ?  53 : 17;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, 1, 61,53, TransportState.INSERT, SlotsIdentifier.OUTPUT, ()->energyStorage.getStoredTier()){
            public int getCustomX() {
                return getConversionMode().isPlatform() ?  13 : 61;
            }
        });
        return slots;
    }

}
