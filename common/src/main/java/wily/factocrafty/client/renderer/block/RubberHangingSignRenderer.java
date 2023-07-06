package wily.factocrafty.client.renderer.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.block.entity.RubberHangingSignBlockEntity;
import wily.factocrafty.block.entity.RubberSignBlockEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Environment(value= EnvType.CLIENT)
public class RubberHangingSignRenderer extends RubberSignRenderer<RubberHangingSignBlockEntity> {
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, -0.3199999928474426, 0.0729999989271164);
    private final Map<WoodType, HangingSignRenderer.HangingSignModel> hangingSignModels;

    public RubberHangingSignRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.hangingSignModels = WoodType.values().collect(ImmutableMap.toImmutableMap((woodType) -> woodType, (woodType) -> new HangingSignRenderer.HangingSignModel(context.bakeLayer(ModelLayers.createHangingSignModelName(woodType)))));
    }

    public float getSignModelRenderScale() {
        return 1.0F;
    }

    public float getSignTextRenderScale() {
        return 0.9F;
    }

    public void render(SignBlockEntity signBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        BlockState blockState = signBlockEntity.getBlockState();
        SignBlock signBlock = (SignBlock)blockState.getBlock();
        WoodType woodType = SignBlock.getWoodType(signBlock);
        HangingSignRenderer.HangingSignModel hangingSignModel = this.hangingSignModels.get(woodType);
        hangingSignModel.evaluateVisibleParts(blockState);
        this.renderSignWithText(signBlockEntity, poseStack, multiBufferSource, i, j, blockState, signBlock, woodType, hangingSignModel);
    }

    void translateSign(PoseStack poseStack, float f, BlockState blockState) {
        poseStack.translate(0.5, 0.9375, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        poseStack.translate(0.0F, -0.3125F, 0.0F);
    }

    void renderSignModel(PoseStack poseStack, int i, int j, Model model, VertexConsumer vertexConsumer) {
        HangingSignRenderer.HangingSignModel hangingSignModel = (HangingSignRenderer.HangingSignModel) model;
        hangingSignModel.root.render(poseStack, vertexConsumer, i, j);
    }

    Material getSignMaterial(WoodType woodType) {
        return Sheets.getHangingSignMaterial(woodType);
    }

    Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

}
