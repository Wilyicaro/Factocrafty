package wily.factocrafty.client.screens.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.inventory.IFactoryContainerMenu;
import wily.factocrafty.network.FactocraftyStorageSidesPacket;
import wily.factoryapi.base.*;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.FactoryDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;
import wily.factoryapi.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import static wily.factoryapi.util.DirectionUtil.nearestRotation;
import static wily.factoryapi.util.DirectionUtil.rotationCyclic;

public class MachineSidesConfig extends FactocraftyScreenWindow<AbstractContainerScreen<? extends IFactoryContainerMenu<?>>> {

    boolean blockSidesDragged = false;
    float configBlockRotateX = 30;
    float configBlockRotateY = -135;
    // ItemSides: 0, EnergySides: 1, FluidSides: 2
    int sideConfigState = 0;

    IFactoryExpandedStorage be = parent.getMenu().getBlockEntity();
    public MachineSidesConfig(AbstractDrawableButton<?> button, int x, int y, FactocraftyStorageScreen<? extends IFactoryStorage> screen) {
        super(button, FactocraftyDrawables.MACHINE_SIDES_CONFIG.createStatic(x,y), screen);
        BlockState state = screen.getMenu().be.getBlockState();
        if (state.getBlock() instanceof FactocraftyMachineBlock mb) configBlockRotateY = 45 + 90 * state.getValue(mb.getFacingProperty()).get2DDataValue();
    }

    @Override
    public List<Renderable> getNestedRenderables() {
        List<Renderable> list = super.getNestedRenderables();
        list.add(new FactoryDrawableButton(getX() + 7,getY() + 11,FactocraftyDrawables.LARGE_BUTTON).icon(FactocraftyDrawables.getButtonIcon(sideConfigState)).tooltip(getSidesConfigTooltip()).onPress((b, i)-> sideConfigState = getNextSide()));
        list.add(new FactoryDrawableButton(getX() + 7,getY() + 30,FactocraftyDrawables.LARGE_BUTTON).color(getSideIdentifier().getColor()).tooltip(getSideIdentifier().getTooltip((sideConfigState == 0 ? "slot" : sideConfigState < 2 ? "cell":"tank"))).onPress((b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStorageSidesPacket(parent.getMenu().getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide(), sideConfigState == 1 ? 0 : getSideType().get(selectedDirection).nextSlotIndex(getSlotsIdentifiers())))));
        list.add(new FactoryDrawableButton(getX() + 7,getY() + 49, FactocraftyDrawables.LARGE_BUTTON).icon(FactocraftyDrawables.getButtonIcon(getSideType().getTransport(selectedDirection).ordinal() + 4)).tooltip(getStateSide().getTooltip()).onPress( (b,i)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStorageSidesPacket(parent.getMenu().getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide().next(), sideConfigState == 1 ? 0 :getSideType().get(selectedDirection).getSlotIndex(getSlotsIdentifiers())))));
        list.add(new FactoryDrawableButton(getX() + width - 18,getY() + 54, FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(1)).tooltip(Component.translatable("tooltip.factocrafty.config.reset")).onPress((b,i)-> {for (Direction d : Direction.values()) Factocrafty.NETWORK.sendToServer(new FactocraftyStorageSidesPacket(parent.getMenu().getBlockPos(), sideConfigState, d.ordinal(), TransportState.NONE, sideConfigState == 1 ? 0 :getSideType().get(selectedDirection).getSlotIndex(getSlotsIdentifiers())));}));
        return list;
    }
    public TransportState getStateSide(){return getSideType().getTransport(selectedDirection);}

    public Component getSidesConfigTooltip(){
        return Component.translatable("tooltip.factocrafty.config." + (sideConfigState == 0 ? "item" : sideConfigState < 2 ? "energy":"fluid"));
    }

    protected List<Integer> sidesType(){
        List<Integer> list = new ArrayList<>();
        be.getStorageSides(Storages.ITEM).ifPresent((i)-> list.add(0));
        be.getStorageSides(Storages.CRAFTY_ENERGY).ifPresent((i)-> list.add(1));
        be.getStorageSides(Storages.FLUID).ifPresent((i)-> list.add(2));
        return list;
    }

    protected int getNextSide(){
        int next = sidesType().indexOf(sideConfigState) + 1;
        return  sidesType().get(next < sidesType().size() ?  next : 0);
    }
    public static SideList<TransportSide> getSideType(IFactoryStorage be, int sideConfigState){
        return sideConfigState == 0 ? be.getStorageSides(Storages.ITEM).get() : sideConfigState == 1 ? be.getStorageSides(Storages.CRAFTY_ENERGY).get() : sideConfigState == 2 ? be.getStorageSides(Storages.FLUID).get() : null;
    }
    protected SideList<TransportSide> getSideType(){ return getSideType(be,sideConfigState);}
    protected List<SlotsIdentifier> getSlotsIdentifiers(){
        return sideConfigState == 0 ? be.getItemSlotsIdentifiers() : sideConfigState == 1 ? List.of(SlotsIdentifier.ENERGY) : sideConfigState == 2 ? be.getFluidSlotsIdentifiers() : null;
    }
    protected SlotsIdentifier getSideIdentifier() {
        return getSideType().get(selectedDirection).identifier();
    }

    @Override
    public float getBlitOffset() {
        return 500F;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (isVisible() && (i == 0 || i == 1) && (clickedSideConfig(d,e) || blockSidesDragged) && !dragging && isFocused()) {
            configBlockRotateY += (float) (((d - (getX() + width/2F)) / (getY() + width/2F)) * 50);
            configBlockRotateX += (float) (((e - (getY() + height/2F)) / (getY() + height/2F)) * 50);
            configBlockRotateX = rotationCyclic(configBlockRotateX);
            configBlockRotateY = rotationCyclic(configBlockRotateY);
            blockSidesDragged = true;
            updateActualMouse(d,e);
            return true;
        }

        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        blockSidesDragged = false;
        return super.mouseReleased(d, e, i);
    }

    protected boolean clickedSideConfig(double mouseX, double mouseY){
        return IFactoryDrawableType.getMouseLimit(mouseX,mouseY,getX() + 40, getY() + 12, 50,50);
    }

    Direction selectedDirection = Direction.NORTH;



    @Override
    public void renderBg(GuiGraphics graphics, int i, int j,float f) {
        super.renderBg(graphics,i,j,f);
        selectedDirection = nearestRotation(configBlockRotateX, configBlockRotateY,true);
        ScreenUtil.renderGuiBlock(graphics,parent.getMenu().getBlockEntity(), parent.getMenu().getBlockState(),getX() + width /2 - 8 ,getY() + 28, 2F,2F, configBlockRotateX, configBlockRotateY);
        //blit(poseStack, i, j, 0, 30, 30, baked.getQuads(state, selectedDirection, parent.getMenu().player.level.random).get(0).getSprite());
        ScreenUtil.drawString(graphics.pose(),I18n.get("tooltip.factocrafty.config.direction", I18n.get("tooltip.factocrafty.config." + selectedDirection.getName())) ,getX() + 8,getY() + height - 16 , 4210752,false);
    }


    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }



}
