package wily.factocrafty.client.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.windows.FactocraftyScreenWindow;
import wily.factocrafty.client.screens.widgets.windows.MachineSidesConfig;
import wily.factocrafty.client.screens.widgets.windows.SlotsWindow;
import wily.factocrafty.client.screens.widgets.windows.UpgradesWindow;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factocrafty.recipes.AbstractFactocraftyProcessRecipe;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.IFactoryDrawableType;
import wily.factoryapi.base.Storages;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class FactocraftyMachineScreen<T extends FactocraftyProcessBlockEntity> extends AbstractContainerScreen<FactocraftyProcessMenu<T>> implements IWindowWidget {
    public FactocraftyMachineScreen(FactocraftyProcessMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");
    public ResourceLocation GUI() {return null;}
    protected int relX() {return (this.width - imageWidth) / 2;}
    protected int relY() { return (this.height - imageHeight) / 2;}

    protected int energyCellPosX = 112;
    protected int[] fluidTankPos = new int[]{56,14};
    protected IFactoryDrawableType.DrawableProgress energyCellType = FactocraftyDrawables.ENERGY_CELL;

    protected IFactoryDrawableType.DrawableProgress fluidTankType = FactocraftyDrawables.MINI_FLUID_TANK;

    protected MachineSidesConfig configWindow;




    @Override
    protected void init() {
        super.init();
            this.addConfigToGui(new FactocraftyConfigWidget(relX() - 18,  relY() + 20, false,Component.translatable("gui.factocrafty.window.transport"), new FactocraftyConfigWidget.Icons(3), this::renderTooltip)
                ,(config)-> configWindow = new MachineSidesConfig(config,relX() + imageWidth / 2 - 65,relY(), this));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.addConfigToGui(new FactocraftyConfigWidget(relX() + imageWidth,  relY() + 20, true,Component.translatable("gui.factocrafty.window.upgrade"), new FactocraftyConfigWidget.Icons(0), this::renderTooltip)
                ,(config)-> new UpgradesWindow(config,relX() + imageWidth + 21,relY(), this, new int[]{menu.upgradeSlot}));
    }

    @Override
    protected void renderSlot(PoseStack poseStack, Slot slot) {
        boolean b = slot instanceof FactocraftySlotWrapper s && s.blitOffset != 0;
        float blit = b ? ((FactocraftySlotWrapper)slot).blitOffset : 0;
        poseStack.pushPose();
        if (b) poseStack.translate(0F,0F,blit);
        super.renderSlot(poseStack, slot);
        poseStack.popPose();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        //if (getMenu().storage instanceof FactocraftyProcessBlockEntity be) be.syncAdditionalMenuData(getMenu(), menu.player);
        renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        if (!configWindow.dragging)
            renderTooltip(poseStack,i,j);

        for ( GuiEventListener gui: children()) {
            if (gui instanceof FactocraftyWidget widget){
                widget.render(poseStack, i, j, f);
            }
        }
        poseStack.pushPose();
        poseStack.translate(relX(),relY(), 0.0F);
        renderStorageTooltips(poseStack,i - relX(), j - relY());
        poseStack.popPose();

    }

    @Override
    public void renderTooltip(PoseStack poseStack, Component component, int i, int j) {
        for (Map.Entry<EasyButton, Boolean> button : configButtons().entrySet()){
            if (button.getKey().clicked(i,j)) {
                if (component.equals(button.getKey().message())) super.renderTooltip(poseStack, component, i, j);
                else return;
            }
        }
        super.renderTooltip(poseStack, component, i, j);
    }

    @Override
    public void renderTooltip(PoseStack poseStack, List<Component> list, Optional<TooltipComponent> optional, int i, int j) {
        if (getChildAt(i,j).isEmpty() || getChildAt(i,j).get() instanceof FactocraftyScreenWindow w && (!w.isVisible() || (!(w instanceof SlotsWindow s) || s.hasSlotAt(i,j)))){
            poseStack.pushPose();
            if (getChildAt(i,j).orElse(null) instanceof SlotsWindow s && s.hasSlotAt(i,j)) poseStack.translate(0F,0F, s.getBlitOffset());
            super.renderTooltip(poseStack, list, optional, i, j);
            poseStack.popPose();
        }
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

    protected void renderStorageTooltips(PoseStack poseStack, int i, int j){
        if (!getMenu().storage.getTanks().isEmpty() && fluidTankType.inMouseLimit(i,j,  fluidTankPos[0], fluidTankPos[1])) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", getMenu().storage.getTanks().get(0)),i, j);
        getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent(e->{if (energyCellType.inMouseLimit(i, j, energyCellPosX, 17)) renderComponentTooltip(poseStack, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", Component.translatable("tier.factocrafty.burned.note"),e), i, j);});
    }


    public static int getProgressScaled(int min, int max, int pixels) {
        return max != 0 && min != 0 ? Math.round(min * (float)pixels / max) : 0;
    }

    @Override
    protected boolean isHovering(int i, int j, int k, int l, double d, double e) {
        if (configWindow.isMouseOver(d,e) && configWindow.isVisible()) return false;
        return super.isHovering(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (Platform.isFabric()){
            this.setDragging(false);

            this.getChildAt(d, e).filter((guiEventListener) -> guiEventListener.mouseReleased(d, e, i));
        }
        return super.mouseReleased(d, e, i);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (mouseClickedButtons(d,e,i)) return true;
        return super.mouseClicked(d, e, i);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI());
        this.blit(poseStack, relX(), relY(), 0, 0, imageWidth, imageHeight);
        renderStorageSprites(poseStack,i,j);
        renderButtons(poseStack,i,j);
        renderButtonsTooltip(this::renderTooltip,poseStack,i,j);
    }
    protected void renderStorageSprites(PoseStack poseStack, int i, int j){
        getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> energyCellType.drawProgress(poseStack,relX() + energyCellPosX, relY() + 17, getProgressScaled(e.getEnergyStored(),e.getMaxEnergyStored(), 52)));
        getMenu().storage.getStorage(Storages.FLUID).ifPresent((g)-> {if (!(getMenu().be instanceof FactocraftyMachineBlockEntity be) || !be.isInputSlotActive())fluidTankType.drawAsFluidTank(poseStack,relX() + fluidTankPos[0], relY() + fluidTankPos[1], getProgressScaled((int) g.getFluidStack().getAmount(), (int) g.getMaxFluid(), fluidTankType.height()), g.getFluidStack(), true);});
        getMenu().slots.forEach((slot)-> {
            if (slot instanceof FactoryItemSlot s && s.isActive()) {
                IFactoryDrawableType drawSlot = s.getType() == FactoryItemSlot.Type.BIG ? FactocraftyDrawables.BIG_SLOT : FactocraftyDrawables.SLOT;
                drawSlot.draw(poseStack, s.getType().getOutPos(relX() + s.x), s.getType().getOutPos(relY() + s.y));
                if (configWindow.isVisible()) {
                    IFactoryDrawableType drawOut = s.getType() == FactoryItemSlot.Type.BIG ? FactocraftyDrawables.BIG_SLOT_OUTLINE : FactocraftyDrawables.SLOT_OUTLINE;
                    int c = Objects.requireNonNullElse(s.identifier().color().getColor(), 0xFFFFF);
                    RenderSystem.setShaderColor(ScreenUtil.getRed(c), ScreenUtil.getGreen(c), ScreenUtil.getBlue(c), 1.0F);
                    drawOut.draw(poseStack, s.getType().getOutPos(relX() + s.x), s.getType().getOutPos(relY() + s.y));
                    RenderSystem.setShaderColor(1.0F,1.0F,1.0F, 1.0F);
                }
            }
        });

    }

    public BakedModel getItemStackModel(ItemStack stack){
        return itemRenderer.getModel(stack,null,null,1);
    }

    public void renderGuiBlock(@Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(i, j, 500.0F );
        poseStack.translate(8.0, 8.0, 0.0);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        poseStack2.scale(scaleX, scaleY, 0.5F);
        poseStack2.mulPose(Axis.XP.rotationDegrees(rotateX));
        poseStack2.mulPose(Axis.YP.rotationDegrees(rotateY));
        ItemStack stack = state.getBlock().asItem().getDefaultInstance();
        BakedModel bakedModel = getItemStackModel(stack);
        if (bakedModel.isCustomRenderer()){
            if (be != null){
                CompoundTag tag = new CompoundTag();
                if (be.getBlockState().getBlock() instanceof IFactocraftyOrientableBlock ob) {
                    CompoundTag blockStateTag = new CompoundTag();
                    blockStateTag.putString("facing", state.getValue(ob.getFacingProperty()).toString());
                    tag.put(BlockItem.BLOCK_STATE_TAG, blockStateTag);
                }
                tag.put("BlockEntityTag", be.getUpdateTag());
                stack.setTag( tag);
            }
        } else bakedModel = minecraft.getBlockRenderer().getBlockModel(state);
        itemRenderer.render(stack, ItemDisplayContext.NONE, false, poseStack2, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        bufferSource.endBatch();
        RenderSystem.enableDepthTest();



        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    @Override
    public Rect2i getBounds() {
        return new Rect2i(relX(),relY(),imageWidth,imageHeight);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
