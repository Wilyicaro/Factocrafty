package wily.factocrafty.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.SmithingMenu;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.client.screens.FactocraftyWidget;
import wily.factoryapi.base.Bearer;

import java.util.Map;
import java.util.function.Supplier;

public class FactocraftyInfoWidget extends FactocraftyWidget {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Factocrafty.MOD_ID,"textures/gui/container/widgets.png");


    public FactocraftyInfoWidget.onTooltip onTooltip;
    public FactocraftyInfoWidget.Icons icon;
    protected int uvX;

    protected final Supplier<Component> component;

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
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        if (button != null)map.put(button.create(getX(),getY()),false);
        return super.addButtons(map);
    }

    public FactocraftyInfoWidget(int x, int y, int uvX, int width, Supplier<Component> component, FactocraftyInfoWidget.Icons icon, FactocraftyInfoWidget.onTooltip tooltip) {
        this(x, y, uvX,width, component);
        this.uvX = uvX;
        this.onTooltip = tooltip;
        this.icon = icon;

    }

    public void playDownSound(SoundManager soundManager) {

    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        if (component.get() != getMessage())this.setMessage(component.get());
        super.render(poseStack, i, j, f);
    }

    @Override
    public void renderWidget(@NotNull PoseStack poseStack, int i, int j, float f) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(poseStack, getX(), getY(), uvX, 216, width, 20);
        if (icon != null)
            blit(poseStack, getX() + (width - icon.width) / 2, getY() + (height - icon.height) / 2, icon.uvX, icon.uvY, icon.width, icon.height);
        renderButtons(poseStack,i,j);
        this.renderToolTips(poseStack, i, j);
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


    public void renderToolTips(PoseStack poseStack, int i, int j) {
        if (onTooltip != null) {
            Bearer<Boolean> bl = Bearer.of(true);
            configButtons().forEach((button,b)-> {
                if (button.clicked(i,j)) bl.set(false);
            });
            if (isHovered()){
                if (bl.get())onTooltip.addTooltip(poseStack,getMessage(),i,j);
                else renderButtonsTooltip(onTooltip,poseStack,i,j);
            }
        }
    }
    public interface InfoButton{
        EasyButton create(int x, int y);
    }


    public record Icons(ResourceLocation location, int uvX, int uvY, int width, int height) {

        public Icons(int id){
            this(FactocraftyConfigWidget.WIDGETS_LOCATION, id * 14, 242, 14,14);
        }

    }

}
