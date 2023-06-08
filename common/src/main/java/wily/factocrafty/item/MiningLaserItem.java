package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.entity.LaserProjectile;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;
import java.util.function.Predicate;

public class MiningLaserItem extends ProjectileWeaponItem implements ICraftyEnergyItem<CYItemEnergyStorage> {

    private final FactoryCapacityTiers energyTier;
    private final int capacity;

    public MiningLaserItem(FactoryCapacityTiers energyTier, Properties properties) {
        super(properties);
        this.energyTier = energyTier;
        this.capacity = energyTier.energyCapacity;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (I)-> false;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if  (getCraftyEnergy(itemStack).getEnergyStored() >= 100) {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        }else  return InteractionResultHolder.fail(itemStack);
    }
    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 6000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 10;
    }
    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getCraftyEnergy(itemStack).getSpace() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getCraftyEnergy(itemStack).getEnergyStored() * 13.0F / (float)this.getCraftyEnergy(itemStack).getMaxEnergyStored());
    }
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }
    public int getBarColor(ItemStack itemStack) {
        return Mth.hsvToRgb(0.5F, 1.0F, 1.0F);
    }
    @Override
    public CYItemEnergyStorage getCraftyEnergy(ItemStack stack) {
        return new CYItemEnergyStorage(stack,0,capacity, TransportState.INSERT, energyTier);
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return energyTier;
    }

    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            CYItemEnergyStorage cell = getCraftyEnergy(itemStack);
            boolean bl = player.getAbilities().instabuild;
                    if (cell.getEnergyStored() >= 100) {
                        if (!level.isClientSide) {
                            LaserProjectile laserProjectile = new LaserProjectile(level, player);
                            laserProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.7F, 0.0F);
                            level.addFreshEntity(laserProjectile);
                        }

                        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                        if (!bl) {
                            cell.consumeEnergy(100, false);
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                }

    }
}
