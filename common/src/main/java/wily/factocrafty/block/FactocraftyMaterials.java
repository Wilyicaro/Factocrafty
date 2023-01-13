package wily.factocrafty.block;

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class FactocraftyMaterials {
    public static final Material PETROLEUM =  (new Material.Builder(MaterialColor.COLOR_BLACK)).noCollider().nonSolid().replaceable().liquid().build();

    public static final Material GASOLINE =  (new Material.Builder(MaterialColor.COLOR_YELLOW)).noCollider().nonSolid().replaceable().liquid().build();

    public static final Material LATEX =  (new Material.Builder(MaterialColor.SNOW)).noCollider().nonSolid().replaceable().liquid().build();

}
