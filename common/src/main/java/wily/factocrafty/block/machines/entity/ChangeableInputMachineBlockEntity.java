package wily.factocrafty.block.machines.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.List;

public class ChangeableInputMachineBlockEntity extends CompoundResultMachineBlockEntity<FactocraftyMachineRecipe> {
    public enum InputType{
        FLUID,ITEM;
        public String getName(){
            return name().toLowerCase();
        }
        public boolean isFluid(){
            return this == FLUID;
        }
        public boolean isItem(){
            return this == ITEM;
        }
    }
    public Bearer<Integer> inputType = Bearer.of(0);

    public InputType getInputType() {
        return InputType.values()[inputType.get()];
    };

    public ChangeableInputMachineBlockEntity(MenuType<?> menu, RecipeType<? extends FactocraftyMachineRecipe> recipeType, BlockEntityType<? extends ProcessMachineBlockEntity<FactocraftyMachineRecipe>> blockEntity, BlockPos blockPos, BlockState blockState) {
        super(menu, (RecipeType<FactocraftyMachineRecipe>) recipeType,blockEntity, blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(6 * FluidStack.bucketAmount(), this, f -> {
            for (Recipe<Container> recipe: getRecipes())
                if (recipe instanceof FactocraftyMachineRecipe rcp && rcp.hasFluidIngredient() && rcp.getFluidIngredient().isFluidEqual(f)) return true;

            return false;
        }, SlotsIdentifier.INPUT, TransportState.INSERT);
        additionalSyncInt.add(inputType);

    }

    public ChangeableInputMachineBlockEntity(BlockPos blockPos, BlockState blockState){
        this(Registration.EXTRACTOR_MENU.get(),Registration.EXTRACTOR_RECIPE.get(),Registration.EXTRACTOR_BLOCK_ENTITY.get(), blockPos, blockState);
        inputType.set(1);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.EXTRACTOR_ACTIVE.get();
    }

    @Override
    public void saveTag(CompoundTag compoundTag) {
        super.saveTag(compoundTag);
        compoundTag.putInt("inputType", inputType.get());
    }

    @Override
    public boolean isInputSlotActive() {
        return getInputType().isItem();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        inputType.set(compoundTag.getInt("inputType"));
    }
    public IPlatformFluidHandler resultTank;
    @Override
    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        list.add(fluidTank);
    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        return getInputType().isFluid() ? ProcessMachineBlockEntity.canProcessFluid(recipe,fluidTank,resultTank,inventory,OUTPUT_SLOT) : super.canMachineProcess(recipe);
    }
}
