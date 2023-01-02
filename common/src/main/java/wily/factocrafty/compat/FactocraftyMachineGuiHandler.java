package wily.factocrafty.compat;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.FactocraftyWidget;

import java.util.ArrayList;
import java.util.List;

public class FactocraftyMachineGuiHandler implements IGuiContainerHandler<FactocraftyMachineScreen<?>> {
    @Override
    public List<Rect2i> getGuiExtraAreas(FactocraftyMachineScreen<?> containerScreen) {
        List<Rect2i> extraAreas = new ArrayList<>();
        extraAreas.add(containerScreen.getBounds());
        for ( GuiEventListener listener :containerScreen.children())
            if (listener instanceof FactocraftyWidget widget && widget.isVisible())
                extraAreas.add(widget.getBounds());


        return extraAreas;
    }
}
