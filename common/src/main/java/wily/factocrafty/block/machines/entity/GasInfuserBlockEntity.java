package wily.factocrafty.block.machines.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.recipes.GasInfuserRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.List;
import java.util.stream.Stream;

public class GasInfuserBlockEntity extends CompoundResultMachineBlockEntity<GasInfuserRecipe>{

    public SlotsIdentifier GAS_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.LIGHT_PURPLE,"gas", 0);
    public SlotsIdentifier IO_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"io", 1);
    public SlotsIdentifier OI_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.DARK_RED,"oi", 2);
    public GasInfuserBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.GAS_INFUSER, Registration.GASEOUS_INFUSION_RECIPE.get(), Registration.GAS_INFUSER_BLOCK_ENTITY.get(), blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f ->  FactocraftyExpectPlatform.isGas(f.getFluid()) && getFilteredRecipes().anyMatch(rcp-> getInfusionMode().isMixer() ? rcp.getFluidIngredient().isFluidEqual(f) : rcp.getResultFluid().isFluidEqual(f)), GAS_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);
        additionalSyncInt.add(infusionMode);
        DRAIN_SLOT = FILL_SLOT = 0;
    }

    @Override
    public boolean isInputSlotActive() {
        return false;
    }

    public enum InfusionMode {
        MIXER, ELECTROLYZER;
        public String getName(){
            return name().toLowerCase();
        }
        public boolean isMixer(){
            return this == MIXER;
        }
        public boolean isElectrolyzer(){
            return this == ELECTROLYZER;
        }
    }
    public Bearer<Integer> infusionMode = Bearer.of(InfusionMode.MIXER.ordinal());

    public InfusionMode getInfusionMode(){
        return InfusionMode.values()[infusionMode.get()];
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactocraftyCYItemSlot(this, DRAIN_SLOT, 56,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
    }

    @Override
    protected void processResults(GasInfuserRecipe recipe) {
        if (getInfusionMode().isMixer()) {
            oiTank.fill(recipe.getResultFluid(), false);
        }else {
            fluidTank.fill(recipe.getResultFluid(), false);
            ioTank.fill(recipe.getOtherFluid(), false);
        }
    }

    @Override
    protected void processIngredients(GasInfuserRecipe recipe) {
        if (getInfusionMode().isMixer()) {
            super.processIngredients(recipe);
            ioTank.drain(recipe.getOtherFluid(), false);
        }else {
            oiTank.drain(recipe.getFluidIngredient(), false);
        }
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
        list.add(ioTank);
        list.add(oiTank);
    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        if (recipe instanceof GasInfuserRecipe rcp) {
            return getInfusionMode().isMixer() ? rcp.matchesFluid(fluidTank,level) && rcp.matchesOtherFluid(ioTank,level) && canTankAcceptResult(oiTank,rcp.getResultFluid()) : rcp.matchesFluid(oiTank,level) && canTankAcceptResult(ioTank,rcp.getOtherFluid()) &&  canTankAcceptResult(fluidTank,rcp.getResultFluid());
        }
        return false;
    }
    public List<GasInfuserRecipe> getMatchRecipes(@Nullable ItemStack input, boolean limitItemCount){
        return getFilteredRecipes().filter(this::canMachineProcess).toList();
    }
    public Stream<GasInfuserRecipe> getFilteredRecipes(){
        return getRecipes().stream().filter(r-> r.getDiff() == infusionMode.get());
    }
    public IPlatformFluidHandler<?> ioTank = FactoryAPIPlatform.getFluidHandlerApi(4 * FluidStack.bucketAmount(),this, f->  getFilteredRecipes().anyMatch(rcp-> rcp.getOtherFluid().isFluidEqual(f)), IO_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);

    public IPlatformFluidHandler<?> oiTank = FactoryAPIPlatform.getFluidHandlerApi(4 * FluidStack.bucketAmount(),this, f->  getFilteredRecipes().anyMatch(rcp-> getInfusionMode().isElectrolyzer() ? rcp.getFluidIngredient().isFluidEqual(f) : rcp.getResultFluid().isFluidEqual(f)), OI_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);
}
