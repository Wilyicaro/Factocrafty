package wily.factocrafty.util.registering;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import wily.factocrafty.init.Registration;

public enum FactocraftyBlockEntities {
    MACHINE_FRAME,ADVANCED_MACHINE_FRAME,GEOTHERMAL_GENERATOR,GENERATOR,
    TIN_CABLE,COPPER_CABLE,GOLD_CABLE,GOLD_TIN_CABLE,SILVER_CABLE,CRYSTAL_CABLE;

    public BlockEntityType<?> get(){
        return Registration.getRegistrarBlockEntityEntry(getName());
    }

    public static<T extends BlockEntity> BlockEntityType<T> ofBlock(Block block){
        return Registration.getRegistrarBlockEntityEntry(block.arch$registryName().getPath());
    }


    public String getName(){
        return name().toLowerCase();
    }
}
