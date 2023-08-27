package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.client.IFactoryDrawableType;

public class FactocraftyConfigWidget extends FactocraftyInfoWidget {
    protected final boolean invert;
    public boolean Pressed = false;
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component) {
        super(x, y, 217, 21, ()-> component);
        this.invert = invert;
    }
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component, IFactoryDrawableType icon) {
        super(x, y,  invert ? 178 : 217, 21,()-> component,icon);
        this.invert = invert;

    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        playDownSound(1.0F);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int i, int j, float f) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int k = uvX + (Pressed ? 0 : width);
        int m = this.width - (Pressed ? 0 :3);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        graphics.blit(WIDGETS_LOCATION, this.getX() - (invert && Pressed ?3 : 0), this.getY(), k, 87, m, this.height);
        if (icon != null)
            graphics.blit(WIDGETS_LOCATION, this.getX() + (width - (invert ?5 : 0) - icon.width()) / 2, this.getY() + (height - icon.height()) / 2, icon.uvX(), icon.uvY(), icon.width(), icon.height());
        if (isHovered())
            this.renderToolTips(font,graphics, i, j);
    }

    @Override
    public boolean isVisible() {
        return true;
    }



    @Override
    public void onClick(double d, double e) {
        if (clicked(d,e)) Pressed = !Pressed;
    }




}

