package wily.factocrafty.block.machines.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.base.FactoryCapacityTiers;

public class ElectricFurnaceBlockEntity extends FactocraftyMachineBlockEntity {



    public ElectricFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super( FactocraftyMenus.ELECTRIC_FURNACE, FactoryCapacityTiers.BASIC,RecipeType.SMELTING,Registration.ELECTRIC_FURNACE_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public int getTotalProcessTime() {
        return (int) (super.getTotalProcessTime() / 1.3);
    }
}
