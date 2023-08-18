package wily.factocrafty.client.screens.widgets.windows;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyDrawableButton;
import wily.factocrafty.client.screens.FactocraftyDrawables;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.network.FactocraftyStateButtonPacket;
import wily.factocrafty.util.ScreenUtil;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static wily.factoryapi.util.DirectionUtil.nearestRotation;
import static wily.factoryapi.util.DirectionUtil.rotationCyclic;

public class MachineSidesConfig extends FactocraftyScreenWindow{

    boolean blockSidesDragged = false;
    float configBlockRotateX = 30;
    float configBlockRotateY = -135;
    // ItemSides: 0, EnergySides: 1, FluidSides: 2
    int sideConfigState = 0;

    FactocraftyMenuBlockEntity be = parent.getMenu().be;
    public MachineSidesConfig(FactocraftyConfigWidget config, int x, int y, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> screen) {
        super(config, 130, 90, x, y, 0, 0, screen);
        BlockState state = screen.getMenu().be.getBlockState();
        if (state.getBlock() instanceof FactocraftyMachineBlock mb) configBlockRotateY = 45 + 90 * state.getValue(mb.getFacingProperty()).get2DDataValue();
    }

    @Override
    public List<FactocraftyDrawableButton> addButtons(List<FactocraftyDrawableButton> list) {
        list.add(new FactocraftyDrawableButton(getX() + 7,getY() + 11, (b)-> sideConfigState = getNextSide(), getSidesConfigTooltip(),FactocraftyDrawables.LARGE_BUTTON).icon(FactocraftyDrawables.getButtonIcon(sideConfigState)));
        list.add(new FactocraftyDrawableButton(getX() + 7,getY() + 30,(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStateButtonPacket(be.getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide(), sideConfigState == 1 ? 0 : ((ISideType) getSideType().get(selectedDirection)).nextSlotIndex(getSlotsIdentifiers()))), getSideIdentifier().getTooltip((sideConfigState == 0 ? "slot" : sideConfigState < 2 ? "cell":"tank")),FactocraftyDrawables.LARGE_BUTTON).color(getSideIdentifier().getColor()));
        list.add(new FactocraftyDrawableButton(getX() + 7,getY() + 49, (b)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStateButtonPacket(be.getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide().nextStateSide(), sideConfigState == 1 ? 0 :((ISideType) getSideType().get(selectedDirection)).getSlotIndex(getSlotsIdentifiers()))),getStateSide().getTooltip(), FactocraftyDrawables.LARGE_BUTTON).icon(FactocraftyDrawables.getButtonIcon(getSideType().getTransport(selectedDirection).ordinal() + 4)));
        list.add(new FactocraftyDrawableButton(getX() + width - 18,getY() + 54, (b)-> {for (Direction d : Direction.values()) Factocrafty.NETWORK.sendToServer(new FactocraftyStateButtonPacket(be.getBlockPos(), sideConfigState, d.ordinal(), TransportState.NONE, sideConfigState == 1 ? 0 :((ISideType) getSideType().get(selectedDirection)).getSlotIndex(getSlotsIdentifiers())));}, Component.translatable("tooltip.factocrafty.config.reset"), FactocraftyDrawables.SMALL_BUTTON).icon(FactocraftyDrawables.getSmallButtonIcon(1)));
        return super.addButtons(list);
    }
    public TransportState getStateSide(){return getSideType().getTransport(selectedDirection);}

    public Component getSidesConfigTooltip(){
        return Component.translatable("tooltip.factocrafty.config." + (sideConfigState == 0 ? "item" : sideConfigState < 2 ? "energy":"fluid"));
    }

    protected List<Integer> sidesType(){
        FactocraftyMenuBlockEntity be = parent.getMenu().be;
        List<Integer> list = new ArrayList<>();
        be.itemSides().ifPresent((i)-> list.add(0));
        be.energySides().ifPresent((i)-> list.add(1));
        be.fluidSides().ifPresent((i)-> list.add(2));
        return list;
    }

    protected int getNextSide(){
        int next = sidesType().indexOf(sideConfigState) + 1;
        return  sidesType().get(next < sidesType().size() ?  next : 0);
    }
    public static SideList<?> getSideType(FactocraftyMenuBlockEntity be, int sideConfigState){
        return sideConfigState == 0 ? be.itemSides : sideConfigState == 1 ? be.energySides : sideConfigState == 2 ? be.fluidSides : null;
    }
    protected SideList<?> getSideType(){ return getSideType(be,sideConfigState);}
    protected List<?> getSlotsIdentifiers(){
        if (getSideType().get(selectedDirection) instanceof ISideType<?,?> s)
            if (s instanceof ItemSide) return be.getSlotsIdentifiers();
            else if (s instanceof FluidSide) return be.getTanks();
        return Collections.emptyList();
    }
    protected SlotsIdentifier getSideIdentifier() {
        if (getSideType().get(selectedDirection) instanceof IHasIdentifier iden) return iden.identifier();
        else return SlotsIdentifier.GENERIC;
    }

    @Override
    public float getBlitOffset() {
        return 500F;
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (isVisible() && (i == 0 || i == 1) && (clickedSideConfig(d,e) || blockSidesDragged) && !dragging && isFocused()) {
            configBlockRotateY += ((d - (getX() + (float)width/2)) / (getY() + (float)width/2)) * 50;
            configBlockRotateX += ((e - (getY() + (float)height/2)) / (getY() + (float)height/2)) * 50;
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
    public void renderBg(GuiGraphics graphics, int i, int j) {
        super.renderBg(graphics,i,j);
        selectedDirection = nearestRotation(configBlockRotateX, configBlockRotateY,true);
        parent.renderGuiBlock(graphics,be,be.getBlockState(),getX() + width /2 - 8 ,getY() + 28, 2F,2F, configBlockRotateX, configBlockRotateY);
        //blit(poseStack, i, j, 0, 30, 30, baked.getQuads(state, selectedDirection, parent.getMenu().player.level.random).get(0).getSprite());
        ScreenUtil.drawString(graphics.pose(),I18n.get("tooltip.factocrafty.config.direction", I18n.get("tooltip.factocrafty.config." + selectedDirection.getName())) ,getX() + 8,getY() + height - 16 , 4210752,false);
    }


    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }



}
