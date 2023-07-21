package wily.factocrafty.util.registering;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.FactocraftyArmorMaterials;
import wily.factocrafty.item.FactocraftyTiers;

import java.util.*;
import java.util.function.Predicate;

public class FactocraftyOre {
    public static class Derivative {

        public final String identifier;
        public final String prefix;
        public final String suffix;

        public String customName = "";

        public Derivative(String identifier) {
            this(identifier, true);
        }

        public Derivative(String identifier, boolean fix) {
            this(identifier, fix ? "" : identifier, fix ? identifier : "");
        }
        public Derivative(String identifier, String customName) {
            this(identifier, "","");
            this.customName = customName;
        }
        public Derivative withCustomName(String customName){
            return new Derivative(identifier,customName);
        }
        public Derivative withPrefix(String p){
            return new Derivative(identifier,p,suffix);
        }
        public Derivative withSuffix(String s){
            return new Derivative(identifier,prefix,s);
        }
        public String getPrefix() {
            return prefix.isEmpty() ? prefix : prefix + "_";
        }

        public String getSuffix() {
            return suffix.isEmpty() ? suffix : "_" + suffix;
        }

        public Derivative(String identifier, String prefix, String suffix) {
            this.identifier = identifier;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        public String getName(Material m) {
            return customName.isEmpty() ? getPrefix() + m.getName() + getSuffix() : customName;
        }

    }

    public static class VariantDerivative extends Derivative {
        public final List<Derivative> variants;

        public VariantDerivative(String identifier, boolean fix, List<Derivative> variants) {
            super(identifier, fix);
            this.variants = variants;
        }
        public VariantDerivative(String identifier,String prefix, String suffix, List<Derivative> variants) {
            super(identifier, prefix,suffix);
            this.variants = variants;
        }

        public String getName(Material m, int variant) {
            return getPrefix() + variants.get(variant).getName(m) + getSuffix();
        }

        public String getName(Material m, Derivative variant) {
            return getPrefix() + variants.get(variants.indexOf(variant)).getName(m) + getSuffix();
        }
    }

    public static class OreDerivative extends VariantDerivative {

        public final int averageXp;
        public final int count;
        public final int countPerChunk;
        public final int extraCountPerChunk;
        public final int extraMinY;
        public final int extraMaxY;
        public final int minY;
        public final int maxY;
        public final boolean common;
        public final boolean deep;

        public OreDerivative(int averageXP, int count, int countPerChunk, int minY, int maxY, int extraCountPerChunk, int extraMinY, int extraMaxY, boolean common, boolean deep) {
            super("ore", true, List.of(COMMON_DROP, new Derivative("deepslate", false)));
            this.averageXp = averageXP;
            this.count = count;
            this.countPerChunk = countPerChunk;
            this.minY = minY;
            this.maxY = maxY;
            this.common = common;
            this.deep = deep;
            this.extraCountPerChunk = extraCountPerChunk;
            this.extraMinY = extraMinY;
            this.extraMaxY = extraMaxY;
        }

        public OreDerivative(int averageXP, int count, int countPerChunk, int minY, int maxY, boolean common, boolean deep) {
            this(averageXP, count, countPerChunk, minY, maxY, countPerChunk * 7, minY < 0 ? -minY / 2 : minY * 3, maxY < 0 ? -maxY / 2 : maxY * 4, common, deep);
        }
    }

    public static Derivative COMMON_DROP = new Derivative("common_drop", "", "");
    public static Derivative REFINED = new Derivative("refined", false);
    public static Derivative DUST = new Derivative("dust");
    public static Derivative INGOT = new Derivative("ingot");
    public static Derivative NUGGET = new Derivative("nugget");
    public static Derivative BLOCK = new Derivative("block");
    public static VariantDerivative RAW = new VariantDerivative("raw", false, List.of(COMMON_DROP, BLOCK));
    public static Derivative CRUSHED = new Derivative("crushed", false);
    public static Derivative PLATE = new Derivative("plate");

    public static VariantDerivative ORE = new VariantDerivative("ore", true, List.of(COMMON_DROP, new Derivative("deepslate", false)));
    public static Derivative ROD = new Derivative("rod");

    public static OreDerivative DEFAULT_ORE = new OreDerivative(0, 8, 10, -64, 120, true, true);

    public static OreDerivative easyOre(int count, int countPerChunk, int minY, int maxY) {
        return new OreDerivative(0, count, countPerChunk, minY, maxY, countPerChunk, minY, maxY, true, true);
    }

