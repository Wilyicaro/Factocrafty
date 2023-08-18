package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.Progress;

public class ElectricFurnaceBlockEntity extends ProcessMachineBlockEntity<SmeltingRecipe> {



    public ElectricFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.ELECTRIC_FURNACE_MENU.get(),RecipeType.SMELTING,Registration.ELECTRIC_FURNACE_BLOCK_ENTITY.get(), blockPos, blockState);
        progress = new Progress(Progress.Identifier.DEFAULT,79,35,200);
    }

    @Override
    public int getTotalProcessTime() {
        return (int) (super.getTotalProcessTime() / 1.3);
    }
}
