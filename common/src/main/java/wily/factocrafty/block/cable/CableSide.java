package wily.factocrafty.block.cable;

import net.minecraft.util.StringRepresentable;

public enum CableSide implements StringRepresentable {
    UP("up"),
    SIDE("side"),
    DOWN("down"),
    NONE("none");

    private final String name;

    private CableSide(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.getSerializedName();
    }


    public CableSide opposite(){
        switch (this){
            case UP -> { return DOWN;}
            case DOWN -> {return UP;}
            case NONE -> {return SIDE;}
            default -> {return NONE;}
        }

    }
    public String getSerializedName() {
        return this.name;
    }

    public boolean isConnected() {
        return this != NONE;
    }
}
