package wily.factocrafty.client.renderer.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.init.Registration;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class HangGliderModel<T extends LivingEntity> extends AgeableListModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Factocrafty.MOD_ID, "hang_glider"), "main");

    public final ModelPart support;
    public final ModelPart left_wing;
    public final ModelPart right_wing;
    public final ModelPart hang;



    public HangGliderModel(ModelPart modelPart) {
        support = modelPart.getChild("support");
        left_wing = modelPart.getChild("left_wing");
        right_wing = modelPart.getChild("right_wing");
        hang = modelPart.getChild("hang");

    }



    public static ResourceLocation getTexture(){
        return new ResourceLocation(MOD_ID,"textures/models/hang_glider/basic_hang_glider.png");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        this.bodyParts().forEach((modelPart) -> {
            if (modelPart != left_wing && modelPart != right_wing)  modelPart.render(poseStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
            else modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k);
        });
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition support = partdefinition.addOrReplaceChild("support", CubeListBuilder.create().texOffs(0, 82).addBox(-1.0F, -14.0F, -23.0F, 2.0F, 2.0F, 51.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
        support.addOrReplaceChild("horizontal", CubeListBuilder.create().texOffs(55, 98).addBox(-15.0F, 1.0F, -16.5F, 2.0F, 2.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.0F, 2.5F, 0.0F, -1.5708F, 0.0F));

        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 41).addBox(0.0F, -1.0F, -25.0F, 54.0F, 1.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 3.0F, 0.0F, 0.0F, 0.1245F));
        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 0).addBox(-54.0F, -1.0F, -25.0F, 54.0F, 1.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 3.0F, 0.0F, 0.0F, -0.1245F));

        PartDefinition hang =  partdefinition.addOrReplaceChild( "hang",CubeListBuilder.create().texOffs(55, 82).addBox(-13.0F, 8.0F, -8.0F, 26.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.ZERO);
        hang.addOrReplaceChild("right_hang", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.125F, -12.3F, -7.0F, 0.0F, 0.0F, 0.5236F));
        hang.addOrReplaceChild("left_hang", CubeListBuilder.create().texOffs(8, 0).addBox(-0.875F, -0.3F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -7.0F, 0.0F, 0.0F, -0.5236F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(support,left_wing,right_wing,hang);
    }

    @Override
    public void setupAnim(T entity, float f, float g, float h, float i, float j) {


    }
}
