package wily.factocrafty.block.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.machines.entity.FactocraftyEnergyTransformerBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

import java.util.List;

public class FactocraftyEnergyTransformerBlock extends FactocraftyMachineBlock {
    public FactocraftyEnergyTransformerBlock(FactoryCapacityTiers tier, Properties properties) {
        super(tier, properties);    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FactocraftyEnergyTransformerBlockEntity(blockPos,blockState);
    }

    @Override
    public ItemStack getBurnRepairItem(RecipeManager recipeManager) {
        return super.getBurnRepairItem(recipeManager);
    }
    

    @Override
    public DirectionProperty getFacingProperty() {
        return BlockStateProperties.FACING;
    }

}
