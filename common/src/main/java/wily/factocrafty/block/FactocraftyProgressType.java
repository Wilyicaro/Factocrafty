package wily.factocrafty.block;


import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;
import wily.factoryapi.base.ProgressType;

public class FactocraftyProgressType extends ProgressType {

    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");

    public static ProgressType ENERGY_PROGRESS = new FactocraftyProgressType(ProgressType.Identifier.DEFAULT,new int[]{24,14,22,7},false,false,  ProgressType.Direction.HORIZONTAL);
    public static ProgressType PROGRESS = new FactocraftyProgressType(ProgressType.Identifier.DEFAULT,new int[]{0,14,24,17},false,false,  ProgressType.Direction.HORIZONTAL);
    public static ProgressType MACHINE_PROGRESS = new FactocraftyProgressType(ProgressType.Identifier.DEFAULT,new int[]{46,15,21,5},false,false,  ProgressType.Direction.HORIZONTAL);
    public static ProgressType BURN_PROGRESS = new FactocraftyProgressType(ProgressType.Identifier.BURN_TIME,new int[]{0,0,14,14},false,false,  ProgressType.Direction.VERTICAL);

    public static ProgressType SOLAR_GENERATING = new FactocraftyProgressType(ProgressType.Identifier.DEFAULT,new int[]{0,102,20,20},false,false,  ProgressType.Direction.HORIZONTAL);

    public static ProgressType BIG_FLUID_TANK = new FactocraftyProgressType(ProgressType.Identifier.TANK,new int[]{24,31,44,52},true, false, ProgressType.Direction.VERTICAL);
    public static ProgressType FLUID_TANK = new FactocraftyProgressType(ProgressType.Identifier.TANK,new int[]{0,31,24,52},true, false,ProgressType.Direction.VERTICAL);
    public static ProgressType MINI_FLUID_TANK = new FactocraftyProgressType(ProgressType.Identifier.TANK,new int[]{0,83,16,19},true,false, ProgressType.Direction.VERTICAL);
    public static ProgressType ENERGY_CELL = new FactocraftyProgressType(ProgressType.Identifier.TANK,new int[]{92,31,18,52},false,false, ProgressType.Direction.VERTICAL);

    public static ProgressType BIG_ENERGY_CELL = new FactocraftyProgressType(ProgressType.Identifier.TANK,new int[]{68,31,24,52},false,false, ProgressType.Direction.VERTICAL);

    public FactocraftyProgressType(Identifier identifier, int[] uvSize, boolean hasFluid, boolean reverse, Direction plane) {
        super(identifier, WIDGETS, uvSize, hasFluid, reverse, plane);
    }
}