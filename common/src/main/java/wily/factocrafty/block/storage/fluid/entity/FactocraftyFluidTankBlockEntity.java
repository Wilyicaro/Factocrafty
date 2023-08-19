package wily.factocrafty.block.storage.fluid.entity;

import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.block.FactocraftyLedBlock;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.util.registering.FactocraftyBlockEntities;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FactocraftyFluidTankBlockEntity extends FactocraftyMenuBlockEntity {
    public FactocraftyFluidTankBlockEntity(FactoryCapacityTiers capacityTier, BlockPos blockPos, BlockState blockState) {
        super(Registration.FLUID_TANK_MENU.get(), FactocraftyBlockEntities.ofBlock(blockState.getBlock()), blockPos, blockState);
        replaceSidedStorage(BlockSide.FRONT,fluidSides,  new FluidSide(fluidTank,TransportState.EXTRACT));
        replaceSidedStorage(BlockSide.BACK,fluidSides, new FluidSide(fluidTank, TransportState.INSERT));
        fluidTank =  FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity() * capacityTier.capacityMultiplier * 8, this, (f)-> !FactocraftyExpectPlatform.isGas(f.getFluid()), SlotsIdentifier.GENERIC,TransportState.EXTRACT_INSERT);
        STORAGE_SLOTS = new int[]{0,1};
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
                level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_CLIENTS);
                level.getProfiler().push("queueCheckLight");
                level.getChunkSource().getLightEngine().checkBlock(worldPosition);
                level.getProfiler().pop();
            }

    }

    @Override
    public Optional<SideList<FluidSide>> fluidSides() {
        return super.fluidSides();
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        list.add(fluidTank);
    }

    @Override
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots= super.getSlots(player);
        slots.add(new FactocraftyFluidItemSlot(this,1, 51,17,SlotsIdentifier.OUTPUT, TransportState.EXTRACT));
        slots.add(new FactocraftyFluidItemSlot(this,0, 51,53,SlotsIdentifier.INPUT, TransportState.INSERT));
        return slots;
    }
}
