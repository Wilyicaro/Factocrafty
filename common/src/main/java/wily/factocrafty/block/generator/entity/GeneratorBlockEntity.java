package wily.factocrafty.block.generator.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class GeneratorBlockEntity extends FactocraftyMenuBlockEntity implements IFactoryProgressiveStorage {


    private final int FUEL_SLOT = 0;


    public Progress burnTime = new Progress(Progress.Identifier.BURN_TIME,56,36,0);
    public Progress progress = new Progress(Progress.Identifier.DEFAULT, 80,39,200);
    public final Bearer<Integer> energyTick = Bearer.of(0);

    public GeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(Registration.GENERATOR_MENU.get(),Registration.GENERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    public GeneratorBlockEntity(MenuType<?> menu, BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(menu, blockEntityType, blockPos, blockState);
        replaceSidedStorage(BlockSide.FRONT,energySides, new TransportSide(SlotsIdentifier.ENERGY,TransportState.NONE));
        additionalSyncInt.add(energyTick);
        STORAGE_SLOTS = new int[]{0,1};
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> f.getFluid() == Fluids.WATER, SlotsIdentifier.WATER, TransportState.INSERT);
    }


    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = super.getSlots(player);
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.FUEL,TransportState.EXTRACT_INSERT,FUEL_SLOT,56,53){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return FuelRegistry.get(itemStack) > 0;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, 1,148,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
        return slots;
    }

    @Override
    public int getInitialEnergyCapacity() {
        return super.getInitialEnergyCapacity() / 2;
    }

    protected void consumeFuel(){
        ItemStack fuel = inventory.getItem(FUEL_SLOT);
        int ticks = FuelRegistry.get(fuel);
        burnTime.first().maxProgress = ticks;
        burnTime.first().set(ticks);
        if (ItemContainerUtil.isFluidContainer(fuel)) {
            ItemContainerUtil.ItemFluidContext context = ItemContainerUtil.drainItem(FluidStack.bucketAmount(),fuel);
            inventory.setItem(FUEL_SLOT, context.container());
        } else inventory.getItem(FUEL_SLOT).shrink(1);


    }
    protected boolean canConsumeFuel(){
        return !inventory.getItem(FUEL_SLOT).isEmpty();
    }
    protected int getPlatformFluidConsume(double multiplier){
        return (int) (multiplier * FluidStack.bucketAmount() * 0.01 );
    }
    protected boolean isBurning(){return  burnTime.first().get() > 0;}

    protected int getGenerationRate(){
        return 3;
    }
    public void serverTick(){
        boolean wasBurning = isBurning();
        int energy = 0;

        boolean hasFluid = fluidTank.getFluidStack().getAmount() >= getPlatformFluidConsume(5);
        if(isBurning()) {
            burnTime.first().shrink(1);
            if (energyStorage.getSpace() > 0 && hasFluid){
                progress.first().add(1);
                energy = energyStorage.receiveEnergy(new CraftyTransaction(getGenerationRate(), energyStorage.supportedTier), false).energy;
                if (progress.first().get() >= progress.first().maxProgress) {
                    fluidTank.drain(getPlatformFluidConsume(5),false);
                    progress.first().set(0);
                }
            }
        } else{
            if (progress.first().get() > 0) progress.first().shrink(2); else progress.first().set(0);

            if (hasFluid && canConsumeFuel() && energyStorage.getSpace() > 0)
                consumeFuel();

        }
        energyTick.set(energy);

        for (Direction direction : Direction.values()) {
            BlockPos pos = getBlockPos().relative(direction);
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && (!(be.getBlockState().getBlock() instanceof IFactocraftyCYEnergyBlock b) || b.isEnergyReceiver()))
                FactoryAPIPlatform.getPlatformFactoryStorage(be).getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).ifPresent((e)-> transferEnergyTo(this, direction, e));
        }
        if (wasBurning != getBlockState().getValue(FactocraftyMachineBlock.ACTIVE)) level.setBlock(getBlockPos(),getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, burnTime.first().get() > 0), 3);

    }
    @Override
    public void saveTag(CompoundTag compoundTag) {
        IFactoryProgressiveStorage.super.saveTag(compoundTag);
    }

    @Override
    public void loadTag(CompoundTag compoundTag) {
        IFactoryProgressiveStorage.super.loadTag(compoundTag);
    }
    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        list.add(fluidTank);
    }

    @Override
    public List<Progress> getProgresses() {
        return new ArrayList<>(List.of(progress,burnTime));
    }

}
