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

    float opacity = 1.0F;

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

    public Map<EasyButton, Boolean> configButtons() {
        Map<EasyButton, Boolean> map = new HashMap<>();
        addButtons(map);
        return map;
    }
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(ButtonTypes.SMALL,getX() + width - 18,getY() + 10,new EasyIcon(7,7,0,239), (b)-> onClose(), Component.translatable("tooltip.factocrafty.config.close")), false);
        return map;
    }
    public EasyButton byButtonType(ButtonTypes type, int x, int y, EasyIcon iconId, Consumer<Integer> onPress, Component message){
        return new EasyButton(x,y,type.width, type.height,type.uvX, type.uvY, iconId,null,onPress, message);
    }
    public EasyButton byButtonColor(ButtonTypes type, int x, int y,  Color color, Consumer<Integer> onPress, Component message){
        return new EasyButton(x,y,type.width, type.height,type.uvX, type.uvY, null,color,onPress, message);
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
        opacity = 1.0F;
        parent.setFocused(this);
    }
    public void onClickOutside(double mouseX, double mouseY){
        opacity = 0.88F;
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
        RenderSystem.setShaderColor(1,1,1,opacity);
        blit(poseStack, getX(), getY(), uvX, uvY, width, height);
        for (Map.Entry<EasyButton,Boolean> entry: configButtons().entrySet()) {
            EasyButton button = entry.getKey();
            entry.setValue(button.clicked(i,j));
            if (button.color!= null) {
                Color c = button.color;
                if (entry.getValue()) c = c.brighter();
                RenderSystem.setShaderColor((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue()/ 255, opacity);
            }
            blit(poseStack, button.x, button.y, (entry.getValue() && button.color == null ? entry.getKey().width : 0), button.uvY, button.width, button.height);
            RenderSystem.setShaderColor(1,1,1,opacity);
            if (entry.getKey().icon != null) blit(poseStack, button.x +(button.width - button.icon.width) /2, button.y +(button.height - button.icon.height) / 2, button.icon.uvX(), button.icon.uvY,button.icon.width, button.icon.height);
        }
        renderBg(poseStack,i,j);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
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
        for (Map.Entry<EasyButton,Boolean> entry: configButtons().entrySet()) {
            if (entry.getKey().clicked(i,j) && !entry.getKey().message.getString().isEmpty()) parent.renderTooltip(poseStack,entry.getKey().message(), i,j);
        }
    }
    @Override
    public boolean isMouseOver(double d, double e) {
        return isVisible() && FactocraftyProgressType.getMouseLimit(d,e,getX(),getY(),width,height);
    }

    public void playDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
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
        for (EasyButton button: configButtons().keySet()) {
            if (button.clicked(d, e)) {
                playDownSound(1.5F);
                button.onPress.accept(i);
                return true;
            }
        }

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
    enum ButtonTypes{
        LARGE(0, 199, 16, 16),MEDIUM(0, 215,13,  13),SMALL(0,228, 11,  11);
        public final int uvX;
        public final int uvY;
        public final int width;
        public final int height;
        ButtonTypes(int uvX,int uvY, int width, int height){
            this.uvX = uvX;
            this.uvY = uvY;
            this.width = width;
            this.height = height;
        }
    }
    public record EasyIcon(int width, int height,int iconId, int uvY){
        public int uvX() {
            return iconId * width;
        }
    }
    public record EasyButton(int x, int y, int width, int height, int uvX, int uvY, EasyIcon icon, Color color, Consumer<Integer> onPress, Component message){
        public boolean clicked(double mouseX, double mouseY){
            return FactocraftyProgressType.getMouseLimit(mouseX,mouseY, x,y, width,height);
        }
        EasyButton(int x, int y, int width, int height, int uvX, int uvY, EasyIcon icon, Color color, Consumer<Integer> onPress){
            this(x,y,width,height,uvX,uvY,icon,color,onPress, Component.empty());
        }
    }


}
