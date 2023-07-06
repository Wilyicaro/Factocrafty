package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyDrawableButton;
import wily.factocrafty.client.screens.FactocraftyWidget;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.List;
import java.util.function.Supplier;

public class FactocraftyInfoWidget extends FactocraftyWidget {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID,"textures/gui/container/widgets.png");


    protected int uvX;

    protected final Supplier<Component> component;

    protected IFactoryDrawableType icon;

    public InfoButton button;
    public FactocraftyInfoWidget(int x, int y,int uvX, int width,Supplier<Component> component) {
        super(x, y, width, 20, component.get());
        this.uvX = uvX;
        this.component = component;


    }
    public FactocraftyInfoWidget withButton(InfoButton button){
        this.button = button;
        return this;
    };

    @Override
    public List<FactocraftyDrawableButton> addButtons(List<FactocraftyDrawableButton> list) {
        if (button != null) list.add(button.create(getX(), getY()));
        return super.addButtons(list);
    }

    public FactocraftyInfoWidget(int x, int y, int uvX, int width, Supplier<Component> component, IFactoryDrawableType icon) {
        this(x, y, uvX,width, component);
        this.uvX = uvX;
        this.icon = icon;

    }

    public void playDownSound(SoundManager soundManager) {

    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        if (component.get() != getMessage())this.setMessage(component.get());
        super.render(graphics, i, j, f);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int i, int j, float f) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(WIDGETS_LOCATION,getX(), getY(), uvX, 216, width, 20);
        if (icon != null)
            icon.draw(graphics,getX() + (width - icon.width()) / 2, getY() + (height - icon.height()) / 2);
        renderButtons(graphics,i,j);
        this.renderToolTips(font,graphics, i, j);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (mouseClickedButtons(d,e,i)) return true;
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean isVisible() {
        return true;
    }


    public void renderToolTips(Font font, GuiGraphics graphics, int i, int j) {
            Bearer<Boolean> bl = Bearer.of(true);
            configButtons().forEach((button)-> {
                if (button.inMouseLimit(i,j)) bl.set(false);
            });
            if (isHovered()){
                if (bl.get())graphics.renderTooltip(font,getMessage(),i,j);
                else renderButtonsTooltip(font,graphics,i,j);
            }

    }

    public interface InfoButton{
        FactocraftyDrawableButton create(int x, int y);
    }


}
