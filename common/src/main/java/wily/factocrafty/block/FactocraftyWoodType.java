package wily.factocrafty.block;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.properties.WoodType;

public class FactocraftyWoodType {



        public static void addWoodType(WoodType woodType){
                Sheets.SIGN_MATERIALS.put(woodType, Sheets.createSignMaterial(woodType));
        }

        public static final WoodType  RUBBER = new WoodType("rubber");


}
