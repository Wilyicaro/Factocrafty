package wily.factocrafty.block.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.generator.entity.SolarPanelBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class SolarPanelBlock extends FactocraftyStorageBlock implements IFactocraftyCYEnergyBlock {
    public SolarPanelTiers solarTier;

    public SolarPanelBlock(SolarPanelTiers tier, Properties properties) {
        super(tier.energyTier,properties.noOcclusion());
        solarTier = tier;
    }

    @Override
    public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return super.getLightBlock(blockState, blockGetter, blockPos);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Block.box(0D,0D,0D,16D, solarTier.heightSize, 16D);

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SolarPanelBlockEntity(solarTier,blockPos,blockState);
    }



    @Override
    public boolean isEnergyReceiver() {
        return false;
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return capacityTier;
    }

    @Override
    public boolean produceEnergy() {
        return true;
    }


}
