package wily.factocrafty.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class FactocraftyFlowingFluid extends ArchitecturyFlowingFluid.Flowing {
    private final  whenSpreadToFluid whenContactFluid;
    private final boolean isGaseous;
    public FactocraftyFlowingFluid(ArchitecturyFluidAttributes attributes, @Nullable whenSpreadToFluid whenContactFluid, boolean isGaseous) {
        super(attributes);
        this.whenContactFluid = whenContactFluid;
        this.isGaseous = isGaseous;
    }

    public interface whenSpreadToFluid{
        boolean result(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState);
    }

    @Override
    public Vec3 getFlow(BlockGetter blockGetter, BlockPos blockPos, FluidState fluidState) {
        if (!isGaseous)return super.getFlow(blockGetter, blockPos, fluidState);
        double d = 0.0;
        double e = 0.0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            mutableBlockPos.setWithOffset(blockPos, direction);
            FluidState fluidState2 = blockGetter.getFluidState(mutableBlockPos);
            if (!this.affectsFlow(fluidState2)) continue;
            float f = fluidState2.getOwnHeight();
            float g = 0.0f;
            if (f == 0.0f) {
                FluidState fluidState3;
                if (!blockGetter.getBlockState(mutableBlockPos).blocksMotion() && this.affectsFlow(fluidState3 = blockGetter.getFluidState((BlockPos) mutableBlockPos.below())) && (f = fluidState3.getOwnHeight()) > 0.0f) {
                    g = fluidState.getOwnHeight() - (f - 0.8888889f);
                }
            } else if (f > 0.0f) {
                g = fluidState.getOwnHeight() - f;
            }
            if (g == 0.0f) continue;
            d += (float)direction.getStepX() * g;
            e += (float)direction.getStepZ() * g;
        }
        Vec3 vec3 = new Vec3(d, 0.0, e);
        if (fluidState.getValue(FALLING).booleanValue()) {
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                mutableBlockPos.setWithOffset(blockPos, direction2);
                if (!this.isSolidFace(blockGetter, mutableBlockPos, direction2) && !this.isSolidFace(blockGetter, mutableBlockPos.above(), direction2)) continue;
                vec3 = vec3.normalize().add(0.0, +6.0, 0.0);
                break;
            }
        }
        return vec3.normalize();
    }
    private boolean affectsFlow(FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.getType().isSame(this);
    }
    public boolean isValidToGetFog(FluidState fluidState){
        return !fluidState.is(FluidTags.WATER) && !fluidState.is(FluidTags.LAVA) && !isGaseous;
    }
    @Override
    protected void spreadTo(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (whenContactFluid != null && whenContactFluid.result(levelAccessor, blockPos, blockState, direction, fluidState)) {
            return;
        }
        super.spreadTo(levelAccessor, blockPos, blockState, direction, fluidState);
    }

    @Override
    protected void spread(Level level, BlockPos blockPos, FluidState fluidState) {
        if (!isGaseous){
            super.spread(level, blockPos, fluidState);
            return;
        }
        if (fluidState.isEmpty()) {
            return;
        }
        BlockState blockState = level.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.above();
        BlockState blockState2 = level.getBlockState(blockPos2);
        FluidState fluidState2 = this.getNewLiquid(level, blockPos2, blockState2);
        if (this.canSpreadTo(level, blockPos, blockState, Direction.UP, blockPos2, blockState2, level.getFluidState(blockPos2), fluidState2.getType())) {
            this.spreadTo(level, blockPos2, blockState2, Direction.UP, fluidState2);
            if (this.sourceNeighborCount(level, blockPos) >= 3) {
                this.spreadToSides(level, blockPos, fluidState, blockState);
            }
        } else if (fluidState.isSource() || !this.isGasHole(level, fluidState2.getType(), blockPos, blockState, blockPos2, blockState2)) {
            this.spreadToSides(level, blockPos, fluidState, blockState);
        }
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return isGaseous ? direction == Direction.UP && !this.isSame(fluid) : super.canBeReplacedWith(state, level, pos, fluid, direction);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, FluidState fluidState) {
        if (isGaseous && fluidState.isSource()) {
            BlockPos above = blockPos.above();
            BlockState aboveState = level.getBlockState(above);
            BlockState state = level.getBlockState(blockPos);

            if (this.canSpreadTo(level, blockPos, state, Direction.UP, above, aboveState, level.getFluidState(above), fluidState.getType())) {
                level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                if (level.random.nextFloat() >= 0.07) {
                    level.setBlock(above, fluidState.createLegacyBlock(), 3);
                    blockPos = above;
                }
            }
        }
        super.tick(level, blockPos, fluidState);

    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return super.createLegacyBlock(state);
    }

    public boolean isGasHole(BlockGetter blockGetter, Fluid fluid, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
        if (!this.canPassThroughWall(Direction.UP, blockGetter, blockPos, blockState, blockPos2, blockState2)) {
            return false;
        }
        return blockState2.getFluidState().getType().isSame(this);
    }
    protected FluidState getNewLiquid(Level level, BlockPos blockPos, BlockState blockState) {
        if (!isGaseous) return super.getNewLiquid(level,blockPos,blockState);
        BlockPos blockPos3;
        BlockState blockState4;
        FluidState fluidState3;
        int i = 0;
        int j = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockPos2 = blockPos.relative(direction);
            BlockState blockState2 = level.getBlockState(blockPos2);
            FluidState fluidState = blockState2.getFluidState();
            if (!fluidState.getType().isSame(this) || !this.canPassThroughWall(direction, level, blockPos, blockState, blockPos2, blockState2)) continue;
            if (fluidState.isSource()) {
                ++j;
            }
            i = Math.max(i, fluidState.getAmount());
        }
        if (this.canConvertToSource(level) && j >= 2) {
            BlockState blockState3 = level.getBlockState(blockPos.above());
            FluidState fluidState2 = blockState3.getFluidState();
            if (blockState3.isSolid() || this.isSourceBlockOfThisType(fluidState2)) {
                return this.getSource(false);
            }
        }
        if (!(fluidState3 = (blockState4 = level.getBlockState(blockPos3 = blockPos.below())).getFluidState()).isEmpty() && fluidState3.getType().isSame(this) && this.canPassThroughWall(Direction.DOWN, level, blockPos, blockState, blockPos3, blockState4)) {
            return this.getFlowing(8, true);
        }
        int k = i - this.getDropOff(level);
        if (k <= 0) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return this.getFlowing(k, false);
    }
    private boolean isSourceBlockOfThisType(FluidState fluidState) {
        return fluidState.getType().isSame(this) && fluidState.isSource();
    }
}
