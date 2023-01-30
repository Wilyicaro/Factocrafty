// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class hang_glider<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "hang_glider"), "main");
	private final ModelPart left_wing;
	private final ModelPart right_wing;
	private final ModelPart right_hang;
	private final ModelPart left_hang;
	private final ModelPart bb_main;

	public hang_glider(ModelPart root) {
		this.left_wing = root.getChild("left_wing");
		this.right_wing = root.getChild("right_wing");
		this.right_hang = root.getChild("right_hang");
		this.left_hang = root.getChild("left_hang");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 41).addBox(0.0F, -1.0F, -25.0F, 54.0F, 1.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 3.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 0).addBox(-54.0F, -1.0F, -25.0F, 54.0F, 1.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.0F, 3.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition right_hang = partdefinition.addOrReplaceChild("right_hang", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.125F, -12.3F, -7.0F, 0.0F, 0.0F, 0.5236F));

		PartDefinition left_hang = partdefinition.addOrReplaceChild("left_hang", CubeListBuilder.create().texOffs(8, 0).addBox(-0.875F, -0.3F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, -7.0F, 0.0F, 0.0F, -0.5236F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 82).addBox(-1.0F, -38.0F, -23.0F, 2.0F, 2.0F, 51.0F, new CubeDeformation(0.0F))
		.texOffs(55, 82).addBox(-13.0F, -16.0F, -8.0F, 26.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(55, 98).addBox(-15.0F, 1.0F, -16.5F, 2.0F, 2.0F, 33.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -37.0F, 2.5F, 0.0F, -1.5708F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_hang.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		left_hang.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}