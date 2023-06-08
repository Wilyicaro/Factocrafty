package wily.factocrafty.recipes;

import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.item.FactocraftyOre;
import wily.factoryapi.base.Progress;

import static wily.factocrafty.util.JsonUtils.jsonElement;

public class EnricherRecipe extends FactocraftyMachineRecipe{
    protected FactocraftyOre.Material matter = FactocraftyOre.Material.EMPTY;
    protected int matterCount = 10;
    public EnricherRecipe(String name, ResourceLocation resourceLocation) {
        super(name, resourceLocation);
    }

    public boolean matchesMatter(FactocraftyOre.Material matter, Progress progress) {
        return !matter.isEmpty() && progress.getInt(0) >= getMatter().second && matter == getMatter().first;
    }

    @Override
    public boolean hasFluidIngredient() {
        return true;
    }
    public boolean hasFluidResult() {
        return true;
    }


    public Pair<FactocraftyOre.Material, Integer> getMatter(){
        return Pair.of(matter,matterCount);
    }
    public static class Serializer extends FactocraftyMachineRecipe.Serializer<EnricherRecipe> {

        public Serializer(FactocraftySerializer<EnricherRecipe> cookieBaker, int defaultProcess) {
            super(cookieBaker, defaultProcess);
        }

        @Override
        public EnricherRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            EnricherRecipe r = super.fromJson(resourceLocation, jsonObject);
            r.matter = FactocraftyOre.Material.byName(GsonHelper.getAsString(jsonObject.getAsJsonObject("matter"),"material"));
            r.matterCount = GsonHelper.getAsInt(jsonObject.getAsJsonObject("matter"),"count",10);
            return r;
        }
        @Override
        public EnricherRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            EnricherRecipe r = super.fromNetwork(resourceLocation, buf);
            r.matter = FactocraftyOre.Material.values()[buf.readVarInt()];
            r.matterCount = buf.readVarInt();
            return r;
        }
        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, EnricherRecipe recipe) {
            super.toNetwork(friendlyByteBuf, recipe);
            friendlyByteBuf.writeVarInt(recipe.matter.ordinal());
            friendlyByteBuf.writeVarInt(recipe.matterCount);
        }
    }
}
