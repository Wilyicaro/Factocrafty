package wily.factocrafty.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.machines.entity.FluidPumpBlockEntity;
import wily.factocrafty.block.storage.energy.entity.FactocraftyEnergyStorageBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.List;

public class FluidPumpBlock extends FactocraftyMachineBlock {
    public FluidPumpBlock(Properties properties) {
        super(FactoryCapacityTiers.BASIC, properties.noOcclusion());    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidPumpBlockEntity(blockPos,blockState);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(capacityTier.getEnergyTierComponent(false));
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