    public static OreDerivative deepOre(int averageXP, int count, int countPerChunk, int minY, int maxY) {
        return new OreDerivative(averageXP, count, countPerChunk, minY, maxY, false, true);
    }

    public static OreDerivative normalOre(int averageXP, int count, int countPerChunk, int minY, int maxY) {
        return new OreDerivative(averageXP, count, countPerChunk, minY, maxY, true, false);
    }

    public static Derivative[] common = new Derivative[]{DEFAULT_ORE, BLOCK, INGOT, NUGGET, RAW, DUST, CRUSHED, PLATE};

    public static Derivative[] commonDerivatives(int count, int countPerChunk, int minY, int maxY) {
        return new Derivative[]{easyOre(count, countPerChunk, minY, maxY), BLOCK, INGOT, NUGGET, RAW, DUST, CRUSHED, PLATE};
    }

    public enum Material {
        EMPTY(MapColor.NONE),

        COAL(MapColor.COLOR_BLACK,List.of(BLOCK,COMMON_DROP,ORE), DUST, CRUSHED),
        COPPER(MapColor.COLOR_ORANGE,List.of(BLOCK,INGOT,ORE),DUST, CRUSHED,PLATE,NUGGET),
        IRON(MapColor.METAL,List.of(BLOCK,INGOT,NUGGET,ORE),DUST,REFINED,CRUSHED,PLATE,ROD),
        GOLD(MapColor.GOLD,List.of(BLOCK,INGOT,NUGGET,ORE),DUST, CRUSHED, PLATE),
        DIAMOND(MapColor.DIAMOND,List.of(BLOCK,COMMON_DROP,ORE),DUST, NUGGET, CRUSHED),
        REDSTONE(MapColor.COLOR_RED,List.of(BLOCK,COMMON_DROP,ORE),CRUSHED),
        LAPIS(MapColor.LAPIS,List.of(BLOCK,new Derivative("common_drop","","lazuli"),ORE),CRUSHED,DUST),
        EMERALD(MapColor.EMERALD,List.of(BLOCK,COMMON_DROP,ORE),DUST, NUGGET, CRUSHED),
        NETHERITE(MapColor.COLOR_BLACK,List.of(INGOT),DUST, NUGGET),
        TIN(MapColor.METAL, null, null, common),
        BRONZE(MapColor.COLOR_ORANGE, FactocraftyArmorMaterials.BRONZE, FactocraftyTiers.BRONZE, INGOT, NUGGET, BLOCK, DUST, PLATE),
        SILVER(MapColor.METAL, null, null, commonDerivatives(5, 6, -64, 72)),
        LEAD(MapColor.METAL, null, null, commonDerivatives(8, 8, -64, 50)),
        PLATINUM(MapColor.COLOR_LIGHT_BLUE, FactocraftyArmorMaterials.PLATINUM, FactocraftyTiers.PLATINUM, normalOre(0, 6, 10, 20, 80), BLOCK, INGOT, NUGGET, RAW, DUST, CRUSHED, PLATE),
        URANIUM(MapColor.COLOR_LIGHT_GREEN,  deepOre(4, 4, 8, -64, 10), BLOCK, REFINED, NUGGET, DUST, COMMON_DROP, CRUSHED, PLATE),
        RUBY(MapColor.COLOR_RED, FactocraftyArmorMaterials.RUBY, FactocraftyTiers.RUBY, new OreDerivative(5, 5, 3, -80, 25, 5, -50, 90, true, true), BLOCK, NUGGET, DUST, COMMON_DROP, CRUSHED),
        STONE(MapColor.STONE,DUST),
        DEEPSLATE(MapColor.DEEPSLATE,DUST),
        RUBBER(MapColor.COLOR_BLACK, FactocraftyArmorMaterials.RUBBER, null,COMMON_DROP),
        SYNTHETIC_RUBBER(MapColor.COLOR_BLACK, COMMON_DROP),
        WOOD(MapColor.WOOD,DUST.withCustomName("sawdust")),
        SILICON(MapColor.COLOR_LIGHT_GRAY,COMMON_DROP,REFINED,NUGGET.withSuffix("fragment")),
        GRAPHENE(MapColor.COLOR_GRAY, Rarity.UNCOMMON,COMMON_DROP, DUST),
        GRAPHITE(MapColor.COLOR_BLACK,INGOT),
        IRIDIUM(MapColor.TERRACOTTA_WHITE, Rarity.EPIC,COMMON_DROP, PLATE, NUGGET),
        STEEL(MapColor.COLOR_GRAY,BLOCK,INGOT,PLATE,NUGGET,DUST);


