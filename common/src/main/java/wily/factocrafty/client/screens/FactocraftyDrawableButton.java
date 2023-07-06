package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.IFactoryDrawableType;

import java.awt.Color;
import java.util.function.Consumer;

public class FactocraftyDrawableButton extends DrawableCustomWidth{
    public boolean selected = false;
    protected Consumer<Integer> onPress;
    public final Component tooltip;
    public IFactoryDrawableType icon;

    public Color color;

    public FactocraftyDrawableButton(int x, int y, Consumer<Integer> onPress, Component tooltip, IFactoryDrawableType buttonImage){
        super(buttonImage,x,y);
        this.onPress = onPress;
        this.tooltip = tooltip;
    }
    public FactocraftyDrawableButton color(Color color){
        this.color = color;
        return this;
    }
    public FactocraftyDrawableButton icon(IFactoryDrawableType icon){
        this.icon = icon;
        return this;
    }
    public FactocraftyDrawableButton(int x, int y, Consumer<Integer> onPress, IFactoryDrawableType buttonImage){
        this(x,y, onPress,Component.empty(),buttonImage);
    }
    public FactocraftyDrawableButton withWidth(Integer width){
        this.customWidth = width != null ? Math.max(4,width) : null;
        return this;
    }


    public boolean inMouseLimit(double mouseX, double mouseY) {
        return IFactoryDrawableType.getMouseLimit(mouseX,mouseY,posX,posY,width(),height());
    }

    @Override
    public void draw(GuiGraphics graphics) {
        this.draw(graphics,posX,posY);
    }
    public void onClick(double mouseX, double mouseY, int i){
        onPress.accept(i);
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        if (color != null)
            graphics.setColor(color.getRed() / 255F,color.getGreen() / 255F, color.getBlue() / 255F, 1F);
        super.draw(graphics, x, y);
        RenderSystem.setShaderColor(1, 1, 1, RenderSystem.getShaderColor()[3]);
        if (selected) graphics.renderOutline(x,y, width(), height(), -1);
        if (icon != null)
            icon.draw(graphics,x + (width() - icon.width()) / 2, y + (height() - icon.height()) / 2);
    }
}
