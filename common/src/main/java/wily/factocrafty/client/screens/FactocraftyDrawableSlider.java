package wily.factocrafty.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import wily.factocrafty.Factocrafty;
import wily.factoryapi.base.IFactoryDrawableType;


import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class FactocraftyDrawableSlider extends FactocraftyDrawableButton{
    private final DrawableCustomWidth sliderBackground;
    public int value;
    public final int maxValue;

    public int relativePosX = 0;

    public boolean dragging = false;

    protected Function<Integer,Component> customText;

    public FactocraftyDrawableSlider(int x, int y, BiConsumer<FactocraftyDrawableSlider, Integer> onChange, Component tooltip,Function<Integer,Component> customText, IFactoryDrawableType buttonImage, IFactoryDrawableType sliderBackground, int buttonWidth,int sliderWidth, int initialValue, int maxValue) {
        super(x, y, i->{}, tooltip, buttonImage);
        onPress = i->onChange.accept(this, i);
        this.customText = customText;
        this.sliderBackground = new DrawableCustomWidth(sliderBackground);
        this.sliderBackground.customWidth = sliderWidth;
        this.value = initialValue;
        this.maxValue = maxValue;
        customWidth = buttonWidth;
    }
    public FactocraftyDrawableSlider(int x, int y, BiConsumer<FactocraftyDrawableSlider, Integer> onChange, Function<Integer,Component> customText, IFactoryDrawableType buttonImage, int buttonWidth,int sliderWidth, int initialValue, int maxValue) {
        this(x, y, onChange, Component.empty(), customText, buttonImage,IFactoryDrawableType.create(buttonImage.texture(),buttonImage.uvX() + buttonImage.width(), buttonImage.uvY(), sliderWidth, buttonImage.height()),buttonWidth,sliderWidth, initialValue,maxValue);
    }

    @Override
    public FactocraftyDrawableSlider icon(IFactoryDrawableType icon) {
        return (FactocraftyDrawableSlider) super.icon(icon);
    }

    @Override
    public FactocraftyDrawableSlider color(Color color) {
        return (FactocraftyDrawableSlider) super.color(color);
    }

    public int getValue() {
        return value;
    }
    public float getPercentage(){
        return (float)value / maxValue;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int i) {
        super.onClick(mouseX, mouseY, i);
        if (!inButtonLimit(mouseX,mouseY)) value = getActualValue(mouseX);
    }
    protected int getActualValue(double mouseX){
        return Math.round(Math.max(0,Math.min(((float)mouseX - posX) / sliderBackground.width() * maxValue, maxValue)));
    }
    public void onRelease(double mouseX, double mouseY, int i) {
        dragging = false;
    }

    public boolean mouseDragging(double mouseX, double mouseY, int i) {
        if (inButtonLimit(mouseX,mouseY) || dragging) {
            super.onClick(mouseX, mouseY, i);
            selected = dragging = true;
            value = getActualValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean inMouseLimit(double mouseX, double mouseY) {
        return sliderBackground.inMouseLimit((int) mouseX, (int) mouseY,posX,posY);
    }
    public boolean inButtonLimit(double mouseX, double mouseY){
        return IFactoryDrawableType.getMouseLimit(mouseX,mouseY,relativePosX,posY,width(),height());
    }

    @Override
    public void draw(GuiGraphics graphics) {
        if (dragging) selected = true;
        sliderBackground.draw(graphics,posX,posY);
        Component comp = customText.apply(value);
        Font font = Minecraft.getInstance().font;
        if (comp != null && !comp.getString().isEmpty()) {
            graphics.drawString(font, customText.apply(value),  posX + (sliderBackground.width() - font.width(comp.getString())) / 2, posY + height() / 2 - 4, -1);
        }
        relativePosX = (int) (posX + getPercentage() * (sliderBackground.width() - width()));
        draw(graphics, relativePosX, posY);
    }
}
