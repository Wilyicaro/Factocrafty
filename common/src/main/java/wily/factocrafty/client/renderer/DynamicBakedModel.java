package wily.factocrafty.client.renderer;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.FactocraftyExpectPlatform;

import java.util.*;

public class DynamicBakedModel implements BakedModel {

    private final TextureAtlasSprite particleSprite;
    private final ItemTransforms itemTransforms;
    private final ItemOverrides itemOverrides;
    public boolean gui3d;
    public boolean ambientOcclusion;
    public boolean usesBlockLight;
    private BakedModel bakedModel;
    private BlockModel blockModel;
    private TextureAtlasSprite allSprite;

    private float height = 1.0F;

    public DynamicBakedModel(TextureAtlasSprite particleSprite, ItemTransforms itemTransforms, ItemOverrides itemOverrides,boolean gui3d,boolean ambientOcclusion,boolean usesBlockLight){
        this.particleSprite = particleSprite;
        this.itemTransforms = itemTransforms;
        this.itemOverrides = itemOverrides;
        this.gui3d = gui3d;
        this.ambientOcclusion = ambientOcclusion;
        this.usesBlockLight = usesBlockLight;
    }
    public DynamicBakedModel(BakedModel bakedModel, TextureAtlasSprite sprite, BlockModel blockModel){
        this(bakedModel.getParticleIcon(),bakedModel.getTransforms(),bakedModel.getOverrides(),bakedModel.isGui3d(),bakedModel.useAmbientOcclusion(),bakedModel.usesBlockLight());
        this.bakedModel = bakedModel;
        this.allSprite = sprite;
        this.blockModel = blockModel;
    }

    public void setHeightScale(float heightScale) {
        this.height = heightScale;
    }

    public static BakedQuad changeQuadUV(BakedQuad bakedQuad, TextureAtlasSprite sprite, BlockElement element, float height){
        int[] is = bakedQuad.getVertices().clone();
        BlockFaceUV uv = element.faces.get(bakedQuad.getDirection()).uv;
        BlockFaceUV uv2 = new BlockFaceUV(uv.uvs.clone(),uv.rotation);
        SpriteContents contents = new SpriteContents(sprite.contents().name(), new FrameSize(bakedQuad.getSprite().contents().width(), Math.max(1,(int)(bakedQuad.getSprite().contents().height()* ((Direction.Plane.HORIZONTAL.test(bakedQuad.getDirection()) ? height : 1))))),sprite.contents().originalImage, ((SpriteAnimatedContents)sprite.contents()).getAnimatedMetadataSection()){
            @Nullable
            @Override
            public AnimatedTexture createAnimatedTexture(FrameSize frameSize, int i, int j, AnimationMetadataSection animationMetadataSection) {
                return sprite.contents().animatedTexture;
            }
        };
        TextureAtlasSprite newSprite = new TextureAtlasSprite(sprite.atlasLocation(),contents, (int) Math.pow(sprite.getU0() / sprite.getX(),-1), (int)(Math.pow(sprite.getV0()/ sprite.getY(),-1) ),  sprite.getX(), sprite.getY());
        for(int i = 0; i < 4; ++i) {
            int j = i *8;
            if (height!= 1.0F && i==3  && Direction.Plane.HORIZONTAL.test(bakedQuad.getDirection())) {
                uv2.uvs[i] *= height;
            }
            is[j + 4] = Float.floatToRawIntBits(newSprite.getU(uv2.getU(i))) ;
            is[j + 5] = Float.floatToRawIntBits(newSprite.getV(uv2.getV(i))) ;
        }
        return new BakedQuad(is, bakedQuad.getTintIndex(), bakedQuad.getDirection(),newSprite,bakedQuad.isShade());
    }
    public static BakedQuad setQuadHeight( float size,float[] allY, BakedQuad bakedQuad){
        int[] is = bakedQuad.getVertices().clone();
        for(int i = 0; i < 4; ++i) {
            int j = i *8;
            float y = Float.intBitsToFloat(is[j + 1]);
            if (y == allY[0]) continue;
            is[j + 1] = Float.floatToRawIntBits(allY[0] + (y - allY[0]) * size);
        }

        return new BakedQuad(is, bakedQuad.getTintIndex(), bakedQuad.getDirection(),bakedQuad.getSprite(),bakedQuad.isShade());
    }


    public void setQuadsEmission(){
        FactocraftyExpectPlatform.setQuadsEmission(DYNAMIC_QUADS);
    }
    private final List<BakedQuad> DYNAMIC_QUADS = new ArrayList<>();

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
        List<BakedQuad> quads = new ArrayList<>(bakedModel.getQuads(blockState,direction,randomSource));
        if (height != 1.0F) {
            float[] allY = new float[8];
            for (BakedQuad q : quads)
                for (int i = 0; i < 4; ++i)
                    allY = ArrayUtils.add(allY, Float.intBitsToFloat(q.getVertices()[i * 8 + 1]));
            Arrays.sort(allY);
            for (int i = 0; i < quads.size(); i++)  quads.set(i,setQuadHeight(height,allY,quads.get(i)));
        }

        for (int i = 0; i < quads.size(); i++) {
            BakedQuad b = quads.get(i);
            if (DYNAMIC_QUADS.contains(b)) continue;
            addQuad(allSprite == null ? b : changeQuadUV( b,allSprite,blockModel.getElements().get(i /6), height));
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
        return true;
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
