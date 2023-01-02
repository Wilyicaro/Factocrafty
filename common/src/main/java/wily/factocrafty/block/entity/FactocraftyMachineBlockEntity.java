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
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyResultSlot;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.*;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.createExperience;

public class FactocraftyMachineBlockEntity extends FactocraftyProcessBlockEntity {

    public FactocraftyMachineBlockEntity(FactocraftyMenus menu, FactoryCapacityTiers tier, RecipeType<? extends Recipe<Container>> recipe, BlockEntityType blockEntity, BlockPos blockPos, BlockState blockState) {
        super(menu,tier,blockEntity,blockPos, blockState);
        this.recipesUsed = new Object2IntOpenHashMap();
        this.quickCheck = RecipeManager.createCheck(recipe);
        UNCHARGE_SLOT = CHARGE_SLOT = 1;
        replaceSidedStorage(BlockSide.FRONT,energySides, TransportState.NONE);

    }

    protected static final int INPUT_SLOT = 0;
    protected static final int OUTPUT_SLOT = 2;
    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        slots.add(new FactoryItemSlot(this.inventory, SlotsIdentifier.BLUE,TransportState.INSERT,INPUT_SLOT, 56,17){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                Recipe<Container> recipe =  quickCheck.getRecipeFor(new SimpleContainer(itemStack), level).orElse(null);
                return recipe != null && !recipe.getResultItem().isEmpty();
            }
        });
        slots.add(new FactocraftyCYItemSlot(this,UNCHARGE_SLOT, 56,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
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
    public final RecipeManager.CachedCheck<Container, ? extends Recipe<Container>> quickCheck;




    public boolean isBurning(){
        return (progress.getInt(0) > 0 || !inventory.getItem(INPUT_SLOT).isEmpty() && inventory.getItem(OUTPUT_SLOT).getCount() < inventory.getMaxStackSize()) && energyStorage.getEnergyStored() > 0;
    }
    protected static boolean canBurn(@Nullable Recipe<?> recipe, Container inv, int i) {
        ItemStack input = inv.getItem(INPUT_SLOT);
        if (!input.isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.getResultItem();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = inv.getItem(OUTPUT_SLOT);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.sameItem(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < i && itemStack2.getCount() < itemStack2.getMaxStackSize()) {
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

    public void tick() {
        if (!level.isClientSide) {
            boolean bl2 = false;
            if (getMachineSound() != null && isBurning() && progress.getInt(0) % 75 == 0) {
                level.playSound(null, worldPosition, getMachineSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            super.tick();

            if (getBlockState().getValue(FactocraftyMachineBlock.ACTIVE) != isBurning()) {
                level.setBlock(getBlockPos(), getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, this.isBurning()), 3);
                bl2 = true;
            }
            boolean bl3 = !(this.inventory.getItem(INPUT_SLOT)).isEmpty();
            if (this.isBurning() || bl3) {
                this.progress.maxProgress = getTotalProcessTime();

                Recipe recipe;
                if (bl3) {
                    recipe = (Recipe) this.quickCheck.getRecipeFor(this.inventory, level).orElse(null);
                } else {
                    recipe = null;
                }

                int i = this.inventory.getMaxStackSize();
                if (canBurn(recipe, this.inventory, i)) {

                    if (this.progress.get()[0] >= this.progress.maxProgress) {
                        this.progress.get()[0] = 0;
                        if (burn(recipe, inventory, i)) {
                            this.setRecipeUsed(recipe);
                        }

                        bl2 = true;
                    }
                    if (energyStorage.getEnergyStored() > 0) {
                        bl2 = true;
                        energyStorage.consumeEnergy(new ICraftyEnergyStorage.EnergyTransaction(1, energyStorage.supportableTier), false);
                        ++this.progress.get()[0];
                    } else if (!isBurning()){
                        this.progress.setInt(0, 0);
                    }
                } else if (this.progress.getInt(0) > 0 && !bl3) {
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
        Optional<? extends Recipe<Container>> optRcp = quickCheck.getRecipeFor( inventory,level);

        if (optRcp.isPresent())
            if( optRcp.get() instanceof AbstractFactocraftyProcessRecipe rcp) {return rcp.getMaxProcess();}
            else if( optRcp.get() instanceof AbstractCookingRecipe rcp) {return rcp.getCookingTime();}
        return progress.maxProgress;
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
    protected boolean burn(@Nullable Recipe<?> recipe, IPlatformItemHandler inv, int i) {
        if (recipe != null && canBurn(recipe, inv, i)) {
            ItemStack itemStack = inv.getItem(INPUT_SLOT);
            addOrSetItem(recipe.getResultItem(), inv, OUTPUT_SLOT);
            setOtherResults(recipe, inv, i);

            itemStack.shrink(recipe instanceof AbstractFactocraftyProcessRecipe rcp ? rcp.getIngredientCount() : 1);
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
