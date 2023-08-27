package wily.factocrafty.client.screens;


import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factoryapi.base.client.IFactoryDrawableType;
import wily.factoryapi.base.Progress;

public class FactocraftyDrawables {

    public static final ResourceLocation MINECRAFT_WIDGETS = new ResourceLocation( "textures/gui/widgets.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");

    public static final ResourceLocation CONFIG_WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/config_widgets.png");

    public static IFactoryDrawableType.DrawableImage RGB_PICKER = IFactoryDrawableType.create(WIDGETS,0,131,60,60);

    public static IFactoryDrawableType ENERGY_CELL_SLOT = IFactoryDrawableType.create(WIDGETS,178,0,20,54);

    public static IFactoryDrawableType BIG_ENERGY_CELL_SLOT = IFactoryDrawableType.create(WIDGETS,198,0,26,54);

    public static IFactoryDrawableType getSmallButtonIcon(int id){
        return IFactoryDrawableType.create(CONFIG_WIDGETS,id*7,239 ,7,7);
    }
    public static IFactoryDrawableType getButtonIcon(int id){
        return IFactoryDrawableType.create(CONFIG_WIDGETS,id*10,246,10,10);
    }
    public static IFactoryDrawableType getInfoIcon(int id){
        return IFactoryDrawableType.create(FactocraftyConfigWidget.WIDGETS_LOCATION, id * 14, 242, 14,14);
    }

    public static IFactoryDrawableType.DrawableImage LARGE_BUTTON = IFactoryDrawableType.create(CONFIG_WIDGETS,0, 199, 16, 16);
    public static IFactoryDrawableType.DrawableImage MEDIUM_BUTTON = IFactoryDrawableType.create(CONFIG_WIDGETS,0, 215,13,  13);
    public static IFactoryDrawableType.DrawableImage SMALL_BUTTON = IFactoryDrawableType.create(CONFIG_WIDGETS,0,228, 11,  11);

    public static IFactoryDrawableType.DrawableImage VANILLA_BUTTON_BACKGROUND = IFactoryDrawableType.create(MINECRAFT_WIDGETS,0, 46, 200, 20);
    public static IFactoryDrawableType.DrawableImage VANILLA_BUTTON = IFactoryDrawableType.create(MINECRAFT_WIDGETS,0, 66, 200, 20);

    public static IFactoryDrawableType.DrawableImage BIG_FLUID_TANK = IFactoryDrawableType.create(WIDGETS,24,31,44,52);
    public static IFactoryDrawableType.DrawableImage FLUID_TANK = IFactoryDrawableType.create(WIDGETS,0,31,24,52);
    public static IFactoryDrawableType.DrawableImage MINI_FLUID_TANK = IFactoryDrawableType.create(WIDGETS,0,83,16,19);


    public static IFactoryDrawableType.DrawableProgress ENERGY_PROGRESS =createProgress(Progress.Identifier.DEFAULT,new int[]{24,14,22,7},false,  IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress TRANSFORMER_PROGRESS =createProgress(Progress.Identifier.DEFAULT,new int[]{73,14,22,7},false,  IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress PROGRESS = createProgress(Progress.Identifier.DEFAULT,new int[]{0,14,24,17},false, IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress MACHINE_PROGRESS = createProgress(Progress.Identifier.DEFAULT,new int[]{46,15,21,5},false, IFactoryDrawableType.Direction.HORIZONTAL);
    public static IFactoryDrawableType.DrawableProgress BURN_PROGRESS = createProgress(Progress.Identifier.BURN_TIME,new int[]{0,0,14,14},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress MATTER_PROGRESS = createProgress(Progress.Identifier.MATTER,new int[]{0,128,135,3},false, IFactoryDrawableType.Direction.HORIZONTAL);

    public static IFactoryDrawableType.DrawableProgress ENERGY_CELL = createProgress(Progress.Identifier.TANK,new int[]{92,31,18,52},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress BIG_ENERGY_CELL = createProgress(Progress.Identifier.TANK,new int[]{68,31,24,52},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress PLATFORM_ENERGY_CELL = createProgress(Progress.Identifier.TANK,new int[]{110,31,18,52},false, IFactoryDrawableType.Direction.VERTICAL);

    public static IFactoryDrawableType.DrawableProgress createProgress(Progress.Identifier identifier, int[] uvSize, boolean reverse, IFactoryDrawableType.Direction plane) {
        return IFactoryDrawableType.create(WIDGETS,uvSize[0],uvSize[1],uvSize[2],uvSize[3]).asProgress(identifier, reverse, plane);
    }

}