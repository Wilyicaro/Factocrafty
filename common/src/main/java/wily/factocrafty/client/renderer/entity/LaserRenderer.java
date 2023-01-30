package wily.factocrafty.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.entity.LaserProjectile;

@Environment(EnvType.CLIENT)
public class LaserRenderer<T extends LaserProjectile> extends EntityRenderer<T> {
    private final LaserModel laserModel;
    public LaserRenderer(EntityRendererProvider.Context context) {
        super(context);
        laserModel = new LaserModel(context.getModelSet().bakeLayer(LaserModel.LAYER_LOCATION));
    }

    public void render(T laser, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, laser.yRotO, laser.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, laser.xRotO, laser.getXRot())));
        float t = -Mth.sin( 3.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(t));
        poseStack.translate(-0.3,-0.3,-0.3);

        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.5F, 0.5F, 0.5F);

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(laser)));
        laserModel.renderToBuffer(poseStack,vertexConsumer,i,OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(laser, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(Factocrafty.MOD_ID, "textures/entity/laser.png");
    }

    public void vertex(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, int i, int j, int k, float f, float g, int l, int m, int n, int o) {
        vertexConsumer.vertex(matrix4f, (float)i, (float)j, (float)k).color(255, 255, 255, 255).uv(f, g).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(o).normal(matrix3f, (float)l, (float)n, (float)m).endVertex();
    }
}

