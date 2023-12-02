package wily.factocrafty.client.screens;


import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.generator.entity.GeneratorBlockEntity;
import wily.factocrafty.client.screens.widgets.SlotsWindow;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageStringUtil.*;

public class GeneratorScreen<T extends GeneratorBlockEntity> extends FactocraftyStorageScreen<T> {
    public GeneratorScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    protected IFactoryDrawableType.DrawableProgress drawableBurn = FactocraftyDrawables.BURN_PROGRESS;

    @Override
    protected void init() {
        super.init();
        defaultProgress = FactocraftyDrawables.ENERGY_PROGRESS.createStatic(leftPos,topPos);
        energyCellType.setX(leftPos + 112);
        this.addWindowToGui(new FactoryDrawableButton(leftPos + imageWidth - 3,  topPos + 46, FactocraftyDrawables.MACHINE_CONFIG_BUTTON_INVERTED).icon(FactocraftyDrawables.getInfoIcon(1)).tooltip(Component.translatable("gui.factocrafty.window.equipment"))
                ,(config)-> new SlotsWindow(config,leftPos + imageWidth + 21,topPos, this, menu.equipmentSlots));
    }
    public List<? extends Renderable> getNestedRenderables() {
        List<Renderable> list = new ArrayList<>();
        list.add(new DrawableStatic(FactocraftyDrawables.MACHINE_INFO,leftPos  + imageWidth,  topPos +  100).tooltip(Component.translatable("tooltip.factocrafty.generating",  getStorageAmount(getMenu().be.energyTick.get(),false,CYMeasure,kiloCY,megaCY)).withStyle(ChatFormatting.GRAY)).overlay(FactocraftyDrawables.getInfoIcon(4)));
        list.addAll(nestedRenderables);
        return list;
    }
    public static <BE extends GeneratorBlockEntity, T extends GeneratorScreen<BE>> MenuRegistry.ScreenFactory<FactocraftyStorageMenu<BE>,T> cast(){
        return (containerMenu, inventory1, component1) -> (T) new GeneratorScreen<>(containerMenu, inventory1, component1);
    }
    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/generator.png");}

    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        super.renderStorageSprites(graphics, i, j);
        drawableBurn.drawProgress(graphics, leftPos, topPos,getMenu().be.burnTime);
    }

}
