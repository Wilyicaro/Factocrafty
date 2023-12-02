package wily.factocrafty.client.screens.widgets;

import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;

public class ReactorChamberWindow extends SlotsWindow{
    public ReactorChamberWindow(AbstractDrawableButton<?> button, int x, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        super(button, 70, 124, x, button.getY(), 0,0, parent, slots);
        useGeneratedBackground = true;
    }
}
