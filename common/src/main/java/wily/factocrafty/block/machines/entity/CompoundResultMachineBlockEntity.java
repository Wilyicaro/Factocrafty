package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyResultSlot;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.recipes.FactocraftyMachineRecipe;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.IPlatformItemHandler;

import java.util.List;
import java.util.Map;

public class CompoundResultMachineBlockEntity<T extends AbstractFactocraftyProcessRecipe> extends FactocraftyMachineBlockEntity<T> {



    protected static int OTHER_RESULT_SLOT = 3;

    public CompoundResultMachineBlockEntity(FactocraftyMenus menu, RecipeType<T> recipe, BlockEntityType<? extends FactocraftyMachineBlockEntity<T>> be, BlockPos blockPos, BlockState blockState) {
        super(menu, FactoryCapacityTiers.BASIC,recipe,be, blockPos, blockState);
    }


    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        super.addSlots(slots,player);
        slots.set(OUTPUT_SLOT, new FactocraftyResultSlot(this,player, OUTPUT_SLOT,129,35).withType(FactoryItemSlot.Type.BIG));
        slots.add(new FactocraftyResultSlot(this, player, OTHER_RESULT_SLOT, 107,35));
    }

    protected void addBasicSlots(NonNullList<FactoryItemSlot> slots, @Nullable Player player) {
        super.addSlots(slots,player);
    }
    @Override
    protected SoundEvent getMachineSound() {
        return Registration.MACERATOR_ACTIVE.get();
    }

    @Override
    protected void setOtherResults(T recipe, IPlatformItemHandler inv, int i) {
        if (recipe.hasFluidIngredient()) fluidTank.drain(recipe.getFluidIngredient(),false);
        if (recipe.hasItemIngredient()) {
            Map<ItemStack, Float> map = recipe.getOtherResults();
            for (int j = 0; j < map.size(); j++) {
                List<Map.Entry<ItemStack, Float>> list = map.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList();
                ItemStack result = list.get(j).getKey();
                ItemStack resultSlot = inv.getItem(OTHER_RESULT_SLOT);
                if (level.random.nextFloat() <= map.entrySet().stream().toList().get(j).getValue() && !result.isEmpty() && (result.is(resultSlot.getItem()) || resultSlot.isEmpty())) {
                    addOrSetItem(result, inv, OTHER_RESULT_SLOT);
                    return;
                }
            }
        }
    }
}
