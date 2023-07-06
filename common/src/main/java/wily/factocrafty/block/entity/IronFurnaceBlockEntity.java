package wily.factocrafty.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;

public class IronFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public IronFurnaceBlockEntity( BlockPos blockPos, BlockState blockState) {
        super(Registration.IRON_FURNACE_BLOCK_ENTITY.get(), blockPos, blockState, RecipeType.SMELTING);
    }

    protected Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new FurnaceMenu(i, inventory, this, this.dataAccess);
    }
    @Override
    public void setItem(int i, ItemStack itemStack) {
        ItemStack itemStack2 = this.items.get(i);
        boolean bl = !itemStack.isEmpty() && ItemStack.isSameItemSameTags(itemStack,itemStack2);
        this.items.set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }

        if (i == 0 && !bl) {
            this.cookingTotalTime = (int) (getTotalCookTime(this.level, this) / 1.3);
            this.cookingProgress = 0;
            this.setChanged();
        }


    }
    private boolean isLit() {
        return this.litTime > 0;
    }
    public void tick(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity) {
    serverTick(level, blockPos, blockState, abstractFurnaceBlockEntity);
        ItemStack itemStack = items.get(1);
        boolean bl3 = !(items.get(0)).isEmpty();
        boolean bl4 = !itemStack.isEmpty();
        if (isLit() || bl4 && bl3) {
                if (abstractFurnaceBlockEntity.cookingTotalTime == getTotalCookTime(this.level, this)) {
                    abstractFurnaceBlockEntity.cookingTotalTime = (int) (getTotalCookTime(this.level, this) / 1.3);
                }
            }
    }

    @Override
    protected int getBurnDuration(ItemStack itemStack) {
        return super.getBurnDuration(itemStack) * cookingTotalTime / 200;
    }
}
