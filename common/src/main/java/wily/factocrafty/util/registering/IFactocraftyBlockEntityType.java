package wily.factocrafty.util.registering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import wily.factocrafty.block.entity.ITicker;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.Bearer;

public interface IFactocraftyBlockEntityType extends IFactocraftyLazyRegistry<BlockEntityType<?>> {

    static<T extends BlockEntity> BlockEntityType<T> ofBlock(Block block){
        Bearer<BlockEntityType<T>> type= Bearer.of(Registration.getRegistrarBlockEntityEntry(block.arch$registryName().getPath()));
        if (type.isEmpty()) Registration.BLOCK_ENTITIES.forEach(b-> {
            if (b.get().isValid(block.defaultBlockState())) type.set((BlockEntityType<T>) b.get());
        });
        return type.get();
    }

    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(){
        return ((level, blockPos, blockState, blockEntity) -> {if (blockEntity instanceof ITicker t) t.tick(level.isClientSide);});
    }
    @Override
    default String getName() {
        return Registration.BLOCK_ENTITIES.getRegistrar().getId(get()).getPath();
    }
}
