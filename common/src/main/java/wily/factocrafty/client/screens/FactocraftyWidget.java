package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class FactocraftyWidget extends AbstractWidget implements IWindowWidget {

     public FactocraftyWidget(int i, int j, int k, int l, Component component) {
          super(i, j, k, l, component);
     }

     @Override
     public void renderWidget(PoseStack poseStack, int i, int j, float f) {

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
