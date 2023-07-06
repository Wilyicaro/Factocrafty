package wily.factocrafty.client.renderer.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import wily.factocrafty.Factocrafty;

import static wily.factocrafty.Factocrafty.MOD_ID;

public class JetpackModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Factocrafty.MOD_ID, "jetpack"), "main");
    public JetpackModel(ModelPart modelPart) {
        super(modelPart);
    }

    public JetpackModel(HumanoidModel<T> parentModel, ModelPart modelPart) {
        super(modelPart);
        parentModel.copyPropertiesTo(this);
    }

    @Override
    public void setupAnim(T livingEntity, float f, float g, float h, float i, float j) {

        super.setupAnim(livingEntity, f, g, h, i, j);
    }
    public static ResourceLocation geteElectricTexture(){
        return new ResourceLocation(MOD_ID,"textures/models/armor/electric_jetpack.png");
    }
    public static ResourceLocation geteFlexTexture(){
        return new ResourceLocation(MOD_ID,"textures/models/armor/flex_jetpack.png");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head",CubeListBuilder.create(),PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat",CubeListBuilder.create(),PartPose.ZERO);
        PartDefinition bb_main = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F,new CubeDeformation(0.5F)).texOffs(14, 16).addBox(1.0F, 2, 2.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 4).addBox(1.5F, 10.8F, 2.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-5.0F, 2, 2.0F, 4.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 0).addBox(-4.5F, 10.8F, 2.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F , 0.0F));

        partdefinition.addOrReplaceChild("right_arm",CubeListBuilder.create(),PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm",CubeListBuilder.create(),PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg",CubeListBuilder.create(),PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg",CubeListBuilder.create(),PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}
