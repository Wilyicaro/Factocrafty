package wily.factocrafty.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;

public class RubberSignBlockEntity extends SignBlockEntity {
    public RubberSignBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return Registration.RUBBER_SIGN_BLOCK_ENTITY.get();
    }
}
