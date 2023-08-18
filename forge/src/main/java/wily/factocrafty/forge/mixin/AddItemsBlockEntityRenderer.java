package wily.factocrafty.forge.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factocrafty.item.FactocraftyMachineBlockItem;
import wily.factocrafty.item.FluidTankItem;

import java.util.function.Consumer;

@Mixin(FactocraftyMachineBlockItem.class)
public class AddItemsBlockEntityRenderer extends Item {
    public AddItemsBlockEntityRenderer(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return FactocraftyBlockEntityWLRenderer.INSTANCE;
            }
        });
        super.initializeClient(consumer);
    }
}
