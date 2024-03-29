package wily.factocrafty.client.renderer;

import com.google.common.base.Charsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.renderer.block.FluidPipeRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Environment(EnvType.CLIENT)
public class ModelHelper {

    public static final  ItemTransforms DEFAULT_ITEM_TRANSFORMS = loadTransformFromJson(new ResourceLocation("minecraft:models/item/generated"));
    public static final  ItemTransforms HANDHELD_ITEM_TRANSFORMS = loadTransformFromJson(new ResourceLocation("minecraft:models/item/handheld"));


    public static final BlockModel BLOCK_MODEL = loadBlockModelFromJson(new ResourceLocation("minecraft:models/block/cube_all"));

    public static final BlockModel LEAVES_MODEL = loadBlockModelFromJson(new ResourceLocation("minecraft:models/block/leaves"));

    public static final BlockModel FLUID_MODEL = ModelHelper.loadBlockModelFromJson(new ResourceLocation( "factocrafty:models/block/fluid_tank/fluid_model"));

    public static final BlockModel FLUID_SIDE_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidSideLocation(false,false));
    public static final BlockModel FLUID_SIDE_NONE_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidSideLocation(false,true));
    public static final BlockModel FLUID_CENTER_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidCenterLocation(false));

    public static final BlockModel LARGE_FLUID_SIDE_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidSideLocation(true,false));
    public static final BlockModel LARGE_FLUID_SIDE_NONE_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidSideLocation(true,true));
    public static final BlockModel LARGE_FLUID_CENTER_MODEL = ModelHelper.loadBlockModelFromJson(FluidPipeRenderer.fluidCenterLocation(true));
    public static final BlockModel TREETAP_LATEX_MODEL = ModelHelper.loadBlockModelFromJson(new ResourceLocation("factocrafty:models/block/treetap/treetap_latex"));
    public static final BlockModel TREETAP_LATEX_FALL_MODEL = ModelHelper.loadBlockModelFromJson(new ResourceLocation("factocrafty:models/block/treetap/treetap_latex_fall"));

    public static BlockModel loadBlockModelFromJson(ResourceLocation location) {
        if (location.getPath().startsWith("block/")) location=  location.withPath("models/"+ location.getPath());
        try {

            return BlockModel.fromStream(getReaderForResource(location));
        } catch (IOException exception) {
            Factocrafty.LOGGER.warning("Can't load resource " + location);
            exception.printStackTrace();
            return null;
        }
    }
    public static ItemTransforms loadTransformFromJson(ResourceLocation location) {
        return loadBlockModelFromJson(location).getTransforms();
    }

    public static Reader getReaderForResource(ResourceLocation location) throws IOException {
        ResourceLocation file = new ResourceLocation(location.getNamespace(), location.getPath() + ".json");
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(file).orElseThrow();
        return new BufferedReader(new InputStreamReader(resource.open(), Charsets.UTF_8));
    }

}