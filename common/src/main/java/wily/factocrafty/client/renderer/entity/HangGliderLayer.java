package wily.factocrafty.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.HangGliderItem;

import static wily.factocrafty.client.renderer.entity.HangGliderModel.getTexture;

public class HangGliderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {


    HangGliderModel<T> hangGliderModel;
    public HangGliderLayer(RenderLayerParent<T, M> renderLayerParent, EntityModelSet entityModelSet) {
        super(renderLayerParent);

        hangGliderModel = new HangGliderModel<>(entityModelSet.bakeLayer(HangGliderModel.LAYER_LOCATION));
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.getItem() instanceof HangGliderItem hang) {
            this.getParentModel().copyPropertiesTo(this.hangGliderModel);
            poseStack.pushPose();

            if (livingEntity.isFallFlying()) {
                poseStack.translate(0.D,0.12D,0.0D);
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
            }
            float v = 1.0F;
            if (livingEntity.getFallFlyingTicks() > 4) {
                v = (float)livingEntity.getDeltaMovement().lengthSqr();
                v /= 0.2F;
                v *= v * v;
            }

            if (v < 1.0F) {
                v = 1.0F;
            }

            this.hangGliderModel.left_wing.zRot = (Mth.cos(f * 0.3662F ) *  g) / (v * 24) + 0.1245F;
            this.hangGliderModel.right_wing.zRot = (Mth.cos(f * 0.3662F + 3.1415927F) *  g) / (v * 24) - 0.1245F;

            int color = hang.getColor(itemStack);
            float R = ((color & 0xFF0000) >> 16) / 255F;
            float G = ((color & 0xFF00) >> 8) / 255F;
            float B = (color & 0xFF) / 255F;
            hangGliderModel.setupAnim(livingEntity,f,g,j,v,l);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(getTexture()), false, itemStack.hasFoil());
            hangGliderModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, R,G,B,1.0F);
            poseStack.popPose();
        }
    }
}
