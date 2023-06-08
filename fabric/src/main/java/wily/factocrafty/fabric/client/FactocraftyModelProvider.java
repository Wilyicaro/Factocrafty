package wily.factocrafty.fabric.client;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;

public class FactocraftyModelProvider implements ModelResourceProvider {

    public static final ResourceLocation FLUID_CELL_MODEL = new ResourceLocation(Factocrafty.MOD_ID, "fluid_cell");

    public static final ResourceLocation DYNAMIC_BUCKET_MODEL = new ResourceLocation(Factocrafty.MOD_ID, "dynamic_bucket");
    @Override
    public @Nullable UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.equals(FLUID_CELL_MODEL)){
            return  new FluidCellModel();
        }else if (resourceId.equals(DYNAMIC_BUCKET_MODEL))
            return new DynamicBucketModel();
        else return null;
    }
}
