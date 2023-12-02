package wily.factocrafty.client.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.IWindowWidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FactocraftyWidget extends AbstractWidget implements IWindowWidget {
     protected final Font font = Minecraft.getInstance().font;
     protected Supplier<Boolean> visibility = ()-> visible;

     protected final List<Renderable> nestedRenderables = new ArrayList<>();

     public FactocraftyWidget(int i, int j, int k, int l, Component component) {
          super(i, j, k, l, component);
          active = false;
     }

     @Override
     public void renderWidget(GuiGraphics graphics, int i, int j, float f) {

     }

     @Override
     public void render(GuiGraphics guiGraphics, int i, int j, float f) {
          visible = visibility.get();
          super.render(guiGraphics, i, j, f);
          IWindowWidget.super.render(guiGraphics,i,j,f);
     }

     public Rect2i getBounds() {
          return new Rect2i(getX(),getY(),width,height);
     }

     public boolean isVisible(){
          return visibility.get();
     }


     @Override
     public List<? extends Renderable> getNestedRenderables() {
          return nestedRenderables;
     }

     @Override
     public <R extends Renderable> R addNestedRenderable(R drawable) {
          nestedRenderables.add(drawable);
          return drawable;
     }

     @Override
     protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

     }

     @Override
     protected boolean clicked(double d, double e) {
          return isMouseOver(d,e);
     }

     @Override
     public boolean isMouseOver(double d, double e) {
          return getBounds().contains((int) d, (int) e);
     }

     @Override
     public boolean mouseClicked(double d, double e, int i) {
          if (!this.isVisible())
               return false;
          if (IWindowWidget.super.mouseClicked(d,e,i)) return true;
          if (this.isValidClickButton(i) && clicked(d,e)) {
               this.playDownSound(Minecraft.getInstance().getSoundManager());
               this.onClick(d, e);
               return true;
          }
          return false;
     }
}
