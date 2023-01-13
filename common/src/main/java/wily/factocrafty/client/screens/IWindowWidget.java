package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.client.screens.widgets.windows.FactocraftyScreenWindow;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface IWindowWidget {

    default Map<FactocraftyScreenWindow.EasyButton, Boolean> configButtons() {
        Map<FactocraftyScreenWindow.EasyButton, Boolean> map = new HashMap<>();
        addButtons(map);
        return map;
    }
    default Map<FactocraftyScreenWindow.EasyButton, Boolean> addButtons(Map<FactocraftyScreenWindow.EasyButton, Boolean> map) {

        return map;
    }
    default FactocraftyScreenWindow.EasyButton byButtonType(FactocraftyScreenWindow.ButtonTypes type, int x, int y, FactocraftyScreenWindow.EasyIcon iconId, Consumer<Integer> onPress, Component message){
        return new FactocraftyScreenWindow.EasyButton(x,y,type.width, type.height,type.uvX, type.uvY, iconId,null,onPress, message);
    }
    default FactocraftyScreenWindow.EasyButton byButtonColor(FactocraftyScreenWindow.ButtonTypes type, int x, int y, Color color, Consumer<Integer> onPress, Component message){
        return new FactocraftyScreenWindow.EasyButton(x,y,type.width, type.height,type.uvX, type.uvY, null,color,onPress, message);
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
    record EasyIcon(int width, int height,int iconId, int uvY){
        public int uvX() {
            return iconId * width;
        }
    }
    record EasyButton(int x, int y, int width, int height, int uvX, int uvY, EasyIcon icon, Color color, Consumer<Integer> onPress, Component message){
        public boolean clicked(double mouseX, double mouseY){
            return FactocraftyProgressType.getMouseLimit(mouseX,mouseY, x,y, width,height);
        }
        EasyButton(int x, int y, int width, int height, int uvX, int uvY, EasyIcon icon, Color color, Consumer<Integer> onPress){
            this(x,y,width,height,uvX,uvY,icon,color,onPress, Component.empty());
        }
    }
    default void renderButtons(PoseStack poseStack, int i, int j) {
        if (this instanceof GuiComponent gui) {
            RenderSystem.setShaderTexture(0, FactocraftyScreenWindow.WIDGETS_LOCATION);
            for (Map.Entry<EasyButton, Boolean> entry : configButtons().entrySet()) {
                EasyButton button = entry.getKey();
                entry.setValue(button.clicked(i, j));
                if (button.color() != null) {
                    Color c = button.color();
                    if (entry.getValue()) c = c.brighter();
                    RenderSystem.setShaderColor((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, RenderSystem.getShaderColor()[3]);
                }
                gui.blit(poseStack, button.x(), button.y(), (entry.getValue() && button.color() == null ? entry.getKey().width() : 0), button.uvY(), button.width(), button.height());
                RenderSystem.setShaderColor(1, 1, 1, RenderSystem.getShaderColor()[3]);
                if (entry.getKey().icon() != null)
                    gui.blit(poseStack, button.x() + (button.width() - button.icon().width()) / 2, button.y() + (button.height() - button.icon().height()) / 2, button.icon().uvX(), button.icon().uvY(), button.icon().width(), button.icon().height());
            }
        }
    }
    default void renderButtonsTooltip(Screen screen, PoseStack poseStack, int i, int j){
        for (Map.Entry<EasyButton,Boolean> entry: configButtons().entrySet()) {
            if (entry.getKey().clicked(i,j) && !entry.getKey().message().getString().isEmpty()) screen.renderTooltip(poseStack,entry.getKey().message(), i,j);
        }
    }
    default boolean mouseClickedButtons(double d, double e, int i) {
        for (EasyButton button: configButtons().keySet()) {
            if (button.clicked(d, e)) {
                playDownSound(1.5F);
                button.onPress().accept(i);
                return true;
            }
        }
        return  false;
    }
    default void playDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
    }
    Rect2i getBounds();

    boolean isVisible();
}
