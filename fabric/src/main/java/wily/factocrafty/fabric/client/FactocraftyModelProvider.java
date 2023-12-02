package wily.factocrafty.fabric.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;

public class FactocraftyModelProvider implements ModelResolver {

    public static final ResourceLocation FLUID_CELL_MODEL = new ResourceLocation(Factocrafty.MOD_ID, "fluid_cell");

    public static final ResourceLocation DYNAMIC_BUCKET_MODEL = new ResourceLocation(Factocrafty.MOD_ID, "dynamic_bucket");

    @Override
    public @Nullable UnbakedModel resolveModel(Context context) {
        if (context.id().equals(FLUID_CELL_MODEL)){
            return  new FluidCellModel();
        }else if (context.id().equals(DYNAMIC_BUCKET_MODEL))
            return new DynamicBucketModel();
        else return null;
    }
}
