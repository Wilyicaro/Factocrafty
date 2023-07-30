package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.List;

public class RefinerBlockEntity extends ChangeableInputMachineBlockEntity {

    public Bearer<Integer> recipeIndex = Bearer.of(0);
    public Bearer<Integer> recipeSize = Bearer.of(0);
    public Bearer<Integer> recipeHeat = Bearer.of(0);

    public RefinerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.REFINER, Registration.REFINER_RECIPE.get(),Registration.REFINER_BLOCK_ENTITY.get(), blockPos, blockState);
        inputType = InputType.FLUID;
        resultTank = FactoryAPIPlatform.getFluidHandlerApi(getTankCapacity(), this, f -> true, SlotsIdentifier.OUTPUT, TransportState.EXTRACT);
        additionalSyncInt.add(recipeIndex);
        additionalSyncInt.add(recipeSize);
        additionalSyncInt.add(recipeHeat);
    }

    @Override
    public FactocraftyMachineRecipe getActualRecipe(@Nullable ItemStack input, boolean limitItemCount) {
        List<FactocraftyMachineRecipe> rcps = getMatchRecipes(input, limitItemCount);
        recipeIndex.set(Math.max(recipeIndex.get(),0));
        FactocraftyMachineRecipe r = rcps.isEmpty() ? null : rcps.get(Math.min(recipeIndex.get(),rcps.size() -1));
        if (r!=null){
            recipeSize.set(rcps.size());
            recipeHeat.set(r.getDiff());
        }
        return r;
    }

    @Override
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        return getBasicSlots(player);
    }

    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        super.addTanks(list);
        list.add(resultTank);
    }

    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

    @Override
    protected void processResults(FactocraftyMachineRecipe rcp) {
        super.processResults(rcp);
        if (rcp.hasFluidResult() && !rcp.getResultFluid().isEmpty()) resultTank.fill(rcp.getResultFluid(),false);
    }
}
