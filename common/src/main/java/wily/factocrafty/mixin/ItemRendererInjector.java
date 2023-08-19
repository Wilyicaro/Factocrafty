package wily.factocrafty.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.platform.Platform;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.block.transport.FactocraftyConduitBlock;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factocrafty.item.FactocraftyMachineBlockItem;
import wily.factocrafty.util.registering.FactocraftyFluidTanks;
import wily.factocrafty.util.registering.IFactocraftyConduit;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererInjector {


    @Shadow @Final private ItemModelShaper itemModelShaper;



    @ModifyVariable(method = ("render"), at = @At("HEAD"), ordinal = 0,argsOnly = true)
    private BakedModel injectRender(BakedModel bakedModel, ItemStack itemStack, ItemDisplayContext transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j){
        if (!itemStack.isEmpty()) {
            boolean bl2 = transformType == ItemDisplayContext.GUI || transformType == ItemDisplayContext.GROUND || transformType == ItemDisplayContext.FIXED;
            if (bl2 && itemStack.getItem() instanceof BlockItem b && b.getBlock() instanceof FactocraftyConduitBlock<?,?>)
                return  itemModelShaper.getModelManager().getModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + b.arch$registryName().getPath()), "inventory"));
        }
        return bakedModel;
    }
    @ModifyVariable(method = ("getModel"), at = @At("STORE"), ordinal = 0)
    private BakedModel injectBakedModel(BakedModel bakedModel,ItemStack itemStack){
            if (itemStack.getItem() instanceof BlockItem b && b.getBlock() instanceof FactocraftyConduitBlock<?,?>)
                return  itemModelShaper.getModelManager().getModel(new ModelResourceLocation( new ResourceLocation("factocrafty:" + b.arch$registryName().getPath() + "_in_hand"), "inventory"));
        return  bakedModel;
    }
}
