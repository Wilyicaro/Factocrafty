package wily.factocrafty.client.screens.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factoryapi.base.client.FactoryScreenWindow;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

public class SlotsWindow extends FactoryScreenWindow<FactocraftyStorageScreen<?>> {


    public final int[] slots;
    public SlotsWindow(AbstractDrawableButton<?> button, int width, int height, int x, int y, int uvX, int uvY, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        super(button, IFactoryDrawableType.create(FactocraftyDrawables.CONFIG_WIDGETS,uvX,uvY,width,height).createStatic(x,y), parent);
        this.slots = slots;
    }
    public SlotsWindow(AbstractDrawableButton<?> button, int x, int y, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        this(button, 34,109, x, y, 222, 87, parent, slots);
    }
    public boolean hasSlotAt(int i, int j){
        for (int k : slots) {
            if (parent.getMenu().slots.get(k) instanceof FactocraftySlotWrapper s) {
                if (IFactoryDrawableType.getMouseLimit(i,j,parent.getBounds().getX() + s.x,parent.getBounds().getY() +s.y,16,16)) return true;
            }
        }
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);
        graphics.pose().pushPose();
        graphics.pose().translate(0F, 0F,getBlitOffset());
        for (int k : slots) {
            if (parent.getMenu().slots.get(k) instanceof FactocraftySlotWrapper s) {
                s.x = getX() + s.initialX - parent.getBounds().getX();
                s.y = getY() + s.initialY - parent.getBounds().getY();
                s.active = isVisible();
                if (s.isActive()) {
                    graphics.pose().pushPose();
                    parent.renderTooltip(graphics,i,j);
                    parent.renderSlotBackground(graphics,s);
                    graphics.pose().translate(parent.getBounds().getX(), parent.getBounds().getY(),0F);
                    parent.renderWindowSlot(graphics, s);
                    if (parent.isHovering(s,i,j) && s.isHighlightable())
                        AbstractContainerScreen.renderSlotHighlight(graphics,s.x,s.y,0);

                    graphics.pose().popPose();
                }
            }
        }
        if (hasSlotAt(i,j) && isVisible())
            parent.renderTooltip(graphics,i,j);
        graphics.pose().popPose();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
