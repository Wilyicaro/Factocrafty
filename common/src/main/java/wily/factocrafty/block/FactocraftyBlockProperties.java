package wily.factocrafty.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class FactocraftyBlockProperties {
    public static final BlockBehaviour.Properties PETROLEUM = getLiquidProperties().mapColor(MapColor.COLOR_BLACK);

    public static final BlockBehaviour.Properties GASOLINE =  getLiquidProperties().mapColor(MapColor.PODZOL);

    public static final BlockBehaviour.Properties COOLANT =  getLiquidProperties().mapColor(MapColor.COLOR_CYAN);

    public static final BlockBehaviour.Properties LATEX =  getLiquidProperties().mapColor(MapColor.SNOW);


    public static BlockBehaviour.Properties getLiquidProperties(){
        return BlockBehaviour.Properties.of().replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY);
    }
    public static BlockBehaviour.Properties getGasProperties(){
        return BlockBehaviour.Properties.of().replaceable().noCollission().strength(100.0F).noLootTable().sound(SoundType.EMPTY).isValidSpawn((a,b,c,d)-> false).air();
    }
}
