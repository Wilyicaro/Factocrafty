package wily.factocrafty.block.transport;

import net.minecraft.util.StringRepresentable;

public enum ConduitSide implements StringRepresentable {
    UP("up"),
    SIDE("side"),
    DOWN("down"),
    NONE("none");

    private final String name;

    private ConduitSide(String string2) {
        this.name = string2;
    }

    public String toString() {
        return this.getSerializedName();
    }


    public ConduitSide opposite(){
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
