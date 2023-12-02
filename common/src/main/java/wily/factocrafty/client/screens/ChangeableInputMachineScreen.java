package wily.factocrafty.client.screens;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class ChangeableInputMachineScreen<T extends ChangeableInputMachineBlockEntity> extends BasicMachineScreen<T> {


    public ChangeableInputMachineScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    public static MenuRegistry.ScreenFactory<FactocraftyStorageMenu<ChangeableInputMachineBlockEntity>,ChangeableInputMachineScreen<ChangeableInputMachineBlockEntity>> extractor(){
        return ChangeableInputMachineScreen::new;
    }
    protected String getRecipeTypeName(){
        String s = Registration.RECIPE_TYPES.getRegistrar().getId(be.getRecipeType()).getPath();
        return s.contains("_") ? s.split("_")[0] : s;
    }
    @Override
    protected void init() {
        super.init();
        addNestedRenderable(new DrawableStatic( FactocraftyDrawables.MACHINE_BUTTON_LAYOUT,leftPos - 20,  topPos + 100));
    }
    public List<? extends Renderable> getNestedRenderables() {
        List<Renderable> list = new ArrayList<>(nestedRenderables);
        list.add(new FactoryDrawableButton(leftPos - 20 + 2, topPos + 100 + 2, FactocraftyDrawables.LARGE_BUTTON).tooltips(List.of(Component.translatable("tooltip.factocrafty.machine_config", title.getString()).append(":"),Component.translatable("tooltip.factocrafty.config.input_type."+ be.getInputType().getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())))).icon(FactocraftyDrawables.getButtonIcon(be.getInputType().isItem() ? 8 : 2)).onPress((b, i)-> Factocrafty.NETWORK.sendToServer( new FactocraftySyncIntegerBearerPacket(be.getBlockPos(), be.inputType.get() >= 1 ? 0: 1, be.additionalSyncInt.indexOf(be.inputType)))));
        return list;
    }
    @Override
    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j) {
        if (energyCellType.inMouseLimit(i,j)) graphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),be.energyStorage),i, j);
        else if (fluidTankType.inMouseLimit(i,j) && be.getInputType().isFluid()) graphics.renderTooltip(font, getFluidTooltip("tooltip.factory_api.fluid_stored", be.fluidTank),i, j);
    }
    @Override
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j) {
        if (be.getInputType().isFluid())
            ScreenUtil.drawGUIFluidSlot(graphics,leftPos + 55, topPos + 13,18,21);
        super.renderStorageSprites(graphics, i, j);
    }
}
