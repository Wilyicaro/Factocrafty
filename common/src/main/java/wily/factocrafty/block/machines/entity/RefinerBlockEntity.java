package wily.factocrafty.block.machines.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.FactocraftySyncRefiningTypePacket;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.List;

public class RefinerBlockEntity extends FactocraftyMachineBlockEntity {

    public enum RefiningType{
        FLUID,ITEM;
        public boolean isFluid(){
            return this == FLUID;
        }
        public boolean isItem(){
            return this == ITEM;
        }
    }

    public RefiningType refiningType  = RefiningType.FLUID;
    public RefinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.REFINER, FactoryCapacityTiers.BASIC,Registration.REFINER_FLUID_RECIPE.get(),Registration.REFINER_BLOCK_ENTITY.get(), blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(6 * FluidStack.bucketAmount(), this, f -> {
            for (Recipe<Container> recipe: getRecipes()){
                if (recipe instanceof FactocraftyMachineRecipe rcp && rcp.getFluidIngredient().isFluidEqual(f)) return true;
            }
            return false;
        }, SlotsIdentifier.BLUE, TransportState.EXTRACT_INSERT);

    }

    @Override
    public void tick() {
        recipeType = refiningType.isItem() ? Registration.REFINER_ITEM_RECIPE.get() : Registration.REFINER_FLUID_RECIPE.get();
        super.tick();
    }

    @Override
    public void syncAdditionalMenuData(AbstractContainerMenu menu, Player player) {
        if (menu.getSlot(INPUT_SLOT) !=getSlots(player).get(INPUT_SLOT)) {
            menu.slots.set(INPUT_SLOT,getSlots(player).get(INPUT_SLOT));
        }
        if (player instanceof ServerPlayer sp)
            Factocrafty.NETWORK.sendToPlayer(sp,new FactocraftySyncRefiningTypePacket(worldPosition,refiningType));
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putInt("refiningType",refiningType.ordinal());
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        refiningType =  RefiningType.values()[compoundTag.getInt("refiningType")];
    }

    public IPlatformFluidHandler resultTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.RED, TransportState.EXTRACT_INSERT);



    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
        list.add(resultTank);
    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        return refiningType.isFluid() ? FactocraftyMachineBlockEntity.canProcessFluid(recipe,fluidTank,resultTank,inventory,OUTPUT_SLOT) : super.canMachineProcess(recipe);
    }

    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

    @Override
    protected void setOtherResults(@Nullable Recipe<?> recipe, IPlatformItemHandler inv, int i) {
        if (recipe instanceof FactocraftyMachineRecipe rcp){
            if (rcp.hasFluidResult()) resultTank.fill(rcp.getResultFluid(),false);
        }
    }
}
