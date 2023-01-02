package wily.factocrafty.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;

import java.util.List;
import java.util.Optional;

import static wily.factocrafty.item.FactocraftyOre.Derivative.OreDerivative;

public class FactocraftyOre {
    public static class Derivative {

        public final String identifier;

        public Derivative(String identifier) {
            this.identifier = identifier;
        }
        public static class OreDerivative extends Derivative {

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
                super("ore");
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
            public OreDerivative(int averageXP,int count, int countPerChunk, int minY, int maxY, boolean common, boolean deep) {
                this(averageXP,count,countPerChunk,minY,maxY,countPerChunk * 7, minY < 0 ? -minY /2 : minY * 3 , maxY < 0 ? -maxY /2 : maxY * 4,common,deep);
            }
        }

    }
    public static Derivative REFINED = new Derivative("refined");
    public static Derivative DUST = new Derivative("dust");
    public static Derivative INGOT = new Derivative("ingot");
    public static Derivative NUGGET = new Derivative("nugget");
    public static Derivative BLOCK = new Derivative("block");
    public static Derivative RAW = new Derivative("raw");
    public static Derivative CRUSHED = new Derivative("crushed");
    public static Derivative PLATE = new Derivative("plate");
    public static Derivative COMMON_DROP = new Derivative("common_drop");
    public static OreDerivative DEFAULT_ORE = new OreDerivative( 0,8,10, -64, 120, true, true);
    public static OreDerivative easyOre(int count, int countPerChunk, int minY, int maxY) {return new OreDerivative(0,count, countPerChunk, minY, maxY, countPerChunk,  minY, maxY, true, true);}

    public static OreDerivative deepOre(int averageXP,int count,int countPerChunk, int minY, int maxY) {return new OreDerivative(averageXP,count,countPerChunk, minY, maxY, false, true);}

    public static OreDerivative normalOre(int averageXP,int count,int countPerChunk, int minY, int maxY) {return new OreDerivative(averageXP,count, countPerChunk, minY, maxY, true, false);}

    public static List<Derivative> common = ImmutableList.of(DEFAULT_ORE, BLOCK, INGOT, NUGGET, RAW, DUST, CRUSHED, PLATE);

    public static List<Derivative> commonDerivatives(int count,int countPerChunk, int minY, int maxY){return ImmutableList.of(easyOre(count,countPerChunk,minY,maxY), BLOCK, INGOT, NUGGET, RAW, DUST, CRUSHED, PLATE);};


    public enum Tier {
            TIN(MaterialColor.METAL, null, null, common),
            BRONZE(MaterialColor.COLOR_ORANGE, FactocraftyArmorMaterials.BRONZE, FactocraftyTiers.BRONZE, ImmutableList.of(INGOT, NUGGET, BLOCK, DUST,PLATE)),
            SILVER(MaterialColor.METAL, null, null, commonDerivatives(5,6,-64,  72)),
            LEAD(MaterialColor.METAL, null, null, commonDerivatives(8,8,-64,  50)),
            PLATINUM(MaterialColor.COLOR_LIGHT_BLUE, FactocraftyArmorMaterials.PLATINUM, FactocraftyTiers.PLATINUM, ImmutableList.of(normalOre(0,10,14,20,80),BLOCK,INGOT,NUGGET,RAW,DUST,CRUSHED,PLATE)),
            URANIUM(MaterialColor.COLOR_GREEN, null, null, ImmutableList.of(deepOre(4,4,8,-64,10),BLOCK,REFINED,NUGGET,DUST,COMMON_DROP, CRUSHED, PLATE)),
            RUBY(MaterialColor.COLOR_RED, FactocraftyArmorMaterials.RUBY, FactocraftyTiers.RUBY, ImmutableList.of(new OreDerivative(5,6,5, -80,25,7, -50,90,true,true),BLOCK,NUGGET,DUST,COMMON_DROP, CRUSHED)),
            COAL( ImmutableList.of(DUST, CRUSHED)),
            COPPER( ImmutableList.of(DUST, CRUSHED,PLATE)),
            IRON( ImmutableList.of(DUST,REFINED, CRUSHED,PLATE)),
            GOLD( ImmutableList.of(DUST, CRUSHED,PLATE)),
            DIAMOND( ImmutableList.of(DUST,NUGGET, CRUSHED)),
            EMERALD( ImmutableList.of(DUST,NUGGET, CRUSHED)),
            NETHERITE( ImmutableList.of(DUST,NUGGET)),
            STONE( ImmutableList.of(DUST)),
            DEEPSLATE(ImmutableList.of(DUST)),
            RUBBER(null, FactocraftyArmorMaterials.RUBBER, null, ImmutableList.of(COMMON_DROP)),
            SAWDUST(ImmutableList.of(COMMON_DROP)),
            SILICON( ImmutableList.of(COMMON_DROP));


            private final MaterialColor color;

            private final ArmorMaterial armor;

            private final net.minecraft.world.item.Tier tier;


            public final List<Derivative> derivatives;

            Tier(MaterialColor color, @Nullable ArmorMaterial armor, @Nullable net.minecraft.world.item.Tier tier, List<Derivative> derivatives) {
                this.color = color;
                this.armor = armor;
                this.tier = tier;
                this.derivatives = derivatives;

            }
        Tier(List<Derivative> derivatives) {
            this(null, null, null, derivatives);
        }
            public Optional<Derivative> getDerivative(Derivative derivative){
                return getDerivative(derivative.identifier);
            }
            public Optional<Derivative> getDerivative(String identifier){
                for (Derivative d : derivatives){
                    if (d.identifier.equals(identifier)) return Optional.of(d);
                }
                return Optional.empty();
            }
            public boolean hasDerivative(Derivative derivatives){return getDerivative(derivatives).isPresent();}

            public MaterialColor getColor() {
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

        public String getOreName(boolean isDeep){
            return (isDeep ? "deepslate_" : "") + this.getName() + "_ore";
        }
        public Block getOre(boolean isDeep){
            return Registration.getRegistrarBlockEntry(getOreName(isDeep));
        }

        }

}
