package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FactocraftyConfigWidget extends FactocraftyInfoWidget {
    protected final boolean invert;
    public boolean Pressed = false;
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component) {
        super(x, y, 217, 21, component);
        this.invert = invert;
    }
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component, Icons icon,onTooltip tooltip) {
        super(x, y,  217, 21, component,icon,tooltip);
        this.invert = invert;

    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        playDownSound(1.0F);
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int k = uvX + (Pressed ? 0 : width);
        int m = this.width - (Pressed ? 0 :3);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.getX(), this.getY(), k, 87, m, this.height);
        if (icon != null)
            this.blit(poseStack, this.getX() + (width - icon.width()) / 2, this.getY() + (height - icon.height()) / 2, icon.uvX(), icon.uvY(), icon.width(), icon.height());
        this.renderBg(poseStack, minecraft, i, j);
        if (isHoveredOrFocused())
            this.renderToolTip(poseStack, i, j);
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

