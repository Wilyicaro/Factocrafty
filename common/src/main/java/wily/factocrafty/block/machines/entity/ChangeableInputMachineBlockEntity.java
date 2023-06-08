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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.FactocraftySyncInputTypePacket;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
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
    public InputType inputType = InputType.ITEM;



    public ChangeableInputMachineBlockEntity(FactocraftyMenus menu, RecipeType<FactocraftyMachineRecipe> recipeType, BlockEntityType<? extends FactocraftyMachineBlockEntity<FactocraftyMachineRecipe>> blockEntity, BlockPos blockPos, BlockState blockState) {
        super(menu,recipeType,blockEntity, blockPos, blockState);
        fluidTank = FactoryAPIPlatform.getFluidHandlerApi(6 * FluidStack.bucketAmount(), this, f -> {
            for (Recipe<Container> recipe: getRecipes())
                if (recipe instanceof FactocraftyMachineRecipe rcp && rcp.hasFluidIngredient() && rcp.getFluidIngredient().isFluidEqual(f)) return true;

            return false;
        }, SlotsIdentifier.INPUT, TransportState.INSERT);

    }
    public ChangeableInputMachineBlockEntity(BlockPos blockPos, BlockState blockState){
        this(FactocraftyMenus.EXTRACTOR,Registration.EXTRACTOR_RECIPE.get(),Registration.EXTRACTOR_BLOCK_ENTITY.get(), blockPos, blockState);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.EXTRACTOR_ACTIVE.get();
    }

    @Override
    public void syncAdditionalMenuData(AbstractContainerMenu menu, ServerPlayer player) {
        super.syncAdditionalMenuData(menu,player);
        Factocrafty.NETWORK.sendToPlayer(player,new FactocraftySyncInputTypePacket(worldPosition, inputType));
    }

    @Override
    public void saveTag(CompoundTag compoundTag) {
        super.saveTag(compoundTag);
        compoundTag.putInt("inputType", inputType.ordinal());
    }

    @Override
    public boolean isInputSlotActive() {
        return inputType.isItem();
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        inputType =  InputType.values()[compoundTag.getInt("inputType")];
    }
    public IPlatformFluidHandler resultTank;
    @Override
    public void addTanks(List<IPlatformFluidHandler> list) {
        list.add(fluidTank);
    }

    @Override
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe) {
        return inputType.isFluid() ? FactocraftyMachineBlockEntity.canProcessFluid(recipe,fluidTank,resultTank,inventory,OUTPUT_SLOT) : super.canMachineProcess(recipe);
    }
}
