package wily.factocrafty.forge;

import dev.architectury.fluid.FluidStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.common.TierSortingRegistry;
import wily.factocrafty.FactocraftyExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

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
        return FMLPaths.CONFIGDIR.get();
    }

    public static CreativeModeTab.Builder CreativeTabBuilder(ResourceLocation resource) {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0).title(Component.translatable("itemGroup.%s.%s".formatted(resource.getNamespace(), resource.getPath())));
        };


    public static boolean platformCorrectDiggerToolForDrops(Tier tier, TagKey<Block> tag, BlockState blockState) {
        return blockState.is(tag) && TierSortingRegistry.isCorrectTierForDrops(tier, blockState);
    }
    public static void registerModel(ResourceLocation modelResourceLocation) {
        FactocraftyForgeClient.REGISTER_MODELS.add(modelResourceLocation);
    }


    public static void setQuadsEmission(List<BakedQuad> quads) {

        QuadTransformers.settingMaxEmissivity().processInPlace(quads);
    }
}
