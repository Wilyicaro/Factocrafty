package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.machines.entity.ProcessMachineBlockEntity;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyScreenWindow;
import wily.factocrafty.client.screens.widgets.MachineSidesConfig;
import wily.factocrafty.client.screens.widgets.SlotsWindow;
import wily.factocrafty.client.screens.widgets.UpgradesWindow;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.IFactoryProgressiveStorage;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.client.FactoryScreenWindow;
import wily.factoryapi.base.client.IWindowWidget;
import wily.factoryapi.base.client.drawable.*;
import wily.factoryapi.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class FactocraftyStorageScreen<T extends FactocraftyMenuBlockEntity> extends AbstractContainerScreen<FactocraftyStorageMenu<T>> implements IWindowWidget {
    public FactocraftyStorageScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");
    public ResourceLocation GUI() {return null;}

    protected DrawableStaticProgress energyCellType;

    protected DrawableStatic fluidTankType;

    public DrawableStaticProgress defaultProgress;

    protected MachineSidesConfig configWindow;

    protected List<Renderable> nestedRenderables = new ArrayList<>();


    protected T be = getMenu().be;

    @Override
    protected void init() {
        super.init();
        nestedRenderables.clear();
        energyCellType = FactocraftyDrawables.ENERGY_CELL.createStatic(leftPos + 20 ,topPos + 17);
        fluidTankType = FactocraftyDrawables.MINI_FLUID_TANK.createStatic(leftPos + 56, topPos + 14);
        this.addWindowToGui(new FactoryDrawableButton(leftPos - 18,  topPos + 20, FactocraftyDrawables.MACHINE_CONFIG_BUTTON).tooltip(Component.translatable("gui.factocrafty.window.transport")).icon(FactocraftyDrawables.getInfoIcon(3))
                ,(config)-> configWindow = new MachineSidesConfig(config,leftPos + imageWidth / 2 - 65,topPos, this));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.addWindowToGui(new FactoryDrawableButton(leftPos + imageWidth - 3,  topPos + 20, FactocraftyDrawables.MACHINE_CONFIG_BUTTON_INVERTED).icon(FactocraftyDrawables.getInfoIcon(0)).tooltip(Component.translatable("gui.factocrafty.window.upgrade"))
                ,(config)-> new UpgradesWindow(config,leftPos + imageWidth + 21,topPos, this, new int[]{menu.upgradeSlot}));
    }

    @Override
    public void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot instanceof FactoryItemSlot s && (s.getCustomX() != s.x || s.getCustomY() != s.y)){
            slot.x = s.getCustomX();
            slot.y = s.getCustomY();
        }
        if (!(slot instanceof FactocraftySlotWrapper)) super.renderSlot(graphics, slot);
    }
    public void renderWindowSlot(GuiGraphics graphics, Slot slot) {
        if (slot instanceof FactocraftySlotWrapper) super.renderSlot(graphics, slot);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        renderBackground(graphics);
        super.render(graphics, i, j, f);
        if (!(getNestedAt(i,j).get() instanceof FactocraftyScreenWindow<?> w) || !w.dragging)
            renderTooltip(graphics,i,j);
        IWindowWidget.super.render(graphics,i,j,f);

        renderStorageTooltips(graphics,i , j);

    }


    @Override
    public void renderTooltip(GuiGraphics graphics, int i, int j) {
        if (!(getNestedAt(i,j).get() instanceof SlotsWindow w && w.isVisible() && w.hasSlotAt(i,j)))
            super.renderTooltip(graphics, i, j);
    }
    public void renderWindowTooltip(GuiGraphics graphics, int i, int j) {
        super.renderTooltip(graphics, i, j);
    }


    @Override
    public boolean shouldCloseOnEsc() {
        if (getNestedRenderables().stream().anyMatch(p-> p instanceof FactoryScreenWindow<?> w && w.isVisible())) return false;
        return super.shouldCloseOnEsc();
    }

    public void addWindowToGui(AbstractDrawableButton<?> config, Function<AbstractDrawableButton<?>, FactoryScreenWindow<?>> func){
        FactoryScreenWindow<?> window = addNestedRenderable(func.apply(config));
        addNestedRenderable(config.disableHoverSelection().grave(1.0F).select(false).selection(IFactoryDrawableType.Direction.HORIZONTAL).onPress((b,i)-> window.onClickWidget()));
    }


    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j){
        if (!getMenu().be.getTanks().isEmpty() && fluidTankType.inMouseLimit(i,j)) graphics.renderTooltip(font,getFluidTooltip("tooltip.factory_api.fluid_stored", getMenu().be.getTanks().get(0)),i, j);
        getMenu().be.getStorage(Storages.CRAFTY_ENERGY).ifPresent(e->{if (energyCellType.inMouseLimit(i, j)) graphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),e), i, j);});
    }


    public static int getProgressScaled(int min, int max, int pixels) {
        return max != 0 && min != 0 ? Math.round(min * (float)pixels / max) : 0;
    }

    @Override
    protected boolean isHovering(int i, int j, int k, int l, double d, double e) {
        if (getNestedAt(i - leftPos,j - topPos).get() instanceof FactoryScreenWindow<?> w && w.isVisible() && (!(w instanceof SlotsWindow s) || !s.hasSlotAt(i,j))) return false;
        return super.isHovering(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        this.setDragging(false);
        if (IWindowWidget.super.mouseReleased(d,e,i)) return true;
        return super.mouseReleased(d,e,i);
    }
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (IWindowWidget.super.mouseDragged(d,e,i,f,g)) return true;
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (IWindowWidget.super.mouseClicked(d,e,i)) return true;
        return super.mouseClicked(d, e, i);
    }


    @Override
    protected void renderBg(GuiGraphics graphics, float f, int i, int j) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(GUI(),leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderStorageSprites(graphics,i,j);
    }
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j){
        getMenu().be.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> energyCellType.drawProgress(graphics, e.getEnergyStored(),e.getMaxEnergyStored()));
        getMenu().be.getStorage(Storages.FLUID).ifPresent((g)-> {if (!(getMenu().be instanceof ProcessMachineBlockEntity<?> be) || !be.isInputSlotActive())fluidTankType.drawAsFluidTank(graphics, g.getFluidStack(), (int) g.getMaxFluid(), true);});
        menu.slots.forEach((slot)-> {
            if (slot instanceof FactoryItemSlot s) renderSlotBackground(graphics,s);

        });
        if (defaultProgress != null && menu.be instanceof IFactoryProgressiveStorage fp) defaultProgress.drawProgress(graphics, fp.getProgresses().get(0));
    }

    public void renderSlotBackground(GuiGraphics graphics,FactoryItemSlot s){
        if (s.isActive()) {
            int size = s.getType() == FactoryItemSlot.Type.BIG ? 26 : 18;
            graphics.pose().pushPose();
            ScreenUtil.drawGUISlot(graphics, s.getType().getOutPos(leftPos + s.getCustomX()), s.getType().getOutPos(topPos + s.getCustomY()), size, size);
            if (configWindow.isVisible()) {
                int c = Objects.requireNonNullElse(s.identifier().color().getColor(), 0xFFFFF);
                RenderSystem.setShaderColor(ScreenUtil.getRed(c), ScreenUtil.getGreen(c), ScreenUtil.getBlue(c), 1.0F);
                ScreenUtil.drawGUISlotOutline(graphics, s.getType().getOutPos(leftPos + s.getCustomX()), s.getType().getOutPos(topPos + s.getCustomY()), size, size);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
            graphics.pose().popPose();
        }
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(leftPos,topPos,imageWidth,imageHeight);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public <R extends Renderable> R addNestedRenderable(R drawable) {
        nestedRenderables.add(drawable);
        return drawable;
    }

    @Override
    public List<? extends Renderable> getNestedRenderables() {
        return nestedRenderables;
    }
}
