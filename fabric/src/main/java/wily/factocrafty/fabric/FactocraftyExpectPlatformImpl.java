package wily.factocrafty.fabric;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.tag.Fluids;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FactocraftyExpectPlatformImpl {

    /**
     * This is our actual method to {@link FactocraftyExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {

        return FabricLoader.getInstance().getConfigDir();
    }


    public static boolean platformCorrectDiggerToolForDrops(Tier tier, TagKey<Block> tag, BlockState blockState) {
        int i = tier.getLevel();
        if (i < 3 && blockState.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        } else if (i < 2 && blockState.is(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        } else {
            return (i >= 1 || !blockState.is(BlockTags.NEEDS_STONE_TOOL)) && blockState.is(tag);
        }
    }
    public static void registerModel(ResourceLocation modelResourceLocation){
         ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(modelResourceLocation));
    }
    public static void setQuadsEmission(List<BakedQuad> quads) {
        quads.forEach((q)->{
            int a = quads.indexOf(q);
            int[] vertices = q.getVertices();
            for (int i = 0; i < 4; i++)
                vertices[i * DefaultVertexFormat.BLOCK.getIntegerSize() + findOffset(DefaultVertexFormat.ELEMENT_UV2)] = 240;
            if (vertices != q.getVertices()) quads.add(a,q);
        });

    }
    private static int findOffset(VertexFormatElement element)
    {
        // Divide by 4 because we want the int offset
        var index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : DefaultVertexFormat.BLOCK.offsets.getInt(index) / 4;
    }

    public static boolean isGas(Fluid fluid) {
        return fluid.is(Fluids.GAS) || FluidVariantAttributes.isLighterThanAir(FluidVariant.of(fluid));
    }
}
