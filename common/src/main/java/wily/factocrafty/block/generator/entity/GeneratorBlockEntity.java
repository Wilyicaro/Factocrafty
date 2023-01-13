package wily.factocrafty.block.generator.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.cable.InsulatedCableBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.Progress;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.List;

public class GeneratorBlockEntity extends FactocraftyProcessBlockEntity {


    private final int FUEL_SLOT = 0;


    public Progress burnTime = new Progress(FactocraftyProgressType.BURN_PROGRESS,1,0);
    public Progress progress = new Progress(FactocraftyProgressType.ENERGY_PROGRESS, getProgress(), 200);

    public GeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(FactocraftyMenus.GENERATOR,Registration.GENERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    public GeneratorBlockEntity(FactocraftyMenus menu,BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(menu, FactoryCapacityTiers.BASIC, blockEntityType, blockPos, blockState);
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);
        DRAIN_SLOT = FILL_SLOT = 1;
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> f.getFluid() == Fluids.WATER, SlotsIdentifier.WATER, TransportState.EXTRACT_INSERT);
    }


    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.ORANGE,TransportState.EXTRACT_INSERT,FUEL_SLOT,56,53){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return FuelRegistry.get(itemStack) > 0;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, FILL_SLOT,147,53, TransportState.INSERT, FactoryCapacityTiers.BASIC){
        });
    }
    protected void consumeFuel(){
        ItemStack fuel = inventory.getItem(FUEL_SLOT);
        burnTime.setInt(0,FuelRegistry.get(fuel));
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
    private boolean isBurning(){return  burnTime.get()[0] > 0;}
    public void tick(){
        super.tick();
        if (!level.isClientSide) {
            boolean wasBurning = isBurning();

            if(isBurning()) {
                burnTime.get()[0]--;
                if (energyStorage.getSpace() > 0)
                    for (int i = 0; i < progress.get().length; i++) {
                        progress.get()[i]++;
                        energyStorage.receiveEnergy(new ICraftyEnergyStorage.EnergyTransaction(3, energyStorage.supportableTier), false);
                        if (progress.get()[i] >= progress.maxProgress) {
                            fluidTank.drain(getPlatformFluidConsume(5),false);
                            progress.get()[i] = 0;
                    }
                }
            } else for (int i = 0; i < progress.get().length; i++) { if (progress.get()[i]>0) progress.get()[i]--; else progress.get()[i] = 0;}

            if (fluidTank.getFluidStack().getAmount() >= getPlatformFluidConsume(5) && burnTime.getInt(0) == 0 && canConsumeFuel() && energyStorage.getSpace() > 0) {
                consumeFuel();
                burnTime.maxProgress = burnTime.getInt(0);
            }
            super.tick();
            for (Direction direction : Direction.values()) {
                BlockPos pos = getBlockPos().relative(direction);
                if (level.getBlockState(pos).getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && energyBlock.isEnergyReceiver() && !(energyBlock instanceof InsulatedCableBlock)) {
                    IFactoryStorage CYEbe = (IFactoryStorage) level.getBlockEntity(pos);
                    if (CYEbe != null)
                        CYEbe.getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).ifPresent((e)-> energyStorage.consumeEnergy(e.receiveEnergy(new ICraftyEnergyStorage.EnergyTransaction(energyStorage.getEnergyStored(), energyStorage.storedTier), false), false));
                }
            }
            if (wasBurning != isBurning()) level.setBlock(getBlockPos(),getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, burnTime.getInt(0) > 0), 3);

        }
    }

    @Override
    public boolean hasProgress() {
        return super.hasProgress();
    }

    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank.identifier().differential(), fluidTank);
    }

    @Override
    public void addProgresses(List<Progress> list) {
        list.add(progress);
        list.add(burnTime);
    }

}
