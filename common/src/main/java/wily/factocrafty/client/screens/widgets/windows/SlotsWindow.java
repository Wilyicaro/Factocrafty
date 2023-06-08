package wily.factocrafty.client.screens.widgets.windows;

import com.mojang.blaze3d.vertex.PoseStack;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factoryapi.base.IFactoryDrawableType;

import java.util.Map;

public class SlotsWindow extends FactocraftyScreenWindow{


    public final int[] slots;
    public SlotsWindow(FactocraftyConfigWidget config,int width, int height, int x, int y, int uvX, int uvY,FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> parent, int[] slots) {
        super(config, width,height, x, y, uvX, uvY, parent);
        this.slots = slots;
    }
    public SlotsWindow(FactocraftyConfigWidget config, int x, int y, FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> parent, int[] slots) {
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
    public void render(PoseStack poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        for (int k : slots) {
            if (parent.getMenu().slots.get(k) instanceof FactocraftySlotWrapper s) {
                s.x = getX() + s.initialX - parent.getBounds().getX();
                s.y = getY() + s.initialY - parent.getBounds().getY();
                s.active = isVisible();
                s.blitOffset = getBlitOffset();
            };
        }
    }

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        return Map.of();
    }
}
