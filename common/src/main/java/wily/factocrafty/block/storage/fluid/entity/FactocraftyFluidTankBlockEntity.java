package wily.factocrafty.block.storage.fluid.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.List;

public class FactocraftyFluidTankBlockEntity extends FactocraftyProcessBlockEntity {
    public FactocraftyFluidTankBlockEntity(FactoryCapacityTiers capacityTier, BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.FLUID_TANK, capacityTier, FactocraftyBlockEntities.ofBlock(blockState.getBlock()), blockPos, blockState);
        replaceSidedStorage(BlockSide.TOP,fluidSides,  new FluidSide(fluidTank,TransportState.EXTRACT));
        replaceSidedStorage(BlockSide.BOTTOM,fluidSides, new FluidSide(fluidTank, TransportState.INSERT));
        fluidTank =  FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity() * capacityTier.capacityMultiplier * 8, this, (f)->true, SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT);
        CHARGE_SLOT = 0;
        UNCHARGE_SLOT = 1;
    }
    public long smoothFluidAmount = 0;
    private long oldFluidAmount = fluidTank.getFluidStack().getAmount();


    @Override
    public boolean hasEnergyCell() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        for  (Direction d : Direction.values()){
            if (level.getBlockEntity(getBlockPos().relative(d)) instanceof IFactoryStorage storage) {
            }
        }

            if (smoothFluidAmount != fluidTank.getFluidStack().getAmount())
                smoothFluidAmount = Mth.clamp((long) (smoothFluidAmount + FluidStack.bucketAmount() * Math.pow(oldFluidAmount, -0.02)), 0, oldFluidAmount);
            if (oldFluidAmount != fluidTank.getFluidStack().getAmount()){
                oldFluidAmount = fluidTank.getFluidStack().getAmount();
                BlockState blockState1 = getBlockState();
                if (Platform.isFabric()) {
                    int i = ((FactocraftyFluidTankBlock) blockState1.getBlock()).getLightEmission(blockState1, level, worldPosition);
                    if (blockState1.lightEmission != i) {
                            blockState1.lightEmission = i;
                            level.setBlockAndUpdate(getBlockPos(), blockState1);
                    }
                }
                level.sendBlockUpdated(getBlockPos(),getBlockState(),blockState1, Block.UPDATE_CLIENTS);
                level.getProfiler().push("queueCheckLight");
                level.getChunkSource().getLightEngine().checkBlock(worldPosition);
                level.getProfiler().pop();
            }



    }

    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {

        slots.add(new FactocraftyFluidItemSlot(this,CHARGE_SLOT, 51,17, TransportState.INSERT));
        slots.add(new FactocraftyFluidItemSlot(this,UNCHARGE_SLOT, 51,53, TransportState.EXTRACT));
    }
}
