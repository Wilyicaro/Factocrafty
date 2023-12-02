package wily.factocrafty.block.entity;

public interface ITicker {
    default void tick(boolean tickClient){
        if (tickClient) clientTick();
        else serverTick();
        tick();
    }
    default void tick(){

    }
    default void clientTick(){
    }
    default void serverTick(){
    }
}
