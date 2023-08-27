package wily.factocrafty.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factocrafty.block.machines.entity.ProcessMachineBlockEntity;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.renderer.block.FactocraftyBlockEntityWLRenderer;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.windows.FactocraftyScreenWindow;
import wily.factocrafty.client.screens.widgets.windows.MachineSidesConfig;
import wily.factocrafty.client.screens.widgets.windows.SlotsWindow;
import wily.factocrafty.client.screens.widgets.windows.UpgradesWindow;
import wily.factocrafty.inventory.FactocraftyStorageMenu;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factocrafty.item.FactocraftyMachineBlockItem;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.IFactoryProgressiveStorage;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.client.IFactoryDrawableType;
import wily.factoryapi.base.client.IWindowWidget;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class FactocraftyStorageScreen<T extends FactocraftyMenuBlockEntity> extends AbstractContainerScreen<FactocraftyStorageMenu<T>> implements IWindowWidget {
    public FactocraftyStorageScreen(FactocraftyStorageMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");
    public ResourceLocation GUI() {return null;}

    protected IFactoryDrawableType.DrawableStaticProgress energyCellType;

    protected IFactoryDrawableType.DrawableStatic<IFactoryDrawableType.DrawableImage> fluidTankType;

    public IFactoryDrawableType.DrawableStaticProgress defaultProgress;

    protected MachineSidesConfig configWindow;


    protected T be = getMenu().be;

    @Override
    protected void init() {
        super.init();
        energyCellType = FactocraftyDrawables.ENERGY_CELL.createStatic(leftPos + 20 ,topPos + 17);
        fluidTankType = FactocraftyDrawables.MINI_FLUID_TANK.createStatic(leftPos + 56, topPos + 14);
        this.addConfigToGui(new FactocraftyConfigWidget(leftPos - 18,  topPos + 20, false,Component.translatable("gui.factocrafty.window.transport"), FactocraftyDrawables.getInfoIcon(3))
                ,(config)-> configWindow = new MachineSidesConfig(config,leftPos + imageWidth / 2 - 65,topPos, this));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.addConfigToGui(new FactocraftyConfigWidget(leftPos + imageWidth,  topPos + 20, true,Component.translatable("gui.factocrafty.window.upgrade"), FactocraftyDrawables.getInfoIcon(0))
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
        if (!(getChildAt(i,j).orElse(null) instanceof FactocraftyScreenWindow w) || !w.dragging)
            renderTooltip(graphics,i,j);

        for ( GuiEventListener gui: children()) {
            if (gui instanceof FactocraftyWidget widget){
                widget.render(graphics, i, j, f);
            }
        }
        renderStorageTooltips(graphics,i , j);

    }


    @Override
    public void renderTooltip(GuiGraphics graphics, int i, int j) {
        if (getChildAt(i,j).isPresent() && getChildAt(i,j).get() instanceof SlotsWindow w && w.isVisible() && w.hasSlotAt(i,j)){
            graphics.pose().pushPose();
            graphics.pose().translate(0F,0F, w.getBlitOffset());
            super.renderTooltip(graphics, i, j);
            graphics.pose().popPose();
        }else super.renderTooltip(graphics, i, j);
    }


    @Override
    public boolean shouldCloseOnEsc() {
        if (getFocused() instanceof FactocraftyScreenWindow w && w.isVisible()) return false;
        return super.shouldCloseOnEsc();
    }

    public void addConfigToGui(FactocraftyConfigWidget config, Function<FactocraftyConfigWidget,FactocraftyScreenWindow> func){
        addWidget(config);
        addWidget(func.apply(config));
    }


    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        for(GuiEventListener guieventlistener : this.children()) {
           guieventlistener.mouseDragged(d, e, i, f, g);
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    protected void renderStorageTooltips(GuiGraphics graphics, int i, int j){
        if (!getMenu().storage.getTanks().isEmpty() && fluidTankType.inMouseLimit(i,j)) graphics.renderTooltip(font,getFluidTooltip("tooltip.factory_api.fluid_stored", getMenu().storage.getTanks().get(0)),i, j);
        getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent(e->{if (energyCellType.inMouseLimit(i, j)) graphics.renderComponentTooltip(font, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),e), i, j);});
    }


    public static int getProgressScaled(int min, int max, int pixels) {
        return max != 0 && min != 0 ? Math.round(min * (float)pixels / max) : 0;
    }

    @Override
    protected boolean isHovering(int i, int j, int k, int l, double d, double e) {
        if (getChildAt(i - leftPos,j - topPos).orElse(null) instanceof FactocraftyScreenWindow w && w.isVisible() && (!(w instanceof SlotsWindow s) || !s.hasSlotAt(i,j))) return false;
        return super.isHovering(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        this.setDragging(false);
        for(GuiEventListener gui : this.children()) {
            if (gui.isMouseOver(d,e) && gui.isFocused() && gui.mouseReleased(d,e,i)) return true;
        }
        return super.mouseReleased(d,e,i);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (mouseClickedButtons(d,e,i)) return true;
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float f, int i, int j) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(GUI(),leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderStorageSprites(graphics,i,j);
        renderButtons(graphics,i,j);
        renderButtonsTooltip(font,graphics,i,j);
    }
    protected void renderStorageSprites(GuiGraphics graphics, int i, int j){
        getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> energyCellType.drawProgress(graphics, e.getEnergyStored(),e.getMaxEnergyStored()));
        getMenu().storage.getStorage(Storages.FLUID).ifPresent((g)-> {if (!(getMenu().be instanceof ProcessMachineBlockEntity be) || !be.isInputSlotActive())fluidTankType.drawAsFluidTank(graphics, g.getFluidStack(), (int) g.getMaxFluid(), true);});
        getMenu().slots.forEach((slot)-> {
            if (slot instanceof FactoryItemSlot s && s.isActive()) {
                int size = s.getType() == FactoryItemSlot.Type.BIG ? 26 : 18;
                ScreenUtil.drawGUISlot(graphics,s.getType().getOutPos(leftPos + s.getCustomX()), s.getType().getOutPos(topPos + s.getCustomY()),size,size);
                if (configWindow.isVisible()) {
                    int c = Objects.requireNonNullElse(s.identifier().color().getColor(), 0xFFFFF);
                    RenderSystem.setShaderColor(ScreenUtil.getRed(c), ScreenUtil.getGreen(c), ScreenUtil.getBlue(c), 1.0F);
                    ScreenUtil.drawGUISlotOutline(graphics, s.getType().getOutPos(leftPos + s.getCustomX()), s.getType().getOutPos(topPos + s.getCustomY()),size,size);
                    RenderSystem.setShaderColor(1.0F,1.0F,1.0F, 1.0F);
                }
            }
        });
        if (defaultProgress != null && menu.be instanceof IFactoryProgressiveStorage fp) defaultProgress.drawProgress(graphics, fp.getProgresses().get(0));
    }

    public BakedModel getItemStackModel(ItemRenderer itemRenderer,ItemStack stack){
        return itemRenderer.getModel(stack,null,null,1);
    }
    public void renderGuiBlock(GuiGraphics graphics, @Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        ItemStack stack = state.getBlock().asItem().getDefaultInstance();
        BakedModel bakedModel = getItemStackModel(itemRenderer,stack);
        graphics.pose().pushPose();
        graphics.pose().translate(i + 8F, j + 8F, 250F);
        graphics.pose().scale(1.0F, -1.0F, 1.0F);
        graphics.pose().scale(16.0F, 16.0F, 16.0F);
        graphics.pose().scale(scaleX, scaleY, 0.5F);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(rotateX));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(rotateY));
        Lighting.setupForFlatItems();
        Consumer<BakedModel> defaultRender = (b)->itemRenderer.render(stack, ItemDisplayContext.NONE, false, graphics.pose(), graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY,b);
        if (bakedModel.isCustomRenderer()){
            stack.getOrCreateTag().put("BlockEntityTag" ,be.getUpdateTag());
            if (state.getBlock().asItem() instanceof FactocraftyMachineBlockItem) {
                bakedModel.getTransforms().getTransform(ItemDisplayContext.NONE).apply(false, graphics.pose());
                graphics.pose().translate(-0.5f, -0.5f, -0.5f);
                FactocraftyBlockEntityWLRenderer.INSTANCE.renderByItemBlockState(state, stack, ItemDisplayContext.NONE, graphics.pose(), graphics.bufferSource(),15728880, OverlayTexture.NO_OVERLAY);
            }else defaultRender.accept(bakedModel);
        }else defaultRender.accept(minecraft.getBlockRenderer().getBlockModel(state));

        graphics.flush();
        graphics.pose().popPose();
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(leftPos,topPos,imageWidth,imageHeight);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
