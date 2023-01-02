package wily.factocrafty.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.platform.Platform;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wily.factocrafty.block.cable.CableTiers;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;

import java.util.List;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererInjector {


    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Shadow protected abstract void renderModelLists(BakedModel bakedModel, ItemStack itemStack, int i, int j, PoseStack poseStack, VertexConsumer vertexConsumer);



    @ModifyVariable(method = ("render"), at = @At("HEAD"), ordinal = 0,argsOnly = true)
    private BakedModel injectRender(BakedModel bakedModel, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j){
        if (!itemStack.isEmpty()) {
            boolean bl2 = transformType == ItemTransforms.TransformType.GUI || transformType == ItemTransforms.TransformType.GROUND || transformType == ItemTransforms.TransformType.FIXED;
            if (bl2) for (CableTiers tier : CableTiers.values()) for (Block block : List.of(tier.getBlock(),tier.getInsulatedBlock())){
                if (block instanceof InsulatedCableBlock cable && itemStack.is(cable.asItem())) {
                    return itemModelShaper.getModelManager().getModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + block.arch$registryName().getPath()) ,"inventory"));
                }
            }
            if (Platform.isFabric()){
                for (FactocraftyFluidTanks tank : FactocraftyFluidTanks.values())
                    if (itemStack.is(tank.get().asItem())) {
                        poseStack.pushPose();
                        bakedModel.getTransforms().getTransform(transformType).apply(bl, poseStack);
                        poseStack.translate(-0.5, -0.5, -0.5);
                        FactocraftyBlockEntityWLRenderer.INSTANCE.renderByItem(itemStack,transformType,poseStack,multiBufferSource,i,j);
                        poseStack.popPose();
                        return bakedModel;
                    }
            }
        }
        return bakedModel;
    }
    @ModifyVariable(method = ("getModel"), at = @At("STORE"), ordinal = 1)
    private BakedModel injectBakedModel(BakedModel bakedModel,ItemStack itemStack){
        for (Block a : Registration.BLOCKS.getRegistrar())
            if (a instanceof InsulatedCableBlock cable && itemStack.is(cable.asItem())) {
                return  itemModelShaper.getModelManager().getModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + a.arch$registryName().getPath()), "inventory"));
            }
        return  bakedModel;
    }
}
