package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyWidget;

public class FactocraftyConfigWidget extends FactocraftyWidget {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID,"textures/gui/container/widgets.png");

    protected final boolean invert;
    public boolean Pressed = false;
    public onTooltip onTooltip;
    public Icons icon;
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component) {
            super(x, y, 21, 20, component);
        this.invert = invert;
    }
    public FactocraftyConfigWidget(int x, int y, boolean invert, Component component, Icons icon,onTooltip tooltip) {
        this(x, y,  invert, component);
        this.onTooltip = tooltip;
        this.icon = icon;

    }


    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int k = 217 + (Pressed ? 0 : width);
        int m = this.width - (Pressed ? 0 :3);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.getX(), this.getY(), k, 87, m, this.height);
        if (icon != null)
            this.blit(poseStack, this.getX() + (width - icon.width) / 2, this.getY() + (height - icon.height) / 2, icon.uvX, icon.uvY, icon.width, icon.height);
        this.renderBg(poseStack, minecraft, i, j);
        if (isHoveredOrFocused())
            this.renderToolTip(poseStack, i, j);
    }

    @Override
    public boolean isVisible() {
        return true;
    }


    public void renderToolTip(PoseStack poseStack, int i, int j) {
        if (onTooltip != null) onTooltip.addTooltip(getMessage(),poseStack,i,j);
    }

    @Override
    public void onClick(double d, double e) {
        if (clicked(d,e)) Pressed = !Pressed;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public interface onTooltip{
        void addTooltip(Component component, PoseStack poseStack, int i, int j);
    }

    public record Icons(ResourceLocation location, int uvX, int uvY, int width, int height) {

        public Icons(int id){
            this(FactocraftyConfigWidget.WIDGETS_LOCATION, id * 14, 242, 14,14);
        }

    }

}

