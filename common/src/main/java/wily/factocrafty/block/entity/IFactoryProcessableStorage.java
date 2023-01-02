package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factoryapi.base.IFactoryStorage;

import java.util.ArrayList;
import java.util.List;

public interface IFactoryProcessableStorage extends IFactoryStorage {
    default boolean hasProgress(){
        return !getProgresses().isEmpty();
    }


    default List<Progress> getProgresses(){
        List<Progress> list = new ArrayList<>(List.of());
        addProgresses(list);
        return list;
    }
    default Progress getProgressByType(FactocraftyProgressType.Identifier identifier){
        for (Progress p : getProgresses()) {
            if (p.progressType.identifier.ordinal() == identifier.ordinal()) return p;
        }
        return null;
    }

    void addProgresses(List<Progress> list);

    @Override
    default void load(CompoundTag compoundTag) {
        IFactoryStorage.super.load(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> p.deserializeTag(compoundTag));}
    }

    @Override
    default void saveAdditional(CompoundTag compoundTag) {
        IFactoryStorage.super.saveAdditional(compoundTag);
        if(hasProgress()) {getProgresses().forEach(p -> compoundTag.merge(p.serializeTag()));}
    }
}
