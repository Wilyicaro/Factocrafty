package wily.factocrafty.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyLedBlockEntity;
import wily.factocrafty.inventory.FactocraftyItemMenuContainer;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.IWindowWidget;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableSlider;
import wily.factoryapi.util.ScreenUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;

public class RGBControllerScreen extends AbstractContainerScreen<FactocraftyItemMenuContainer> implements IWindowWidget {

    private final FactocraftyLedBlockEntity be;

    protected final List<Renderable> renderables = new ArrayList<>();
    protected double[] lastRGBMousePos = new double[2];

    private DrawableStatic RGB_PICKER;

    private int viewedColor = -1;

    public RGBControllerScreen(FactocraftyItemMenuContainer abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        be = (FactocraftyLedBlockEntity) menu.player.level().getBlockEntity(menu.blockPos);
        imageWidth = 152;
        imageHeight = 160;
    }
    private double getDistanceFrom(double mouseX, double mouseY, double pX, double pY){
        return Math.sqrt(Math.pow(mouseX - pX,2) + Math.pow(mouseY - pY,2));
    }

    @Override
    protected void init() {
        super.init();
        renderables.clear();
        addNestedRenderable(new FactoryDrawableSlider(leftPos + 12, topPos + 82, i->Component.literal("Red: " + i.value), FactocraftyDrawables.MEDIUM_BUTTON,  FactocraftyDrawables.MEDIUM_BUTTON_BACKGROUND,5, 128,FastColor.ARGB32.red(be.actualRgb.get()), 255).onPress((s, i)-> sendServerActualColor(()->s.value,()-> null,()-> null)));
        addNestedRenderable(new FactoryDrawableSlider(leftPos + 12, topPos + 96, i->Component.literal("Green: " + i.value), FactocraftyDrawables.MEDIUM_BUTTON,  FactocraftyDrawables.MEDIUM_BUTTON_BACKGROUND,  5, 128,FastColor.ARGB32.green(be.actualRgb.get()), 255).onPress((s, i)-> sendServerActualColor(()-> null,()->s.value,()-> null)));
        addNestedRenderable(new FactoryDrawableSlider(leftPos + 12, topPos + 110, i->Component.literal("Blue: " + i.value), FactocraftyDrawables.MEDIUM_BUTTON,  FactocraftyDrawables.MEDIUM_BUTTON_BACKGROUND,  5, 128,FastColor.ARGB32.blue(be.actualRgb.get()), 255).onPress((s, i)-> sendServerActualColor(()-> null,()-> null,()->s.value)));
        RGB_PICKER = FactocraftyDrawables.RGB_PICKER.createStatic(leftPos + 46, topPos + 17);
    }
    protected void sendServerActualColor(ArbitrarySupplier<Integer> red, ArbitrarySupplier<Integer> green, ArbitrarySupplier<Integer> blue ){
        Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(menu.blockPos,FastColor.ARGB32.color(255,red.or(FastColor.ARGB32.red(be.actualRgb.get())) ,green.or(FastColor.ARGB32.green(be.actualRgb.get())),blue.or(FastColor.ARGB32.blue(be.actualRgb.get()))), be.additionalSyncInt.indexOf(be.actualRgb)));
    }


    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int i, int j) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        //guiGraphics.drawString(font, Component.literal("Color: " + lastRGBMousePos[0] + "-" + lastRGBMousePos[1] ), 112 , 14, be.actualRgb.get());
    }



    protected float getColorFromDistance(double d){
        return d <= 30 ? 1F : (float) (Math.max(0, 1 - ((d - 30) / 30)));
    }
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (IWindowWidget.super.mouseClicked(d,e,i)) return true;
        if (RGB_PICKER.inMouseLimit((int) d, (int) e)) {
            lastRGBMousePos[0] = d;
            lastRGBMousePos[1] = e;
    //        double dr = getDistanceFrom(d,e,leftPos + 46 + 60,topPos + 15);
    //        double dg = getDistanceFrom(d,e, leftPos + 46 + 30.5,topPos + 15 + 60);
    //        double db = getDistanceFrom(d,e, leftPos + 46,topPos + 15);
    //        Color color = new Color(getColorFromDistance(dr),getColorFromDistance(dg),getColorFromDistance(db));
    //        Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(menu.blockPos,color.getRGB(), be.additionalSyncInt.indexOf(be.actualRgb)));
            Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(menu.blockPos,viewedColor, be.additionalSyncInt.indexOf(be.actualRgb)));
            return true;
        }
        if (IWindowWidget.super.mouseClicked(d,e,i)) return true;
        return super.mouseClicked(d, e, i);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        renderBackground(graphics);
        super.render(graphics, i, j, f);
        ScreenUtil.drawGUISlot(graphics, leftPos + 110,topPos + 16, 18,36);
        ScreenUtil.drawGUISlot(graphics, leftPos + 129,topPos + 16, 18,36);
        if (RGB_PICKER.inMouseLimit(i,j))
            graphics.fill( leftPos + 111,topPos + 17, leftPos + 111 + 16,topPos + 17+ 34, viewedColor = getPixelColor(FactocraftyDrawables.WIDGETS,i - RGB_PICKER.getX(),j -RGB_PICKER.getY() + 131));
        graphics.fill( leftPos + 130,topPos + 17, leftPos + 130 + 16,topPos + 17+ 34, be.actualRgb.get());
        IWindowWidget.super.render(graphics,i,j,f);
        renderTooltip(graphics,i,j);

    }
    public int getPixelColor(ResourceLocation location,int x, int y) {
        if (minecraft.getResourceManager().getResource(location).isPresent())
            try (InputStream stream = minecraft.getResourceManager().getResource(location).get().open()) {
                BufferedImage bufferedImage = ImageIO.read(stream);
                return bufferedImage.getRGB(x, y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return 0xFFFFFF;
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float f, int i, int j) {
        ScreenUtil.drawGUIBackground(graphics,leftPos, topPos, imageWidth, imageHeight);
        ScreenUtil.drawGUISubSlot(graphics, RGB_PICKER.getX() - 2, RGB_PICKER.getY() - 2, 64, 64);
        RGB_PICKER.draw(graphics);
        FactocraftyDrawables.ENERGY_CELL_SLOT.draw(graphics, leftPos + 6, topPos + 16);
        FactocraftyDrawables.ENERGY_CELL.drawProgress(graphics, leftPos + 7, topPos + 17, be.energyStorage.getEnergyStored(), be.energyStorage.getMaxEnergyStored());
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (IWindowWidget.super.mouseReleased(d,e,i)) return true;
        return super.mouseReleased(d, e, i);
    }
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (IWindowWidget.super.mouseDragged(d,e,i,f,g)) return true;
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int i, int j) {
        super.renderTooltip(guiGraphics, i, j);
        if (FactocraftyDrawables.ENERGY_CELL.inMouseLimit(i,j,leftPos + 7, topPos + 17)) guiGraphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", be.energyStorage),i,j);
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(leftPos,topPos,imageWidth,imageHeight);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public <R extends Renderable> R addNestedRenderable(R drawable) {
        renderables.add(drawable);
        return drawable;
    }

    @Override
    public List<? extends Renderable> getNestedRenderables() {
        return renderables;
    }
}
