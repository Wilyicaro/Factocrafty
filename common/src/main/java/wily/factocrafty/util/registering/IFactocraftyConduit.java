package wily.factocrafty.util.registering;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.VoxelShape;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.TransportState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface IFactocraftyConduit<T extends Enum<T> & IFactocraftyConduit<T,B,BE>,B extends Block, BE extends BlockEntity> extends IFactocraftyLazyRegistry<Block> {

    Shape getConduitShape();
    FactoryCapacityTiers getCapacityTier();
    default BlockEntityType<BE> getBlockEntity(){
        return Objects.requireNonNull(Registration.getRegistrarBlockEntityEntry(getName()), "There is no conduit with that name");
    }
    default B get(){
        return (B) Objects.requireNonNull(Registration.getRegistrarBlockEntry(getName()), "There is no conduit with that name");
    }
    Storages.Storage<?> getTransferenceStorage();

    default boolean sameStorage(IFactocraftyConduit<?,?,?> c){
        return getTransferenceStorage() == c.getTransferenceStorage();
    }

    default List<T> getSupportedConduits(FactoryCapacityTiers capacityTier){
        List<T> list = new ArrayList<>();
        T e = ((T)this);
        for (T tier : e.getDeclaringClass().getEnumConstants()){
            if (tier.getCapacityTier().ordinal() <= capacityTier.ordinal())list.add(tier);
        }
        return  list;
    }

    ResourceLocation getSideModelLocation(TransportState state);

    default ResourceLocation getSideModelLocation(){
        return getSideModelLocation(TransportState.EXTRACT_INSERT);
    }

    default ResourceLocation getUpModelLocation(){
        return null;
    };
    enum Shape{
        COMMON(Block.box(6, 0, 0, 10, 4, 6),Block.box(6, 4, 0, 10, 20, 4)),
        INSULATED(Block.box(5.7, 0, 0, 10.3, 4.6, 6),Block.box(5.7, 4.6, 0, 10.3, 20.6, 4.6)),
        THIN(Block.box(6.4, 0.4, 0, 9.6, 3.6, 6),Block.box(6.4, 3.6, 0, 9.6, 19.6, 3.2)),
        INSULATED_THIN(Block.box(6.1, 0.1, 0, 9.9, 3.9, 6),Block.box(6.1, 3.9, 0, 9.9, 19.9, 3.8)),
        SOLID(Block.box(6, 6, 0, 10, 10, 6)),
        FLUID_PIPE(Block.box(5, 5, 0, 11, 11, 5)),
        LARGE_FLUID_PIPE(Block.box(4, 4, 0, 12, 12, 4));;

        public final VoxelShape[] shapes;

        Shape(VoxelShape... sideShape){
            this.shapes = sideShape;
        }

    }
}
