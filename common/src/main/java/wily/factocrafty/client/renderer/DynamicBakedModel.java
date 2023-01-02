package wily.factocrafty.client.renderer;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;

import java.util.ArrayList;
import java.util.List;

public class DynamicBakedModel implements BakedModel {

    private final TextureAtlasSprite particleSprite;
    private final ItemTransforms itemTransforms;
    private final ItemOverrides itemOverrides;
    public boolean gui3d;
    public boolean ambientOcclusion;
    public boolean usesBlockLight;
    public boolean customRenderer;
    private BakedModel bakedModel;
    private BlockModel blockModel;
    private TextureAtlasSprite allSprite;


    public DynamicBakedModel(TextureAtlasSprite particleSprite, ItemTransforms itemTransforms, ItemOverrides itemOverrides,boolean gui3d,boolean ambientOcclusion,boolean usesBlockLight,boolean customRenderer){
        this.particleSprite = particleSprite;
        this.itemTransforms = itemTransforms;
        this.itemOverrides = itemOverrides;
        this.gui3d = gui3d;
        this.ambientOcclusion = ambientOcclusion;
        this.usesBlockLight = usesBlockLight;
        this.customRenderer = customRenderer;
    }
    public DynamicBakedModel(BakedModel bakedModel, TextureAtlasSprite sprite, BlockModel blockModel){
        this(bakedModel.getParticleIcon(),bakedModel.getTransforms(),bakedModel.getOverrides(),bakedModel.isGui3d(),bakedModel.useAmbientOcclusion(),bakedModel.usesBlockLight(),bakedModel.isCustomRenderer());
        this.bakedModel = bakedModel;
        this.allSprite = sprite;
        this.blockModel = blockModel;
    }
    public static BakedQuad replaceSprite( BakedQuad bakedQuad, TextureAtlasSprite sprite, BlockElement element){
        int[] is = bakedQuad.getVertices();
        for(int i = 0; i < 4; ++i) {
            int j = i *8;
            is[j + 4] = Float.floatToRawIntBits(sprite.getU(element.faces.get(bakedQuad.getDirection()).uv.getU(i))) ;
            is[j + 5] = Float.floatToRawIntBits(sprite.getV(element.faces.get(bakedQuad.getDirection()).uv.getV(i))) ;
        }

        return new BakedQuad(is, bakedQuad.getTintIndex(), bakedQuad.getDirection(),sprite,bakedQuad.isShade());
    }


    public void setQuadsEmission(){
        FactocraftyExpectPlatform.setQuadsEmission(DYNAMIC_QUADS);
    }
    private final List<BakedQuad> DYNAMIC_QUADS = new ArrayList<>();

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        List<BakedQuad> quads = bakedModel.getQuads(blockState,direction,randomSource);


        for (int i = 0; i < quads.size(); i++) {
            BakedQuad b = quads.get(i);
            if (DYNAMIC_QUADS.contains(b)) continue;
            addQuad(allSprite == null ? b : replaceSprite( b,allSprite,blockModel.getElements().get(i /6)));
        }
        return DYNAMIC_QUADS;
    }

    public void addQuad(BakedQuad a){
        DYNAMIC_QUADS.add(a);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ambientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return gui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return usesBlockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return customRenderer;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleSprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return itemTransforms;
    }

    @Override
    public ItemOverrides getOverrides() {
        return itemOverrides;
    }
}