        private final MapColor color;
        private final ArmorMaterial armor;

        private final Rarity rarity;
        private final net.minecraft.world.item.Tier tier;

        public final List<Derivative> derivatives;
        public List<Derivative> registeredDerivatives =  List.of();
        public static LoadingCache<Material,Ingredient> ingCache = CacheBuilder.newBuilder().maximumSize(FactocraftyOre.Material.values().length).build(new CacheLoader<>() {
            public @NotNull Ingredient load(FactocraftyOre.Material key) {
                return key.getIngredient();
            }
        });;
        Material(MapColor color, Rarity rarity, @Nullable ArmorMaterial armor, @Nullable net.minecraft.world.item.Tier tier, Derivative... derivatives) {
            this.color = color;
            this.armor = armor;
            this.tier = tier;
            this.derivatives = List.of(derivatives);
            this.rarity = rarity;

        }

        Material(MapColor color, @Nullable ArmorMaterial armor, @Nullable net.minecraft.world.item.Tier tier, Derivative... derivatives) {
            this(color, Rarity.COMMON, armor,tier,derivatives);
        }
        Material(MapColor color, Derivative... derivatives) {
            this(color, null, null, derivatives);
        }
        Material(MapColor color, Rarity rarity, Derivative... derivatives) {
            this(color, rarity,null, null, derivatives);
        }
        Material(MapColor color, List<Derivative> registeredDerivatives,Derivative... derivatives) {
            this(color, derivatives);
            this.registeredDerivatives = registeredDerivatives;

        }

        public Rarity getRarity() {
            return rarity;
        }

        public boolean isEmpty(){
            return this == EMPTY;
        }
        public static Material findItemMaterial(ItemStack stack){
            for (Material m : values()) {
                if (getIngredient(m).test(stack)) return m;
            }
            return EMPTY;
        }
        public <D extends Derivative> Optional<D> getDerivative(D derivative) {
            return getDerivative(derivative.identifier);
        }
        public static Material byName(String name) {
            return valueOf(name.toUpperCase());
        }

        public <D extends Derivative> Optional<D> getDerivative(String identifier) {
            return getDerivative(identifier,false);
        }
        public <D extends Derivative> Optional<D> getDerivative(String identifier, boolean allowRegistered) {
            for (Derivative d : derivatives) {
                if (d.identifier.equals(identifier)) return Optional.of((D) d);
            }
            if (allowRegistered)for (Derivative d : registeredDerivatives) {
                if (d.identifier.equals(identifier)) return Optional.of((D) d);
            }
            return Optional.empty();
        }
        public MutableComponent getComponent(){
            return isEmpty() ?  Component.translatable("tooltip.factory_api.empty") : Component.translatable("material.factocrafty." +getName());
        }

        public boolean hasDerivative(Derivative derivatives) {
            return getDerivative(derivatives).isPresent();
        }
        public static Ingredient getIngredient(Material m){
            ArrayList<ItemLike> list = new ArrayList<>();
            Predicate<Derivative> p = d-> !Objects.equals(d.identifier, "ore") && !Objects.equals(d.identifier, "raw") && !Objects.equals(d.identifier, "crushed");
            m.derivatives.stream().filter(p).forEach(d-> list.add(Registration.ITEMS_REGISTRAR.get(Registration.getModResource(d.getName(m)))));
            if (m.ordinal() <= 8)m.registeredDerivatives.stream().filter(p).forEach(d-> list.add(Registration.ITEMS_REGISTRAR.get(new ResourceLocation(d.getName(m)))));

            return Ingredient.of(list.toArray(new ItemLike[0]));
        }
        public Ingredient getIngredient(){
            return getIngredient(this);
        }

        public MapColor getColor() {
            return this.color;
        }

        public ArmorMaterial getArmor() {
            return this.armor;
        }

        public net.minecraft.world.item.Tier getToolTier() {
            return this.tier;
        }

        public String getName() {
            return this.name().toLowerCase();
        }

        public String getOreName(boolean isDeep) {
            return (isDeep ? "deepslate_" : "") + this.getName() + "_ore";
        }

        public Block getOre(boolean isDeep) {
            return Registration.getRegistrarBlockEntry(getOreName(isDeep));
        }

    }

}
