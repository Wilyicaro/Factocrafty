package wily.factocrafty.forge.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factocrafty.item.FluidTankItem;

import java.util.function.Consumer;

@Mixin(FluidTankItem.class)
public class AddFluidTankBlockEntityItemRenderer extends Item {
    public AddFluidTankBlockEntityItemRenderer(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new FactocraftyBlockEntityWLRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            }
        });
        super.initializeClient(consumer);
    }
}
