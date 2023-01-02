package wily.factocrafty;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.List;

public class FactocraftyExpectPlatform {
    /**
     * We can use {@link Platform#getConfigFolder()} but this is just an example of {@link ExpectPlatform}.
     * <p>
     * This must be a <b>public static</b> method. The platform-implemented solution must be placed under a
     * platform sub-package, with its class suffixed with {@code Impl}.
     * <p>
     * Example:
     * Expect: net.factocrafty.ExampleExpectPlatform#getConfigDirectory()
     * Actual Fabric: net.factocrafty.fabric.ExampleExpectPlatformImpl#getConfigDirectory()
     * Actual Forge: net.factocrafty.forge.ExampleExpectPlatformImpl#getConfigDirectory()
     * <p>
     * <a href="https://plugins.jetbrains.com/plugin/16210-architectury">You should also get the IntelliJ plugin to help with @ExpectPlatform.</a>
     */
    @ExpectPlatform
    public static Path getConfigDirectory() {

        throw new AssertionError();
    }

    @ExpectPlatform
    public static CreativeModeTab.Builder CreativeTabBuilder(ResourceLocation resource) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean platformCorrectDiggerToolForDrops(Tier tier, TagKey<Block> tag, BlockState blockState) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerModel(ResourceLocation modelResourceLocation){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void setQuadsEmission(List<BakedQuad> quads){
        throw new AssertionError();
    }
}
