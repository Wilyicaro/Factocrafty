package wily.factocrafty.client.screens.widgets.windows;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyDrawableButton;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.List;

public class SlotsWindow extends FactocraftyScreenWindow{


    public final int[] slots;
    public SlotsWindow(FactocraftyConfigWidget config, int width, int height, int x, int y, int uvX, int uvY, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        super(config, width,height, x, y, uvX, uvY, parent);
        this.slots = slots;
    }
    public SlotsWindow(FactocraftyConfigWidget config, int x, int y, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        this(config, 34,109, x, y, 222, 87, parent, slots);
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
        graphics.pose().translate(parent.getBounds().getX(), parent.getBounds().getY(),getBlitOffset());
        for (int k : slots) {
            if (parent.getMenu().slots.get(k) instanceof FactocraftySlotWrapper s) {
                s.x = getX() + s.initialX - parent.getBounds().getX();
                s.y = getY() + s.initialY - parent.getBounds().getY();
                if (s.active = isVisible()) {
                    parent.renderWindowSlot(graphics, s);
                    if (parent.isHovering(s,i,j) && s.isHighlightable()){
                        AbstractContainerScreen.renderSlotHighlight(graphics,s.x,s.y,0);
                    }
                }
            };
        }
        graphics.pose().popPose();
    }

    @Override
    public List<FactocraftyDrawableButton> addButtons(List<FactocraftyDrawableButton> list) {
        return list;
    }
}
