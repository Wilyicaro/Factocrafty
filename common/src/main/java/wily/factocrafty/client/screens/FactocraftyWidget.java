package wily.factocrafty.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.IWindowWidget;

public class FactocraftyWidget extends AbstractWidget implements IWindowWidget {
     protected final Font font = Minecraft.getInstance().font;

     public FactocraftyWidget(int i, int j, int k, int l, Component component) {
          super(i, j, k, l, component);
     }

     @Override
     public void renderWidget(GuiGraphics graphics, int i, int j, float f) {

     }

     public Rect2i getBounds() {
          return new Rect2i(getX(),getY(),width,height);
     }

     public boolean isVisible(){
          return false;
     }

     @Override
     protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

     }
}
