package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class ElectricWrenchItem extends WrenchItem implements ICraftyStorageItem,FactocraftyDiggerItem {


    private final FactoryCapacityTiers energyTier;

    public ElectricWrenchItem(FactoryCapacityTiers energyTier, Properties properties) {
        super(properties);
        this.energyTier = energyTier;
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(getSupportedEnergyTier().getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getEnergyStorage(itemStack)));
    }
    public boolean isActivated(ItemStack itemStack){
        return itemStack.getOrCreateTag().getBoolean("activated");
    }

    @Override
    protected boolean canUseWrench(ItemStack stack) {
        return isActivated(stack) && getEnergyStorage(stack).getEnergyStored() > 0;
    }

    @Override
    protected void whenUseWrench(int used, UseOnContext useOnContext) {
        getEnergyStorage(useOnContext.getItemInHand()).consumeEnergy(new CraftyTransaction(used, energyTier),false);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack enabled = player.getItemInHand(interactionHand);
        enabled.getOrCreateTag().putBoolean("activated",!isActivated(enabled));
        return InteractionResultHolder.sidedSuccess(enabled,!level.isClientSide);
    }
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean bl) {
        if (isActivated(stack) && !level.isClientSide && level.random.nextFloat() <= 0.7 && entity.tickCount % 18 == 0)
            getEnergyStorage(stack).consumeEnergy(1,false);
        super.inventoryTick(stack, level, entity, i, bl);
    }


    @Override
    public int getCapacity() {
        return getSupportedEnergyTier().initialCapacity;
    }

    @Override
    public TransportState getTransport() {
        return TransportState.INSERT;
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getEnergyStorage(itemStack).getEnergySpace() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getEnergyStorage(itemStack).getEnergyStored() * 13.0F / (float)this.getEnergyStorage(itemStack).getMaxEnergyStored());
    }

    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }

    @Override
    public FactoryCapacityTiers getSupportedEnergyTier() {
        return energyTier;
    }
}
