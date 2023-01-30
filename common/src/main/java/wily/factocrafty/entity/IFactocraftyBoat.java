package wily.factocrafty.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import wily.factocrafty.init.Registration;

public interface IFactocraftyBoat {
    enum Type{
        RUBBER(Registration.RUBBER_PLANKS.get(),"rubber");
        //CARBON(Blocks.COAL_BLOCK,"carbon");
        private final String name;
        private final Block materials;

        private Type(Block block, String string2) {
            this.name = string2;
            this.materials = block;
        }

        public String getName() {
            return this.name;
        }

        public Block getMaterials() {
            return this.materials;
        }

        public String toString() {
            return this.name;
        }

        public static Type byId(int i) {
            Type[] types = values();
            if (i < 0 || i >= types.length) {
                i = 0;
            }

            return types[i];
        }

        public static Type byName(String string) {
            Type[] types = values();

            for(int i = 0; i < types.length; ++i) {
                if (types[i].getName().equals(string)) {
                    return types[i];
                }
            }

            return types[0];
        }
    }
    IFactocraftyBoat.Type getFactocraftyBoatType();

    void setType(IFactocraftyBoat.Type  type);
}
