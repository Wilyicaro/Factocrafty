package wily.factocrafty.client.screens.widgets.windows;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.FactocraftyWidget;
import wily.factocrafty.client.screens.IWindowWidget;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class FactocraftyScreenWindow extends FactocraftyWidget {
    public final int uvX;
    public final int uvY;
    public final FactocraftyConfigWidget config;
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID, "textures/gui/container/config_widgets.png");

    private double actualMouseX;

    private double actualMouseY;

    public boolean dragging = false;



    public FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> parent;
    public FactocraftyScreenWindow(FactocraftyConfigWidget config, int width, int height, int x, int y, int uvX, int uvY, FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> parent){
        super(x,y,width,height,Component.empty());
        this.config = config;
        this.lastX = x;
        this.lastY = y;
        this.uvX = uvX;
        this.uvY = uvY;
        this.parent = parent;

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
    protected void renderBg(PoseStack poseStack, int i, int j) {

    }
    protected void renderBackground(PoseStack poseStack, int i, int j){
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1,1,1,alpha);
        blit(poseStack, getX(), getY(), uvX, uvY, width, height);
        renderButtons(poseStack,i,j);
        renderBg(poseStack,i,j);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(FactocraftyScreenWindow.ButtonTypes.SMALL,getX() + width - 18,getY() + 10,new FactocraftyScreenWindow.EasyIcon(7,7,0,239), (b)-> onClose(), Component.translatable("tooltip.factocrafty.config.close")), false);
        return super.addButtons(map);
    }

    public void render(PoseStack poseStack, int i, int j, float f) {
        if (!isVisible()) return;
        if (parent.getFocused() == config) parent.setFocused(this);
        poseStack.pushPose();
        poseStack.translate(0D,0D, parent.getBlitOffset() + 450F);
        renderBackground(poseStack,i,j);
        renderToolTip(poseStack,i,j);
        poseStack.popPose();

    }
    public void renderToolTip(PoseStack poseStack, int i, int j) {
        renderButtonsTooltip(parent,poseStack,i,j);
    }
    @Override
    public boolean isMouseOver(double d, double e) {
        return isVisible() && FactocraftyProgressType.getMouseLimit(d,e,getX(),getY(),width,height);
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
                updateActualMouse(d, e);
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
        }
        return false;
    }
    int lastX;
    int lastY;
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        changeFocus(true);
        if (!isVisible() || (parent.isDragging() && !dragging)) return false;
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
