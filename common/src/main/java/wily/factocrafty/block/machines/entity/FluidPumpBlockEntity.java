package wily.factocrafty.block.machines.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.transport.BlockConnection;
import wily.factocrafty.block.transport.entity.ConduitBlockEntity;
import wily.factocrafty.block.transport.fluid.FluidPipeBlock;
import wily.factocrafty.block.transport.fluid.FluidPipeBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.item.BucketLikeItem;
import wily.factocrafty.item.UpgradeType;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class FluidPumpBlockEntity extends FactocraftyMenuBlockEntity implements IFactoryProgressiveStorage {


    public Progress progress = new Progress(Progress.Identifier.DEFAULT,80,40,80);

    public Bearer<Integer> pumpMode = Bearer.of(1);

    public TransportState getPumpMode(){ return TransportState.byOrdinal(pumpMode.get());}

    private final BlockConnection defaultDrain = new BlockConnection() {
        @Override
        public boolean test(LevelAccessor level, BlockPos pos, @Nullable Direction direction) {
            return level.getBlockState(pos).getBlock() instanceof BucketPickup && level.getBlockState(pos).getFluidState().isSource();
        }
        @Override
        public boolean connectionTick(BlockEntity be,BlockPos blockPos, Direction direction) {
            if(isRemoved()) return true;
            IFactoryExpandedStorage storage = (IFactoryExpandedStorage) be;
            if (!storage.fluidSides().isEmpty() && !storage.fluidSides().get().getTransport(direction).canInsert()) return false;
            BlockState state = be.getLevel().getBlockState(blockPos);
            Fluid fluid = state.getFluidState().getType();
            BucketPickup p = (BucketPickup) state.getBlock();
            for (IPlatformFluidHandler<?> tank : storage.getTanks()) {
                if (progress.first().get() >= progress.first().maxProgress && energyStorage.getEnergyStored() >= 3 && tank.isFluidValid(FluidStack.create(fluid, FluidStack.bucketAmount())) && (tank.getFluidStack().isEmpty() || fluid.isSame(tank.getFluidStack().getFluid())) && tank.getTotalSpace() >= FluidStack.bucketAmount()){
                    if (level.isClientSide) return true;
                    progress.first().set(0);
                    energyStorage.consumeEnergy(3, false);
                    return tank.fill(BucketLikeItem.tryPickupFluidSource(level,state,blockPos,p), false) > 0;
                }
            }
            return true;
        }
    };

    private final BlockConnection defaultFill = new BlockConnection() {

        @Override
        public boolean test(LevelAccessor level, BlockPos pos, @Nullable Direction direction) {
            return (level.getBlockState(pos).getBlock() instanceof LiquidBlockContainer && level.getBlockState(pos).getFluidState().isEmpty()) || level.getBlockState(pos).isAir() ;
        }
        @Override
        public boolean connectionTick(BlockEntity be,BlockPos blockPos, Direction direction) {
            if(isRemoved()) return true;
            IFactoryExpandedStorage storage = (IFactoryExpandedStorage) be;
            if (!storage.fluidSides().isEmpty() && !storage.fluidSides().get().getTransport(direction).canExtract()) return false;
            BlockState state = be.getLevel().getBlockState(blockPos);
            for (IPlatformFluidHandler<?> tank : storage.getTanks()) {
                FluidStack fluidStack = tank.getFluidStack();
                if (progress.first().get() >= progress.first().maxProgress && energyStorage.getEnergyStored() >= 3 &&  fluidStack.getAmount() >= FluidStack.bucketAmount() && (state.isAir() ||  (state.getBlock() instanceof LiquidBlockContainer p &&  p.canPlaceLiquid(level,blockPos,state,fluidStack.getFluid())))){
                    if (level.isClientSide) return true;
                    progress.first().set(0);
                    energyStorage.consumeEnergy(3, false);
                    return ((state.isAir() && level.setBlock(blockPos, fluidStack.getFluid().defaultFluidState().createLegacyBlock(),3)) ||(state.getBlock() instanceof LiquidBlockContainer p && p.placeLiquid(level,blockPos,state,tank.getFluidStack().getFluid().defaultFluidState()))) && !tank.drain(fluidStack.copyWithAmount(FluidStack.bucketAmount()), false).isEmpty();
                }
            }
            return true;
        }
    };

    public FluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.FLUID_PUMP_MENU.get(),Registration.FLUID_PUMP_BLOCK_ENTITY.get(), blockPos, blockState);
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);
        replaceSidedStorage(BlockSide.BACK,fluidSides, new FluidSide(fluidTank,TransportState.EXTRACT));
        replaceSidedStorage(BlockSide.BOTTOM,fluidSides, new FluidSide(fluidTank,TransportState.INSERT));
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.GENERIC, TransportState.EXTRACT_INSERT);
        STORAGE_SLOTS = new int[]{0,1,2};
        additionalSyncInt.add(pumpMode);
    }


    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = super.getSlots(player);
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.INPUT,TransportState.EXTRACT_INSERT,3,56,17){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof BlockItem b && b.getBlock() instanceof FluidPipeBlock;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, 0,56,53, TransportState.INSERT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyFluidItemSlot(this, 1,147,17, SlotsIdentifier.INPUT,TransportState.INSERT));
        slots.add(new FactocraftyFluidItemSlot(this, 2,147,53, SlotsIdentifier.OUTPUT,TransportState.EXTRACT));
        return slots;
    }
    public void tick(){
        super.tick();
        if (!level.isClientSide) {
            boolean showActive = false;
            if (energyStorage.getEnergyStored() > 0) {
                if (getPumpMode().isUsable()) {
                    energyStorage.consumeEnergy((int) Math.pow(72 ,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK)), false);
                    progress.first().add((int) Math.pow(80,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK)));
                    if (!fluidTank.getFluidStack().isEmpty()) showActive = true;
                } else progress.first().shrink(2);
                Function<BlockConnection ,BlockConnection<ConduitBlockEntity<?>>> pump = (connection)-> new BlockConnection<>() {
                    public boolean test(LevelAccessor level, BlockPos pos, @Nullable Direction direction) {
                        if (!isRemoved() && level.getBlockEntity(pos) instanceof FluidPipeBlockEntity otherBe && !otherBe.additionalConnections.contains(this))
                            otherBe.additionalConnections.add(this);
                        return connection.test(level, pos, direction);
                    }
                    public boolean connectionTick(ConduitBlockEntity<?> be, BlockPos pos, Direction direction) {
                        return connection.connectionTick(be, pos, direction);
                    }
                };
                if (getPumpMode().canInsert()) {
                    Direction bottomDirection = BlockSide.BOTTOM.blockStateToFacing(getBlockState());
                    BlockPos pos = getBlockPos().relative(bottomDirection);
                    if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be) {
                        BlockConnection<ConduitBlockEntity<?>> c = pump.apply(defaultDrain);
                        if (!be.additionalConnections.contains(c))
                            be.additionalConnections.add(c);
                    } else if (defaultDrain.test(level, pos, bottomDirection)) {
                        defaultDrain.connectionTick(this, pos, bottomDirection);
                    }
                }
                if (getPumpMode().canExtract()) {
                    Direction back = BlockSide.BACK.blockStateToFacing(getBlockState());
                    BlockPos pos = getBlockPos().relative(back);
                    if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be) {
                        BlockConnection<ConduitBlockEntity<?>> c = pump.apply(defaultFill);
                        if (!be.additionalConnections.contains(c))
                            be.additionalConnections.add(c);
                    } else if (defaultFill.test(level, pos, back)) {
                        defaultFill.connectionTick(this, pos, back);
                    }
                }
            }
            if (getBlockState().getValue(FactocraftyMachineBlock.ACTIVE) != showActive) {
                level.setBlock(getBlockPos(), getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, showActive), 3);
            }
        }
    }

    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        list.add(fluidTank.identifier().differential(), fluidTank);
    }

    @Override
    public List<Progress> getProgresses() {
        return new ArrayList<>(List.of(progress));
    }

}
