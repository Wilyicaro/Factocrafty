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
import wily.factocrafty.block.IFactocraftyCYEnergyBlock;
import wily.factocrafty.block.transport.energy.CableBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.List;

public class GeneratorBlockEntity extends FactocraftyProcessBlockEntity {


    private final int FUEL_SLOT = 0;


    public Progress burnTime = new Progress(Progress.Identifier.BURN_TIME,56,36,0);
    public final Bearer<Integer> energyTick = Bearer.of(0);

    public GeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(FactocraftyMenus.GENERATOR,Registration.GENERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    public GeneratorBlockEntity(FactocraftyMenus menu,BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(menu, FactoryCapacityTiers.BASIC, blockEntityType, blockPos, blockState);
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);
        progress = new Progress(Progress.Identifier.DEFAULT, 80,39,200);
        additionalSyncInt.add(energyTick);
        DRAIN_SLOT = FILL_SLOT = 1;
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
        slots.add(new FactocraftyCYItemSlot(this, FILL_SLOT,147,53, TransportState.INSERT, FactoryCapacityTiers.BASIC){
        });
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
    private boolean isBurning(){return  burnTime.first().get() > 0;}
    public void tick(){
        super.tick();
        if (!level.isClientSide) {
            boolean wasBurning = isBurning();
            int energy = 0;

            boolean hasFluid = fluidTank.getFluidStack().getAmount() >= getPlatformFluidConsume(5);
            if(isBurning()) {
                burnTime.first().shrink(1);
                if (energyStorage.getSpace() > 0 && hasFluid){

                    progress.first().add(1);
                    energy = energyStorage.receiveEnergy(new CraftyTransaction(3, energyStorage.supportableTier), false).energy;
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


            super.tick();
            for (Direction direction : Direction.values()) {
                BlockPos pos = getBlockPos().relative(direction);
                if (level.getBlockState(pos).getBlock() instanceof IFactocraftyCYEnergyBlock energyBlock && energyBlock.isEnergyReceiver() && !(energyBlock instanceof CableBlock)) {
                    IFactoryStorage CYEbe = (IFactoryStorage) level.getBlockEntity(pos);
                    if (CYEbe != null)
                        CYEbe.getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).ifPresent((e)-> transferEnergyTo(direction, e));
                }
            }
            if (wasBurning != getBlockState().getValue(FactocraftyMachineBlock.ACTIVE)) level.setBlock(getBlockPos(),getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, burnTime.first().get() > 0), 3);

        }
    }

    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank.identifier().differential(), fluidTank);
    }

    @Override
    public List<Progress> getProgresses() {
        return new ArrayList<>(List.of(progress,burnTime));
    }

}
