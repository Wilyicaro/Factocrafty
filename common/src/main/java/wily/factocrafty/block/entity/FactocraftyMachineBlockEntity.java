package wily.factocrafty.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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
import wily.factocrafty.item.UpgradeType;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

import java.util.*;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.createExperience;

public class FactocraftyMachineBlockEntity<T extends Recipe<Container>> extends FactocraftyProcessBlockEntity {


    public RecipeType<T> recipeType;
    public FactocraftyMachineBlockEntity(FactocraftyMenus menu, FactoryCapacityTiers tier, RecipeType<T> recipe, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
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

    protected List<T> getRecipes(){
        if (level == null) return List.of();
        return level.getRecipeManager().getAllRecipesFor(recipeType).stream().sorted(Comparator.comparingInt( ca-> ca instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getDiff() : 0)).toList();
    }

    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.INPUT,TransportState.INSERT,INPUT_SLOT, 56,17){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                T recipe =  getActualRecipe( itemStack, false);
                return recipe != null;
            }
            @Override
            public boolean isActive() {
                return isInputSlotActive() && !getRecipes().isEmpty() && getRecipes().get(0) != null ;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, DRAIN_SLOT, 56,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyResultSlot(this,player, OUTPUT_SLOT,116,35).withType(FactoryItemSlot.Type.BIG));
    }
    public boolean isInputSlotActive(){
        return true;
    }
    @Override
    public void saveTag(CompoundTag compoundTag) {
        super.saveTag(compoundTag);
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
            ItemStack itemStack = recipe.getResultItem(RegistryAccess.EMPTY);
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = inv.getItem(output);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItem(itemStack2,itemStack)) {
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
        if (!tank.getFluidStack().isEmpty() && recipe instanceof FactocraftyMachineRecipe rcp && rcp.matchesFluid(tank,null)) {
            ItemStack itemStack = recipe.getResultItem(RegistryAccess.EMPTY);
            if (itemStack.isEmpty() && (rcp.hasFluidResult() && rcp.getResultFluid().isEmpty())) {
                return false;
            } else {
                ItemStack itemStack2 = inv.getItem(output);
                boolean bl = resultTank != null;
                if ((itemStack2.isEmpty() || itemStack.isEmpty()) && (rcp.getResultFluid().isEmpty() || bl && resultTank.getFluidStack().isEmpty() )) {
                    return true;
                } else if (!ItemStack.isSameItem(itemStack2,itemStack) ||  (!bl || !resultTank.getFluidStack().isFluidEqual(rcp.getResultFluid()))) {
                    return false;
                } else if (itemStack2.getCount() < inv.getMaxStackSize() && itemStack2.getCount() < itemStack2.getMaxStackSize() && !itemStack.isEmpty() || resultTank.getTotalSpace() >= rcp.getResultFluid().getAmount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxStackSize() && !itemStack.isEmpty();
                }
            }
        } else {
            return false;
        }
    }
    protected SoundEvent getMachineSound(){
        return null;
    }

    public T getActualRecipe(@Nullable ItemStack input, boolean limitItemCount){
        List<T> matchRecipes = getMatchRecipes(input, limitItemCount);
        return matchRecipes.isEmpty()? null : matchRecipes.get(0);
    }
    public List<T> getMatchRecipes(@Nullable ItemStack input, boolean limitItemCount){
        Container container = new SimpleContainer( input == null ? inventory.getItem(INPUT_SLOT) : input);
        return getRecipes().stream().filter((r)->{
            boolean itemMatches = false;
            for (Ingredient i : r.getIngredients()) if (i.test(container.getItem(0))) itemMatches =true;
            return r instanceof AbstractFactocraftyProcessRecipe rcp && !isInputSlotActive() ?  rcp.matchesFluid(fluidTank,level) : limitItemCount ? r.matches(container,level) : itemMatches;
        }).toList();
    }
    public void tick() {
        if (!level.isClientSide) {
            boolean bl2 = false;
            if (getMachineSound() != null && isProcessing() && progress.getInt(0) % 75 == 0) {
                level.playSound(null, worldPosition, getMachineSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            super.tick();

            boolean bl3 = isInputFree();
            if (this.isProcessing()) {

                T recipe;
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
                    int energy = (recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getEnergyConsume() : 3 * (int)Math.pow(72,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK)));
                    if (energyStorage.getEnergyStored() > energy) {
                        bl2 = true;
                        energyStorage.consumeEnergy(new ICraftyEnergyStorage.EnergyTransaction(energy, energyStorage.supportableTier), false);
                        ++this.progress.get()[0];
                    } else if (!isProcessing()){
                        this.progress.setInt(0, 0);
                    }
                } else if (this.progress.getInt(0) > 0) {
                    this.progress.setInt(0, Mth.clamp(this.progress.getInt(0) - 2, 0, this.progress.maxProgress));
                }
            }

            if (getBlockState().getValue(FactocraftyMachineBlock.ACTIVE) != bl2) {
                level.setBlock(getBlockPos(), getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, this.isProcessing()), 3);
            }
            if (bl2) {
                this.setChanged();
            }
        }

    }
    public int getTotalProcessTime() {
        double d = Math.pow(100,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK));
        if (level == null ) return (int) (progress.maxProgress / d);
        Optional<? extends Recipe<Container>> optRcp = Optional.ofNullable(getActualRecipe(null,true));
        if (optRcp.isPresent())
            if( optRcp.get() instanceof AbstractFactocraftyProcessRecipe rcp) {return (int) (rcp.getMaxProcess() / d);}
            else if( optRcp.get() instanceof AbstractCookingRecipe rcp) {return (int) (rcp.getCookingTime() / d);}
        return (int) (200 / d);
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
    protected void setOtherResults( T recipe, IPlatformItemHandler inv, int i) {
    }
    protected void addOrSetItem(ItemStack result, Container inv, int index){
        ItemStack resultSlot = inv.getItem(index);
        if (resultSlot.isEmpty()) {
            inv.setItem(index, result.copy());
        } else if (resultSlot.is(result.getItem()) && resultSlot.getCount() < inv.getMaxStackSize()) {
            resultSlot.grow(1);
        }
    }
    protected boolean process(@Nullable T recipe) {
        if (recipe != null && canMachineProcess(recipe)) {
            if (isInputSlotActive()) {
                addOrSetItem(recipe.getResultItem(RegistryAccess.EMPTY), inventory, OUTPUT_SLOT);
                inventory.getItem(INPUT_SLOT).shrink(recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getIngredientCount() : 1);
            }
            setOtherResults(recipe, inventory, inventory.getMaxStackSize());

            return true;
        } else {
            return false;
        }
    }
    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer) {
        List<Recipe<?>> list = this.getRecipesToAwardAndPopExperience(serverPlayer.serverLevel(), serverPlayer.position());
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
