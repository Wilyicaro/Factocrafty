package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factoryapi.base.client.FactoryScreenWindow;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

import java.util.ArrayList;
import java.util.List;

public class FactocraftyScreenWindow<T extends AbstractContainerScreen<?>> extends FactoryScreenWindow<T> {

    public FactocraftyScreenWindow(AbstractDrawableButton<?> button, DrawableStatic drawable, T parent) {
        super(button, drawable, parent);
    }
    public FactocraftyScreenWindow(AbstractDrawableButton<?> button, int x, int y,int width, int height, T parent) {
        this(button, IFactoryDrawableType.create(null,0,0,width,height).createStatic(x,y), parent);
        useGeneratedBackground = true;
    }
    @Override
    public List<Renderable> getNestedRenderables() {
        List<Renderable> list = new ArrayList<>(nestedRenderables);
        list.add(new FactoryDrawableButton(getX() + width - 18,getY() + 10, FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(0)).tooltip(Component.translatable("tooltip.factocrafty.config.close")).onPress((b,i)-> onClose()));
        return list;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}
