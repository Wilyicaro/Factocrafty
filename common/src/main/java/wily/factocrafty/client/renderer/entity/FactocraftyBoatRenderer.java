package wily.factocrafty.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ElytraItem;
import org.joml.Quaternionf;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.entity.FactocraftyBoat;
import wily.factocrafty.entity.IFactocraftyBoat;

import java.util.Map;
import java.util.stream.Stream;

public class FactocraftyBoatRenderer extends EntityRenderer<Boat>{

    private final Map<IFactocraftyBoat.Type, Pair<ResourceLocation, ListModel<Boat>>> factocraftyBoatResources;
    public FactocraftyBoatRenderer(EntityRendererProvider.Context context, boolean bl) {
        super(context);
        this.shadowRadius = 0.8F;
        this.factocraftyBoatResources = Stream.of(IFactocraftyBoat.Type.values()).collect(ImmutableMap.toImmutableMap((type) -> {
            return type;
        }, (type) -> {
            return Pair.of(new ResourceLocation(Factocrafty.MOD_ID,getTextureLocation(type, bl)), this.createBoatModel(context, type, bl));
        }));
    }
    public static ModelLayerLocation getFactocraftyBoatLayer(IFactocraftyBoat.Type type, boolean bl){
        return new ModelLayerLocation(new ResourceLocation(Factocrafty.MOD_ID,bl ? "chest_boat/" : "boat/" + type.getName()),"main");
    }
    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, IFactocraftyBoat.Type type, boolean bl) {
        ModelPart modelPart = context.bakeLayer(getFactocraftyBoatLayer(type,bl));
        return (bl ? new ChestBoatModel(modelPart) : new BoatModel(modelPart));

    }
    private static String getTextureLocation(IFactocraftyBoat.Type type, boolean bl) {
        return bl ? "textures/entity/chest_boat/" + type.getName() + ".png" : "textures/entity/boat/" + type.getName() + ".png";
    }
    public void render(Boat boat, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.375, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f));
        float h = (float)boat.getHurtTime() - g;
        float j = boat.getDamage() - g;
        if (j < 0.0F) {
            j = 0.0F;
        }

        if (h > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(h) * h * j / 10.0F * (float)boat.getHurtDir()));
        }

        float k = boat.getBubbleAngle(g);
        if (!Mth.equal(k, 0.0F)) {
            poseStack.mulPose((new Quaternionf()).setAngleAxis(boat.getBubbleAngle(g) * 0.017453292F, 1.0F, 0.0F, 1.0F));
        }
        if (boat instanceof IFactocraftyBoat factocraftyBoat) {
            Pair<ResourceLocation, ListModel<Boat>> pair = this.factocraftyBoatResources.get(factocraftyBoat.getFactocraftyBoatType());
            ResourceLocation resourceLocation = pair.getFirst();
            BoatModel boatModel = (BoatModel) pair.getSecond();
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            boatModel.setupAnim(boat, g, 0.0F, -0.1F, 0.0F, 0.0F);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(boatModel.renderType(resourceLocation));
            boatModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            if (!boat.isUnderWater()) {
                VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RenderType.waterMask());
                boatModel.waterPatch().render(poseStack, vertexConsumer2, i, OverlayTexture.NO_OVERLAY);
            }
        }

        poseStack.popPose();
        super.render(boat, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat entity) {
        return (this.factocraftyBoatResources.get(entity instanceof IFactocraftyBoat b ? b.getFactocraftyBoatType(): IFactocraftyBoat.Type.values()[0])).getFirst();
    }
}
