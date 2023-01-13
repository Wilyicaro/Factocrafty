package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyWidget;

public class FactocraftyInfoWidget extends FactocraftyWidget {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID,"textures/gui/container/widgets.png");


    public FactocraftyInfoWidget.onTooltip onTooltip;
    public FactocraftyInfoWidget.Icons icon;
    protected int uvX;
    public FactocraftyInfoWidget(int x, int y,int uvX, int width,Component component) {
        super(x, y, width, 20, component);
        this.uvX = uvX;

    }
    public FactocraftyInfoWidget(int x, int y,int uvX, int width, Component component, FactocraftyInfoWidget.Icons icon, FactocraftyInfoWidget.onTooltip tooltip) {
        this(x, y, uvX,width, component);
        this.uvX = uvX;
        this.onTooltip = tooltip;
        this.icon = icon;

    }

    public void playDownSound(SoundManager soundManager) {

    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(poseStack, this.getX(), this.getY(), uvX, 216, width, 20);
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
        if (onTooltip != null) onTooltip.addTooltip(poseStack,getMessage(),i,j);
    }





    public interface onTooltip{
        void addTooltip( PoseStack poseStack, Component component, int i, int j);
    }

    public record Icons(ResourceLocation location, int uvX, int uvY, int width, int height) {

        public Icons(int id){
            this(FactocraftyConfigWidget.WIDGETS_LOCATION, id * 14, 242, 14,14);
        }

    }

}
