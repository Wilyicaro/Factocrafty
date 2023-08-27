package wily.factocrafty.client.screens.widgets.windows;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.client.screens.FactocraftyWidget;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.client.FactoryDrawableButton;
import wily.factoryapi.base.client.IFactoryDrawableType;

import java.util.List;

public abstract class FactocraftyScreenWindow extends FactocraftyWidget {
    public final int uvX;
    public final int uvY;
    public final FactocraftyConfigWidget config;
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID, "textures/gui/container/config_widgets.png");

    private double actualMouseX;

    private double actualMouseY;

    public boolean dragging = false;

    public boolean useGeneratedBackground;


    protected final ItemRenderer itemRenderer;

    public FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent;
    public FactocraftyScreenWindow(FactocraftyConfigWidget config, int width, int height, int x, int y, int uvX, int uvY, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent){
        super(x,y,width,height,Component.empty());
        this.config = config;
        this.lastX = x;
        this.lastY = y;
        this.uvX = uvX;
        this.uvY = uvY;
        this.parent = parent;
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }



    @Override
    public boolean isVisible() {
        return config.Pressed;
    }

    public void onClose(){
        this.config.Pressed = false;
        for (GuiEventListener listener : parent.children()) {
            if (listener instanceof FactocraftyScreenWindow window && window.isVisible()) window.onClickWidget();
        }
    }
    public void onClickWidget(){
        alpha = 1.0F;
        parent.setFocused(this);
    }
    public void onClickOutside(double mouseX, double mouseY){
        setFocused(false);
        alpha = 0.88F;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256 && isVisible()) {
            onClose();
            return true;
        }

        return false;
    }

    protected void renderBg(GuiGraphics graphics, int i, int j) {
        graphics.pose().pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1,1,1,alpha);
        if (useGeneratedBackground) ScreenUtil.drawGUIBackground(graphics, getX(), getY(), width, height);
            else graphics.blit(WIDGETS_LOCATION, getX(), getY(), uvX, uvY, width, height);
        renderButtons(graphics,i,j);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        graphics.pose().popPose();
    }

    @Override
    public List<FactoryDrawableButton> addButtons(List<FactoryDrawableButton> list) {
        list.add(new FactoryDrawableButton(getX() + width - 18,getY() + 10, (b)-> onClose(), Component.translatable("tooltip.factocrafty.config.close"), FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(0)));
        return super.addButtons(list);
    }

    public void render(GuiGraphics graphics, int i, int j, float f) {
        if (!isVisible()) return;
        if (parent.getFocused() == config) parent.setFocused(this);
        graphics.pose().pushPose();
        graphics.pose().translate(0D,0D,  getBlitOffset());
        renderBg(graphics,i,j);
        renderWidget(graphics,i,j,f);
        renderToolTip(graphics,i,j);
        graphics.pose().popPose();

    }
    public float getBlitOffset(){
        return 450F;
    }

    public void renderToolTip(GuiGraphics graphics, int i, int j) {
        renderButtonsTooltip(font,graphics,i,j);
    }
    @Override
    public boolean isMouseOver(double d, double e) {
        return isVisible() && IFactoryDrawableType.getMouseLimit(d,e,getX(),getY(),width,height);
    }

    public void updateActualMouse(double mouseX, double mouseY){
            actualMouseX = mouseX;
            actualMouseY = mouseY;
    }
    public void updateLastMouse(int mouseX, int mouseY){
        lastX = mouseX;
        lastY = mouseY;
    }



    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (!isVisible()) return false;
        if (i == 0) {
            if (isMouseOver(d,e) || config.isMouseOver(d,e)) {
                onClickWidget();
                if (isMouseOver(d,e))updateActualMouse(d, e);
            }else  onClickOutside(d,e);
        }
        mouseClickedButtons(d,e,i);

        return false;
    }



    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (dragging) {
            parent.setDragging(dragging = false);;
            updateLastMouse(getX(),getY());
            return true;
        }
        return false;
    }
    int lastX;
    int lastY;
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (!isVisible() || ((parent.isDragging() && !dragging))) return false;
        if (i == 0 && ((isMouseOver(d,e) || dragging))) {
            double newX =  (lastX + d - actualMouseX);
            double newY = (lastY + e - actualMouseY);
            if (newX + width < parent.width && newX > 0 )
                setX((int) newX);
            if (newY + height < parent.height && newY > 0)
                setY((int)newY);
            parent.setDragging(dragging = true);
            return true;
        }
        return false;
    }



}
