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

public class GasInfuserBlockEntity extends CompoundResultMachineBlockEntity<GasInfuserRecipe>{

    public SlotsIdentifier GAS_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"gas", 0);
    public SlotsIdentifier OI_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"oi", 1);
    public SlotsIdentifier IO_TANK_IDENTIFIER =  new SlotsIdentifier(ChatFormatting.DARK_PURPLE,"io", 2);
    public GasInfuserBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.GAS_INFUSER, Registration.GASEOUS_INFUSION_RECIPE.get(), Registration.GAS_INFUSER_BLOCK_ENTITY.get(), blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this,  f-> FactocraftyExpectPlatform.isGas(f.getFluid()), GAS_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);
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
    protected void setOtherResults(GasInfuserRecipe recipe, IPlatformItemHandler inv, int i) {
        if (getInfusionMode().isMixer()) {
            super.setOtherResults(recipe, inv, i);
            oiTank.drain(recipe.getOtherFluid(), false);
            ioTank.fill(recipe.getResultFluid(), false);
        }else {
            ioTank.drain(recipe.getFluidIngredient(), false);
            fluidTank.fill(recipe.getResultFluid(), false);
            oiTank.fill(recipe.getOtherFluid(), false);
        }
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
        list.add(oiTank);
        list.add(ioTank);
    }
    private boolean canTankAcceptResult(IPlatformFluidHandler<?> tank, FluidStack resultFluid){
        return tank.getTotalSpace() >= resultFluid.getAmount() && (tank.getFluidStack().isEmpty() || tank.getFluidStack().isFluidEqual((resultFluid)));
    }
    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        if (recipe instanceof GasInfuserRecipe rcp) {
            return getInfusionMode().isMixer() ? rcp.matchesFluid(fluidTank,level) && rcp.matchesOtherFluid(oiTank,level) && canTankAcceptResult(ioTank,rcp.getResultFluid()) : rcp.matchesFluid(ioTank,level) && canTankAcceptResult(oiTank,rcp.getOtherFluid()) &&  canTankAcceptResult(fluidTank,rcp.getResultFluid());
        } else return false;

    }
    public List<GasInfuserRecipe> getMatchRecipes(@Nullable ItemStack input, boolean limitItemCount){
        return getRecipes().stream().filter(r-> canMachineProcess(r) && r.getDiff() == infusionMode.get()).toList();
    }
    public IPlatformFluidHandler<?> oiTank = FactoryAPIPlatform.getFluidHandlerApi(4 * FluidStack.bucketAmount(),this, f-> true, OI_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);

    public IPlatformFluidHandler<?> ioTank = FactoryAPIPlatform.getFluidHandlerApi(4 * FluidStack.bucketAmount(),this,f-> true, IO_TANK_IDENTIFIER, TransportState.EXTRACT_INSERT);
}
