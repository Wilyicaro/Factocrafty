package wily.factocrafty.block.storage.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class FactocraftyEnergyStorageBlock extends FactocraftyMachineBlock {
    public FactocraftyEnergyStorageBlock(FactoryCapacityTiers tier, Properties properties) {
        super(tier, properties);    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FactocraftyEnergyStorageBlockEntity(capacityTier,blockPos,blockState);
    }


    @Override
    public Item asItem() {
        return super.asItem();
    }

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }

}
