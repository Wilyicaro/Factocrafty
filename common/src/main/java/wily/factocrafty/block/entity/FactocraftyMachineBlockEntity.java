package wily.factocrafty.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyResultSlot;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

import java.util.*;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.createExperience;

public class FactocraftyMachineBlockEntity extends FactocraftyProcessBlockEntity {


    protected RecipeType<? extends Recipe<Container>> recipeType;
    public FactocraftyMachineBlockEntity(FactocraftyMenus menu, FactoryCapacityTiers tier, RecipeType<? extends Recipe<Container>> recipe, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(menu,tier,blockEntity,blockPos, blockState);
        this.recipesUsed = new Object2IntOpenHashMap();

        this.recipeType = recipe;
        DRAIN_SLOT = FILL_SLOT = 1;
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);

    }

    protected int INPUT_SLOT = 0;
    protected int OUTPUT_SLOT = 2;

    protected boolean isInputFree(){
        Recipe<Container> rcp = getActualRecipe(null,true);
        return rcp != null && canMachineProcess(rcp);
    }

    protected List<? extends Recipe<Container>> getRecipes(){
        return Objects.requireNonNull(getLevel()).getRecipeManager().getAllRecipesFor(recipeType);
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.BLUE,TransportState.INSERT,INPUT_SLOT, 56,17){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                Recipe<Container> recipe =  getActualRecipe( itemStack, false);
                return recipe != null;
            }
            @Override
            public boolean isActive() {
                Recipe<Container> recipe =  getRecipes().get(0);
                return recipe != null && (!(recipe instanceof AbstractFactocraftyProcessRecipe rcp) || !rcp.hasFluidIngredient()) ;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, DRAIN_SLOT, 56,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyResultSlot(this,player, OUTPUT_SLOT,116,35));
    }
    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!recipesUsed.isEmpty()) {
            CompoundTag compoundTag2 = new CompoundTag();
            this.recipesUsed.forEach((resourceLocation, integer) -> {
                compoundTag2.putInt(resourceLocation.toString(), integer);
            });
            compoundTag.put("RecipesUsed", compoundTag2);
        }
    }
    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (!recipesUsed.isEmpty()) {
            CompoundTag compoundTag2 = compoundTag.getCompound("RecipesUsed");
        Iterator var3 = compoundTag2.getAllKeys().iterator();

        while(var3.hasNext()) {
            String string = (String)var3.next();
            this.recipesUsed.put(new ResourceLocation(string), compoundTag2.getInt(string));
            }
        }
    }

    public final Object2IntOpenHashMap<ResourceLocation> recipesUsed;




    public boolean isProcessing(){
        return (progress.getInt(0) > 0 || (isInputFree() && inventory.getItem(OUTPUT_SLOT).getCount() < inventory.getMaxStackSize()) && energyStorage.getEnergyStored() > 0);
    }
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe){
        return canProcessItem(recipe,inventory,INPUT_SLOT,OUTPUT_SLOT);
    }

    protected static boolean canProcessItem(@Nullable Recipe<?> recipe, Container inv, int input, int output) {
        ItemStack inputStack = inv.getItem(input);
        if (!inputStack.isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.getResultItem();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = inv.getItem(output);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.sameItem(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < inv.getMaxStackSize() && itemStack2.getCount() < itemStack2.getMaxStackSize()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }
    protected static boolean canProcessFluid(@Nullable Recipe<?> recipe, IPlatformFluidHandler tank,@Nullable IPlatformFluidHandler resultTank,Container inv, int output) {
        if (!tank.getFluidStack().isEmpty() && recipe instanceof FactocraftyMachineRecipe rcp && rcp.hasFluidIngredient()) {
            ItemStack itemStack = recipe.getResultItem();
            if (itemStack.isEmpty() && (rcp.hasFluidResult() && rcp.getResultFluid().isEmpty())) {
                return false;
            } else {
                ItemStack itemStack2 = inv.getItem(output);
                boolean bl = resultTank != null;
                if ((itemStack2.isEmpty() || itemStack.isEmpty()) && (rcp.getResultFluid().isEmpty() || bl && resultTank.getFluidStack().isEmpty())) {
                    return true;
                } else if (!itemStack2.sameItem(itemStack) &&  (!bl || !resultTank.getFluidStack().isFluidEqual(rcp.getResultFluid()))) {
                    return false;
                } else if ((itemStack2.getCount() < inv.getMaxStackSize() && itemStack2.getCount() < itemStack2.getMaxStackSize() && !itemStack.isEmpty()) ||( bl && resultTank.getTotalSpace() >= rcp.getResultFluid().getAmount())) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }
    protected SoundEvent getMachineSound(){
        return null;
    }

    public Recipe<Container> getActualRecipe(@Nullable ItemStack input, boolean allowItemLimit){
        Container container = new SimpleContainer( input == null ? inventory.getItem(INPUT_SLOT) : input);
        for (Recipe<Container> a : getRecipes()){
            boolean itemMatches = false;
            for (Ingredient i : a.getIngredients()) if (i.test(container.getItem(0))) itemMatches =true;
            if (a instanceof AbstractFactocraftyProcessRecipe rcp && rcp.hasFluidIngredient() ?  rcp.matchesFluid(fluidTank,level) : allowItemLimit ? a.matches(container,level) : itemMatches ) return a;
        }
        return null;
    }
    public void tick() {
        if (!level.isClientSide) {
            boolean bl2 = false;
            if (getMachineSound() != null && isProcessing() && progress.getInt(0) % 75 == 0) {
                level.playSound(null, worldPosition, getMachineSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            super.tick();

            if (getBlockState().getValue(FactocraftyMachineBlock.ACTIVE) != isProcessing()) {
                level.setBlock(getBlockPos(), getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, this.isProcessing()), 3);
                bl2 = true;
            }
            boolean bl3 = isInputFree();
            if (this.isProcessing()) {

                Recipe<? extends Container> recipe;
                if (bl3) {
                    recipe = getActualRecipe(null,true);
                } else {
                    recipe = null;
                }
                if (recipe != null) this.progress.maxProgress = getTotalProcessTime();

                if (canMachineProcess(recipe)) {


                    if (this.progress.get()[0] >= this.progress.maxProgress) {
                        this.progress.get()[0] = 0;
                        if (process(recipe)) {
                            this.setRecipeUsed(recipe);
                        }

                        bl2 = true;
                    }
                    if (energyStorage.getEnergyStored() > 0) {
                        bl2 = true;
                        energyStorage.consumeEnergy(new ICraftyEnergyStorage.EnergyTransaction(1, energyStorage.supportableTier), false);
                        ++this.progress.get()[0];
                    } else if (!isProcessing()){
                        this.progress.setInt(0, 0);
                    }
                } else if (this.progress.getInt(0) > 0) {
                    this.progress.setInt(0, Mth.clamp(this.progress.getInt(0) - 2, 0, this.progress.maxProgress));
                }

            }


            if (bl2) {
                this.setChanged();
            }
        }

    }
    public int getTotalProcessTime() {
        if (level == null ) return progress.maxProgress;
        Optional<? extends Recipe<Container>> optRcp = Optional.ofNullable(getActualRecipe(null,true));
        if (optRcp.isPresent())
            if( optRcp.get() instanceof AbstractFactocraftyProcessRecipe rcp) {return rcp.getMaxProcess();}
            else if( optRcp.get() instanceof AbstractCookingRecipe rcp) {return rcp.getCookingTime();}
        return 200;
    }

    @Override
    public void addProgresses(List<Progress> list) {
        list.add(progress);
    }

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourceLocation = recipe.getId();
            this.recipesUsed.addTo(resourceLocation, 1);
        }

    }
    protected void setOtherResults(@Nullable Recipe<?> recipe, IPlatformItemHandler inv, int i) {
    }
    protected void addOrSetItem(ItemStack result, Container inv, int index){
        ItemStack resultSlot = inv.getItem(index);
        if (resultSlot.isEmpty()) {
            inv.setItem(index, result.copy());
        } else if (resultSlot.is(result.getItem()) && resultSlot.getCount() < inv.getMaxStackSize()) {
            resultSlot.grow(1);
        }
    }
    protected boolean process(@Nullable Recipe<?> recipe) {
        if (recipe != null && canMachineProcess(recipe)) {
            addOrSetItem(recipe.getResultItem(), inventory, OUTPUT_SLOT);
            setOtherResults(recipe, inventory, inventory.getMaxStackSize());

            if (recipe instanceof FactocraftyMachineRecipe rcp && rcp.hasFluidIngredient()) fluidTank.drain(rcp.getFluidIngredient(),false);
            else inventory.getItem(INPUT_SLOT).shrink(recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getIngredientCount() : 1);

            return true;
        } else {
            return false;
        }
    }
    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(serverPlayer.getLevel(), serverPlayer.position());
        serverPlayer.awardRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel serverLevel, Vec3 vec3) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

        while(var4.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry)var4.next();
            serverLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                Float f = 0F;
                if (recipe instanceof AbstractCookingRecipe rcp) f = rcp.getExperience();
                else if (recipe instanceof AbstractFactocraftyProcessRecipe rcp) f = rcp.getExperience();
                createExperience(serverLevel, vec3, entry.getIntValue(), f);

            });
        }

        return list;
    }
}
