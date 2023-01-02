package wily.factocrafty.item;

import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import wily.factocrafty.util.registering.FactocraftyItems;

import java.util.function.Supplier;

public enum FactocraftyTiers implements Tier {

    BRONZE(2, 400, 5.0f, 2.3f, 9, () -> Ingredient.of(FactocraftyItems.BRONZE_INGOT.get())),
    PLATINUM(2, 727, 8.0f, 2.7f, 11, () -> Ingredient.of(FactocraftyItems.PLATINUM_INGOT.get())),
    RUBY(3, 1323, 7.4f, 3.0f, 16, () -> Ingredient.of(FactocraftyItems.RUBY.get()));


    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private FactocraftyTiers(int j, int k, float f, float g, int l, Supplier<Ingredient> supplier) {
        this.level = j;
        this.uses = k;
        this.speed = f;
        this.damage = g;
        this.enchantmentValue = l;
        this.repairIngredient = new LazyLoadedValue<Ingredient>(supplier);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
