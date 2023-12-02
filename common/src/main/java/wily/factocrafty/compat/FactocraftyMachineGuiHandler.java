package wily.factocrafty.compat;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.block.machines.entity.ProcessMachineBlockEntity;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.client.screens.FactocraftyWidget;
import wily.factoryapi.base.client.IWindowWidget;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.AbstractDrawableStatic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FactocraftyMachineGuiHandler implements IGuiContainerHandler<FactocraftyStorageScreen<?>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(FactocraftyStorageScreen<?> containerScreen) {
        List<Rect2i> extraAreas = new ArrayList<>();
        extraAreas.add(containerScreen.getBounds());
        for (Renderable listener : containerScreen.getNestedRenderables())
            if (listener instanceof IWindowWidget widget && widget.isVisible())
                extraAreas.add(widget.getBounds());
            else if (listener instanceof AbstractDrawableStatic<?,?> b) extraAreas.add(b);
        return extraAreas;
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(FactocraftyStorageScreen<?> containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> list = new ArrayList<>();
        if (containerScreen.getMenu().be instanceof ProcessMachineBlockEntity<?> be){
            list.add(IGuiClickableArea.createBasic(be.progress.first().x,be.progress.first().y,containerScreen.defaultProgress.width(),containerScreen.defaultProgress.height(),FactocraftyJeiUtils.fromVanillaRecipeType(be.getRecipeType())));
        }
        return list;
    }
}
