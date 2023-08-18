package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;

import java.util.ArrayList;
import java.util.List;


public class RecyclerBlockEntity extends ProcessMachineBlockEntity<FactocraftyMachineRecipe> {


    public RecyclerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.RECYCLER_MENU.get(),Registration.RECYCLER_RECIPE.get(),Registration.RECYCLER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public List<FactocraftyMachineRecipe> getMatchRecipes(@Nullable ItemStack input, boolean limitItemCount) {
        Container container = new SimpleContainer( input == null ? inventory.getItem(INPUT_SLOT) : input);
        ArrayList<FactocraftyMachineRecipe> matchRecipes = new ArrayList<>(getRecipes().stream().filter(rcp -> rcp.matches(container,level)).toList());
        List<FactocraftyMachineRecipe> defaultRecipe = new ArrayList<>(getRecipes().stream().filter(rcp -> rcp.getIngredients().get(0).isEmpty()).toList());
        matchRecipes.addAll(defaultRecipe);
        return matchRecipes;
    }
    protected void processIngredients(FactocraftyMachineRecipe recipe) {
        inventory.getItem(INPUT_SLOT).shrink( Math.max(1,recipe.getIngredientCount()));
    }

    @Override
    protected SoundEvent getMachineSound() {
        return Registration.COMPRESSOR_ACTIVE.get();
    }

}
