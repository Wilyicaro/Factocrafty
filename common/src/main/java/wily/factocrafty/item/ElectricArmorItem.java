package wily.factocrafty.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.ITagSerializable;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiPredicate;

public class ElectricArmorItem extends ArmorItem implements ICraftyEnergyItem<CYItemEnergyStorage> {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private final FactoryCapacityTiers energyTier;
    private final int capacity;
    private final ArmorMaterial armorMaterial;
    public ElectricArmorItem(FactoryCapacityTiers energyTier, int capacityMultiplier, ArmorMaterial armorMaterial, Type equipmentSlot, Properties properties) {
        super(armorMaterial, equipmentSlot, properties.defaultDurability(-1));
        this.energyTier = energyTier;
        this.capacity = armorMaterial.getDurabilityForType(equipmentSlot) * capacityMultiplier;
        this.armorMaterial = armorMaterial;
    }



    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(energyTier.getEnergyTierComponent(false));
        list.add( StorageStringUtil.getEnergyTooltip("tooltip.factory_api.energy_stored", getCraftyEnergy(itemStack)));
    }
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uUID = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        CYItemEnergyStorage cell = getCraftyEnergy(stack);
        float f = cell.getEnergyStored() != 0 ? ((float) cell.getEnergyStored() / cell.getMaxEnergyStored()) > 0.2F ? 1 : 0.4F : 0;
        if (f!= 0) {
            builder.put(Attributes.ARMOR, new AttributeModifier(uUID, "Armor modifier", f * getDefense(), AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uUID, "Armor toughness", f * getToughness(), AttributeModifier.Operation.ADDITION));
            if (hasActiveFeature(ArmorFeatures.SUPER_SPEED,stack,false))
                builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uUID, "Leggings super speed feature", f / 2, AttributeModifier.Operation.ADDITION));
            if (armorMaterial == FactocraftyArmorMaterials.GRAPHANO || armorMaterial == FactocraftyArmorMaterials.QUANTUM) {
                builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uUID, "Armor knockback resistance", f * this.knockbackResistance, AttributeModifier.Operation.ADDITION));
            }
        }

        return slot == getEquipmentSlot() ? builder.build() : ImmutableMultimap.of();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);

        for (ArmorFeatures f : ArmorFeatures.values()) {
            if (entity instanceof ServerPlayer p && hasFeature(f,itemStack,false))
                f.getTick().accept(p,itemStack);
        }
    }

    public boolean hasFeature(ArmorFeatures feature, ItemStack s, boolean needsEnergy){
        return feature.hasFeature(this,s) && (!needsEnergy || (getCraftyEnergy(s).getEnergyStored() / (float) getCraftyEnergy(s).getMaxEnergyStored() >= 0.1));
    }
    public boolean hasActiveFeature(ArmorFeatures feature, ItemStack s, boolean needsEnergy){
        return hasFeature(feature,s,needsEnergy) && feature.isActive(s.getOrCreateTag());
    }
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
      return getAttributeModifiers(slot,stack);
    }


    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getCraftyEnergy(itemStack).getSpace() > 0;}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getCraftyEnergy(itemStack).getEnergyStored() * 13.0F / (float)this.getCraftyEnergy(itemStack).getMaxEnergyStored());
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
}
