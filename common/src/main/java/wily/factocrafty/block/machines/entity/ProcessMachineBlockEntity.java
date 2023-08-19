package wily.factocrafty.block.machines.entity;

import com.google.common.collect.Lists;
import dev.architectury.fluid.FluidStack;
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
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyResultSlot;
import wily.factocrafty.item.UpgradeType;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factoryapi.base.*;

import java.util.*;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.createExperience;

public class ProcessMachineBlockEntity<T extends Recipe<Container>> extends FactocraftyMenuBlockEntity implements IFactoryProgressiveStorage {


    protected RecipeType<T> recipeType;

    public Progress progress = new Progress(Progress.Identifier.DEFAULT,80,40,200);

    public ProcessMachineBlockEntity(MenuType<?> menu, RecipeType<T> recipe, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(menu,blockEntity,blockPos, blockState);
        this.recipesUsed = new Object2IntOpenHashMap();
        this.recipeType = recipe;
        STORAGE_SLOTS = new int[]{1};
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);

    }

    protected int INPUT_SLOT = 0;
    protected int OUTPUT_SLOT = 2;


    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    protected List<T> getRecipes(){
        if (level == null) return List.of();
        return level.getRecipeManager().getAllRecipesFor(recipeType).stream().sorted(Comparator.comparingInt( ca-> ca instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getDiff() : 0)).toList();
    }


    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = super.getSlots(player);
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.INPUT,TransportState.INSERT,INPUT_SLOT, 56,17){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return getActualRecipe( itemStack, false) != null;
            }
            @Override
            public boolean isActive() {
                return isInputSlotActive() && !getRecipes().isEmpty() && getRecipes().get(0) != null ;
            }
        });
        slots.add(new FactocraftyCYItemSlot(this, 1, 56,53, TransportState.INSERT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyResultSlot(this,player, OUTPUT_SLOT,116,35).withType(FactoryItemSlot.Type.BIG));
        return slots;
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




    public boolean isActive(){
        return progress.first().get() > 0;
    }
    protected boolean canMachineProcess(@Nullable Recipe<?> recipe){
        return canProcessItem(recipe,inventory,INPUT_SLOT,OUTPUT_SLOT);
    }

    protected static boolean canProcessItem(@Nullable Recipe<?> recipe, Container inv, int input, int output) {
        ItemStack inputStack = inv.getItem(input);
        if (!inputStack.isEmpty() && recipe != null) {
            return canSlotAcceptItem(inv,output, recipe.getResultItem(RegistryAccess.EMPTY));
        }return false;

    }
    protected static boolean canTankAcceptResult(IPlatformFluidHandler<?> tank, FluidStack resultFluid){
        return tank.getTotalSpace() >= resultFluid.getAmount() && (tank.getFluidStack().isEmpty() || tank.getFluidStack().isFluidEqual((resultFluid)));
    }
    protected static boolean canSlotAcceptItem(Container inv, int slot, ItemStack stack){
        ItemStack slotStack = inv.getItem(slot);
        return !stack.isEmpty() && (slotStack.isEmpty() ||(ItemStack.isSameItem(stack,slotStack) &&   slotStack.getCount() + stack.getCount() < Math.min(inv.getMaxStackSize(),slotStack.getMaxStackSize())));
    }
    protected static boolean canProcessFluid(@Nullable Recipe<?> recipe, IPlatformFluidHandler tank,@Nullable IPlatformFluidHandler resultTank,Container inv, int output) {
        if (!tank.getFluidStack().isEmpty() && recipe instanceof FactocraftyMachineRecipe rcp && rcp.matchesFluid(tank,null)) {
            ItemStack itemStack = recipe.getResultItem(RegistryAccess.EMPTY);
            if (!itemStack.isEmpty()) {
                return canSlotAcceptItem(inv,output, recipe.getResultItem(RegistryAccess.EMPTY));
            }else if (!rcp.getResultFluid().isEmpty() && resultTank != null){
                return canTankAcceptResult(resultTank,rcp.getResultFluid());
            }
        } return false;
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

            super.tick();

            if (energyStorage.getEnergyStored() > 0) {
                T recipe = getActualRecipe(null,true);


                if (recipe != null && canMachineProcess(recipe)) {
                    this.progress.first().maxProgress = getTotalProcessTime();
                    if (getMachineSound() != null && progress.first().get() % 75 == 0) {
                        level.playSound(null, worldPosition, getMachineSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    if (this.progress.first().get() >= this.progress.first().maxProgress) {
                        this.progress.first().set(0);
                        if (process(recipe)) {
                            this.setRecipeUsed(recipe);
                        }

                        bl2 = true;
                    }
                    int energy = (recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getEnergyConsume() : 3) * (int)Math.pow(72,storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK));
                    if (energyStorage.getEnergyStored() > energy) {
                        bl2 = true;
                        energyStorage.consumeEnergy(new CraftyTransaction(energy, energyStorage.supportableTier), false);
                        this.progress.first().add((int) Math.pow(getTotalProcessTime(), storedUpgrades.getUpgradeEfficiency(UpgradeType.OVERCLOCK)));
                    } else if (!isActive()){
                        this.progress.first().set(0);
                    }
                } else if (this.progress.first().get() > 0) {
                    this.progress.first().shrink(2);
                }
            }

            if (getBlockState().getValue(FactocraftyMachineBlock.ACTIVE) != bl2) {
                level.setBlock(getBlockPos(), getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, bl2), 3);
            }
            if (bl2) {
                this.setChanged();
            }
        }

    }
    public int getTotalProcessTime() {
        if (level == null ) return progress.first().maxProgress;
        Optional<? extends Recipe<Container>> optRcp = Optional.ofNullable(getActualRecipe(null,true));
        if (optRcp.isPresent())
            if( optRcp.get() instanceof AbstractFactocraftyProcessRecipe rcp) {return rcp.getMaxProcess();}
            else if( optRcp.get() instanceof AbstractCookingRecipe rcp) {return rcp.getCookingTime();}
        return 200;
    }

    @Override
    public List<Progress> getProgresses() {
        return new ArrayList<>(List.of(progress));
    }

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation resourceLocation = recipe.getId();
            this.recipesUsed.addTo(resourceLocation, 1);
        }

    }
    protected void processResults(T recipe) {
        if (!(recipe instanceof AbstractFactocraftyProcessRecipe rcp) || (rcp.getResultChance() >= 1.0F || level.random.nextFloat() <= rcp.getResultChance()))
         addOrSetItem(recipe.getResultItem(RegistryAccess.EMPTY), inventory, OUTPUT_SLOT);
    }
    protected void processIngredients(T recipe) {
        if (isInputSlotActive())
            inventory.getItem(INPUT_SLOT).shrink(recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getIngredientCount() : 1);
    }
    protected int addOrSetItem(ItemStack stack, Container inv, int index){
        ItemStack slotStack = inv.getItem(index);

        if (slotStack.isEmpty()) {
            inv.setItem(index, stack.copy());
            return Math.min(inv.getMaxStackSize(),stack.getCount());
        } else {
            int resultCount =  slotStack.getCount() + stack.getCount();
            int maxStack = Math.min(inv.getMaxStackSize(),slotStack.getMaxStackSize());
            if (slotStack.is(stack.getItem()) && slotStack.getCount() < maxStack) {
                if (resultCount <= maxStack) {
                    slotStack.grow(Math.max(stack.getCount(), 1));
                    return Math.max(stack.getCount(), 1);
                }
                int count = maxStack - slotStack.getCount();
                slotStack.setCount(maxStack);
                return count;
            }
        }
        return 0;
    }
    protected boolean process(@Nullable T recipe) {
        if (recipe != null && canMachineProcess(recipe)) {
            processIngredients(recipe);
            processResults(recipe);
            return true;
        }
        return false;
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
