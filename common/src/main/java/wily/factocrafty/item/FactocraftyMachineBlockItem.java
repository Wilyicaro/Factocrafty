package wily.factocrafty.item;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.IFactoryItemClientExtension;

import java.util.function.Consumer;

public class FactocraftyMachineBlockItem extends BlockItem implements IFactoryItem {
    public FactocraftyMachineBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer) {
        clientExtensionConsumer.accept(new IFactoryItemClientExtension() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
                return FactocraftyBlockEntityWLRenderer.INSTANCE;
            }
        });
    }
}
