package wily.factocrafty.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import wily.factocrafty.util.registering.FactocraftyItems;

import java.util.function.Supplier;

public enum FactocraftyArmorMaterials implements ArmorMaterial {
    BRONZE("bronze", 15, new int[]{3, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(FactocraftyItems.BRONZE_INGOT.get())),
    GRAPHANO("graphano", 15, new int[]{3, 7, 8, 4}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 1.5F, 0.8F, () -> Ingredient.of(FactocraftyItems.CARBON_PLATE.get())),
    NIGHT_VISION("night_vision", 15, new int[]{2, 3, 4, 1}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.1F, 0.2F, () -> Ingredient.of(FactocraftyItems.CARBON_PLATE.get())),
    QUANTUM("quantum", 15, new int[]{6, 14, 16, 8}, 9, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.2F, 1.4F, () -> Ingredient.of(FactocraftyItems.CARBON_PLATE.get())),
    JETPACK("jetpack", 1, new int[]{0, 0, 2, 0}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(FactocraftyItems.CARBON_PLATE.get())),
    PLATINUM("platinum", 15, new int[]{3, 5, 7, 4}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.1F, () -> Ingredient.of(FactocraftyItems.PLATINUM_INGOT.get())),
    RUBY("ruby", 30, new int[]{3, 6, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.5F, 0.0F, () -> Ingredient.of(FactocraftyItems.RUBY.get())),
    RUBBER("rubber", 8, new int[]{1, 2, 3, 1}, 13, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(FactocraftyItems.RUBBER.get()));

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    FactocraftyArmorMaterials(String p_i231593_3_, int p_i231593_4_, int[] p_i231593_5_, int p_i231593_6_, SoundEvent p_i231593_7_, float p_i231593_8_, float p_i231593_9_, Supplier<Ingredient> p_i231593_10_) {
        this.name = p_i231593_3_;
        this.durabilityMultiplier = p_i231593_4_;
        this.slotProtections = p_i231593_5_;
        this.enchantmentValue = p_i231593_6_;
        this.sound = p_i231593_7_;
        this.toughness = p_i231593_8_;
        this.knockbackResistance = p_i231593_9_;
        this.repairIngredient = new LazyLoadedValue<>(p_i231593_10_);
    }

    public int getDurabilityForType(ArmorItem.Type p_200896_1_) {
        return HEALTH_PER_SLOT[p_200896_1_.getSlot().getIndex()] * this.durabilityMultiplier;
    }

    public int getDefenseForType(ArmorItem.Type p_200902_1_) {
        return this.slotProtections[p_200902_1_.getSlot().getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }


    public String getName() {
        return name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
