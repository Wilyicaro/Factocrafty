package wily.factocrafty.block.entity;

import net.minecraft.nbt.CompoundTag;
import wily.factoryapi.base.ITagSerializable;
import wily.factoryapi.base.ProgressType;

public class Progress implements ITagSerializable<CompoundTag> {
    private int[] progress;

    public int maxProgress;
    public ProgressType progressType;

    public Progress(ProgressType progressType, int progressSize, int maxProgress){
        this.progressType = progressType;
        this.maxProgress = maxProgress;
        progress = new int[progressSize];
    }
    public void setInt(int ordinal, int value){
        progress[ordinal] = value;
    }
    public int getInt(int ordinal){
        return progress[ordinal];
    }

    public void set(int[] value){
        progress = value;
    }
    public int[] get(){
        return progress;
    }

    public String getMaxName(){
        return "actual" + (progressType.name.substring(0,1).toUpperCase() + progressType.name.substring(1));
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag compoundTag = new CompoundTag();
        if(progress.length == 1){
            compoundTag.putInt(progressType.name, getInt(0));
        }else if (progress.length > 1) compoundTag.putIntArray(progressType.name, get());
        compoundTag.putInt(getMaxName(), maxProgress);
        return compoundTag;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {

        if(progress.length == 1){
            setInt(0, tag.getInt(progressType.name));
        }else if (progress.length > 1) set(tag.getIntArray(progressType.name));
        maxProgress = tag.getInt(getMaxName());
    }
}
