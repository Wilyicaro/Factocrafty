package wily.factocrafty.block.generator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyStorageBlock;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.generator.entity.SolarPanelBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;

public class SolarPanelBlock extends FactocraftyStorageBlock implements IFactocraftyCYEnergyBlock {
    public SolarPanelTiers solarTier;
    public LoadingCache<Double, VoxelShape> cache;

    public SolarPanelBlock(SolarPanelTiers tier, Properties properties) {
        super(tier.energyTier,properties.noOcclusion().forceSolidOn());
        cache = CacheBuilder.newBuilder().maximumSize(FactoryCapacityTiers.values().length).build((new CacheLoader<>() {
            @Override
            public @NotNull VoxelShape load(@NotNull Double key) {
                return Block.box(0D, 0D, 0D, 16D, key, 16D);
            }
        }));
        solarTier = tier;
    }


    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return cache.getUnchecked(solarTier.heightSize);

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
