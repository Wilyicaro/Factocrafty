package wily.factocrafty.block.storage.fluid.entity;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.block.FactocraftyLedBlock;
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
        fluidTank =  FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity() * capacityTier.capacityMultiplier * 8, this, (f)-> !FactocraftyExpectPlatform.isGas(f.getFluid()), SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT);
        FILL_SLOT = 0;
        DRAIN_SLOT = 1;
    }
    public double unitHeight = 0;
    public long smoothFluidAmount = 0;
    private long oldFluidAmount = fluidTank.getFluidStack().getAmount();


    @Override
    public boolean hasEnergyCell() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
            if (smoothFluidAmount != fluidTank.getFluidStack().getAmount())
                smoothFluidAmount = (long) Mth.clamp((long) (smoothFluidAmount + Math.pow(oldFluidAmount, -0.02) * fluidTank.getMaxFluid() / 16), 0, oldFluidAmount);
            if (oldFluidAmount != fluidTank.getFluidStack().getAmount()){
                oldFluidAmount = fluidTank.getFluidStack().getAmount();
                BlockState blockState1 = getBlockState();
                if (Platform.isFabric()) {
                    int i = ((FactocraftyFluidTankBlock) blockState1.getBlock()).getLightEmission(blockState1, level, worldPosition);
                    if (blockState1.getValue(FactocraftyLedBlock.LIGHT_VALUE) != i){
                        level.setBlock(getBlockPos(),blockState1.setValue(FactocraftyLedBlock.LIGHT_VALUE,i),3);
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
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots= super.getSlots(player);
        slots.add(new FactocraftyFluidItemSlot(this,DRAIN_SLOT, 51,17,SlotsIdentifier.OUTPUT, TransportState.INSERT));
        slots.add(new FactocraftyFluidItemSlot(this,FILL_SLOT, 51,53,SlotsIdentifier.INPUT, TransportState.EXTRACT));
        return slots;
    }
}
