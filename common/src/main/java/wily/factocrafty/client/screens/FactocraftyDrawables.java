package wily.factocrafty.client.screens;


import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;
import wily.factoryapi.base.IFactoryDrawableType;
import wily.factoryapi.base.Progress;

public class FactocraftyDrawables {

    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");

    public static IFactoryDrawableType SLOT = IFactoryDrawableType.create(WIDGETS,178,0,18,18);
    public static IFactoryDrawableType BIG_SLOT = IFactoryDrawableType.create(WIDGETS,196,0,26,26);
    public static IFactoryDrawableType SLOT_OUTLINE = IFactoryDrawableType.create(WIDGETS,178,18,18,18);
    public static IFactoryDrawableType BIG_SLOT_OUTLINE = IFactoryDrawableType.create(WIDGETS,196,26,26,26);

    public static IFactoryDrawableType.DrawableProgress ENERGY_PROGRESS =createProgress(Progress.Identifier.DEFAULT,new int[]{24,14,22,7},false,  IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress PROGRESS = createProgress(Progress.Identifier.DEFAULT,new int[]{0,14,24,17},false, IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress MACHINE_PROGRESS = createProgress(Progress.Identifier.DEFAULT,new int[]{46,15,21,5},false, IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress BURN_PROGRESS = createProgress(Progress.Identifier.BURN_TIME,new int[]{0,0,14,14},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress MATTER_PROGRESS = createProgress(Progress.Identifier.MATTER,new int[]{0,128,135,3},false, IFactoryDrawableType.Direction.HORIZONTAL);

    public static IFactoryDrawableType.DrawableProgress BIG_FLUID_TANK = createProgress(Progress.Identifier.TANK,new int[]{24,31,44,52},true, IFactoryDrawableType.Direction.VERTICAL);
    public static IFactoryDrawableType.DrawableProgress FLUID_TANK = createProgress(Progress.Identifier.TANK,new int[]{0,31,24,52},true, IFactoryDrawableType.Direction.VERTICAL);
    public static IFactoryDrawableType.DrawableProgress MINI_FLUID_TANK = createProgress(Progress.Identifier.TANK,new int[]{0,83,16,19},true,IFactoryDrawableType.Direction.VERTICAL);
    public static IFactoryDrawableType.DrawableProgress ENERGY_CELL = createProgress(Progress.Identifier.TANK,new int[]{92,31,18,52},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress BIG_ENERGY_CELL = createProgress(Progress.Identifier.TANK,new int[]{68,31,24,52},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress createProgress(Progress.Identifier identifier, int[] uvSize, boolean reverse, IFactoryDrawableType.Direction plane) {
        return IFactoryDrawableType.create(WIDGETS,uvSize[0],uvSize[1],uvSize[2],uvSize[3]).asProgress(identifier, reverse, plane);
    }
}