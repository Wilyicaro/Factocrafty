package wily.factocrafty.compat;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.FactocraftyWidget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FactocraftyMachineGuiHandler implements IGuiContainerHandler<FactocraftyMachineScreen<?>> {
    @Override
    public @NotNull List<Rect2i> getGuiExtraAreas(FactocraftyMachineScreen<?> containerScreen) {
        List<Rect2i> extraAreas = new ArrayList<>();
        extraAreas.add(containerScreen.getBounds());
        for ( GuiEventListener listener :containerScreen.children())
            if (listener instanceof FactocraftyWidget widget && widget.isVisible())
                extraAreas.add(widget.getBounds());
        return extraAreas;
    }

    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(FactocraftyMachineScreen<?> containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> list = new ArrayList<>();
        if (containerScreen.getMenu().be instanceof FactocraftyMachineBlockEntity<?> be){
            list.add(IGuiClickableArea.createBasic(containerScreen.machineProgress.posX - containerScreen.getBounds().getX(),containerScreen.machineProgress.posY - containerScreen.getBounds().getY(),containerScreen.machineProgress.width(),containerScreen.machineProgress.height(),FactocraftyJeiUtils.fromVanillaRecipeType(be.getRecipeType())));
        }
        return list;
    }
}
