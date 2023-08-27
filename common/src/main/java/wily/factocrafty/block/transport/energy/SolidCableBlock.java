package wily.factocrafty.block.transport.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.transport.ConduitSide;
import wily.factocrafty.block.transport.FactocraftySolidConduitBlock;
import wily.factocrafty.block.transport.energy.entity.CableBlockEntity;
import wily.factocrafty.block.transport.energy.entity.SolidCableBlockEntity;
import wily.factocrafty.util.registering.FactocraftyCables;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.SimpleFluidLoggedBlock;
import wily.factoryapi.util.StorageStringUtil;
import wily.factoryapi.util.VoxelShapeUtil;

import java.util.List;

public class SolidCableBlock extends FactocraftySolidConduitBlock<FactocraftyCables, CableBlockEntity>  implements IFactocraftyCYEnergyBlock{

    public SolidCableBlock(FactocraftyCables tier, Properties properties) {super(tier, properties);}
    private static final VoxelShape SHAPE_CUBE = Block.box(6, 6, 6, 10, 10, 10);
    @Override
    protected VoxelShape getCenterCubeShape() {
        return SHAPE_CUBE;
    }

    @Override
    public void unsupportedTierBurn(Level level, BlockPos pos, FactoryCapacityTiers higherTier) {
        IFactocraftyCYEnergyBlock.super.unsupportedTierBurn(level, pos, higherTier);
        level.removeBlock(pos, true);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(conduitType.getCapacityTier().getEnergyTierComponent(false));
        list.add(StorageStringUtil.getMaxCraftyTransferTooltip(conduitType.maxEnergyTransfer()).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return conduitType.getCapacityTier();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolidCableBlockEntity(blockPos, blockState);
    }
}
