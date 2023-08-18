package wily.factocrafty.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public enum ArmorFeatures {
    NIGHT_VISION((e, i) -> e.getMaterial() == FactocraftyArmorMaterials.NIGHT_VISION && e.getEquipmentSlot() == EquipmentSlot.HEAD),
    SUPER_SPEED((e, i) -> e.getMaterial() == FactocraftyArmorMaterials.GRAPHANO && e.getEquipmentSlot() == EquipmentSlot.LEGS),
    GRAVITY_0((e, i) -> e.getMaterial() == FactocraftyArmorMaterials.QUANTUM && e.getEquipmentSlot() == EquipmentSlot.CHEST),
    SUPER_JUMP((e, i) -> e.getMaterial() == FactocraftyArmorMaterials.GRAPHANO && e.getEquipmentSlot() == EquipmentSlot.FEET);

    final BiPredicate<ElectricArmorItem, ItemStack> predicate;
    private BiConsumer<Player,ItemStack> tick = (p,i)->getDefaultTick(this).accept(p,i);
    public BiConsumer<Player,ItemStack> getTick(){
        return tick;
    }
    public static BiConsumer<Player,ItemStack> getDefaultTick(ArmorFeatures feature){
        return  (p,i)-> {if (p.getRandom().nextFloat() >= 0.7F && feature.isActive(i.getOrCreateTag()) && p.getItemBySlot(((ElectricArmorItem)i.getItem()).getEquipmentSlot()) == i) ((ElectricArmorItem)i.getItem()).getEnergyStorage(i).consumeEnergy(1,false);};
    }

    ArmorFeatures(BiPredicate<ElectricArmorItem, ItemStack> condition) {
        this.predicate = condition;
    }
   static {
        GRAVITY_0.tick = (p,i)-> {
            if (!p.isCreative() && !p.isSpectator()) {
                ElectricArmorItem e = ((ElectricArmorItem) i.getItem());
                boolean f = e.hasActiveFeature(GRAVITY_0, i, true) && p.getItemBySlot(e.getEquipmentSlot()) == i;
                if (f != p.getAbilities().mayfly) {
                    p.getAbilities().mayfly= f;
                    if (p.getAbilities().flying && !f) p.getAbilities().flying = false;
                    p.onUpdateAbilities();
                }
                getDefaultTick(GRAVITY_0).accept(p, i);
            }
        };
   }
    public boolean hasFeature(ElectricArmorItem item, ItemStack stack) {
        return predicate.test(item, stack);
    }

    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isActive(CompoundTag tag) {
        return tag.getBoolean(getName());
    }
}
