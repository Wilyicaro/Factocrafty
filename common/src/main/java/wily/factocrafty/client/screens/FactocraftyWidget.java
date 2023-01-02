package wily.factocrafty.client.screens;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class FactocraftyWidget extends AbstractWidget implements IWidget {

     public FactocraftyWidget(int i, int j, int k, int l, Component component) {
          super(i, j, k, l, component);
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
