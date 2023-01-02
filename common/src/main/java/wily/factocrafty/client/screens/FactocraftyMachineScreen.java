package wily.factocrafty.client.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.IFactocraftyOrientableBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.client.screens.widgets.windows.FactocraftyScreenWindow;
import wily.factocrafty.client.screens.widgets.windows.MachineSidesConfig;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factoryapi.base.ProgressType;
import wily.factoryapi.base.Storages;
import wily.factoryapi.util.ProgressElementRenderUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;


public class FactocraftyMachineScreen<T extends BlockEntity> extends AbstractContainerScreen<FactocraftyProcessMenu<T>> implements IWidget  {
    public FactocraftyMachineScreen(FactocraftyProcessMenu<T> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);

    }
    public static final ResourceLocation WIDGETS = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/widgets.png");
    public ResourceLocation GUI() {return null;}
    protected int relX() {return (this.width - imageWidth) / 2;}
    protected int relY() { return (this.height - imageHeight) / 2;}

    protected int energyCellPosX = 112;
    protected int[] fluidTankPos = new int[]{56,14};
    protected ProgressType energyCellType = FactocraftyProgressType.ENERGY_CELL;

    protected ProgressType fluidTankType = FactocraftyProgressType.MINI_FLUID_TANK;

    protected FactocraftyScreenWindow configWindow;




    @Override
    protected void init() {
        super.init();

        if (getMenu().be instanceof FactocraftyProcessBlockEntity)
            this.addConfigToGui(new FactocraftyConfigWidget(relX() - 18,  relY() + 20, true,Component.literal("Config Block Sides Transport"), new FactocraftyConfigWidget.Icons(3), (a, b, c, d)-> renderTooltip(b,a,c,d))
                ,(config)-> configWindow = new MachineSidesConfig(config,relX() + imageWidth / 2 - 65,relY(), (FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity>) this));
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;

    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        if (!configWindow.dragging)
            renderTooltip(poseStack,i,j);

        for ( GuiEventListener gui: children()) {
            if (gui instanceof FactocraftyWidget widget){
                widget.render(poseStack, i, j, f);
            }
        }


    }



    @Override
    public void renderTooltip(PoseStack poseStack, List<Component> list, Optional<TooltipComponent> optional, int i, int j) {
        if (!configWindow.isMouseOver(i,j) || !configWindow.isVisible()) super.renderTooltip(poseStack, list, optional, i, j);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (configWindow.isVisible() && getFocused() == configWindow) return false;
        return super.shouldCloseOnEsc();
    }

    public void addConfigToGui(FactocraftyConfigWidget config, Function<FactocraftyConfigWidget,FactocraftyScreenWindow> func){
        addWidget(config);
        addWidget(func.apply(config));
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int i, int j) {
        super.renderLabels(poseStack, i, j);
        int mouseX = i - relX();
        int mouseY = j - relY();
        renderStorageTooltips(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        for(GuiEventListener guieventlistener : this.children()) {
            guieventlistener.mouseDragged(d, e, i, f, g);
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    protected void renderStorageTooltips(PoseStack poseStack, int i, int j){
        if (!getMenu().storage.getTanks().isEmpty() && fluidTankType.inMouseLimit(i,j,  fluidTankPos[0],fluidTankPos[1])) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", getMenu().storage.getTanks().get(0)),i, j);
        if (getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).isPresent() && energyCellType.inMouseLimit(i,j,  energyCellPosX,  17)) renderComponentTooltip(poseStack, getCompleteEnergyTooltip("tooltip.factocrafty.energy_stored", getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).get()),i, j);
    }




    public int getProgressScaled(int min, int max, int pixels) {
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
    protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI());
        this.blit(poseStack, relX(), relY(), 0, 0, imageWidth, imageHeight);
        getMenu().storage.getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> ProgressElementRenderUtil.renderDefaultProgress(poseStack,this,relX() + energyCellPosX, relY() + 17, getProgressScaled(e.getEnergyStored(),e.getMaxEnergyStored(), 52), energyCellType));
        getMenu().storage.getStorage(Storages.FLUID).ifPresent((g)-> ProgressElementRenderUtil.renderFluidTank(poseStack,this,relX() + fluidTankPos[0], relY() + fluidTankPos[1], getProgressScaled((int) g.getFluidStack().getAmount(), (int) g.getMaxFluid(), fluidTankType.sizeY), fluidTankType, g.getFluidStack(), true));
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
        poseStack.translate(i, j, 500.0F + this.getBlitOffset());
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
        itemRenderer.render(stack, ItemTransforms.TransformType.NONE, false, poseStack2, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
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
