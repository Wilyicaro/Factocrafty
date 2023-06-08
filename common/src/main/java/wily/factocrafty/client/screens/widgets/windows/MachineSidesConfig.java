package wily.factocrafty.client.screens.widgets.windows;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.network.FactocraftyStateButtonPacket;
import wily.factoryapi.base.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static wily.factocrafty.util.DirectionUtil.nearestRotation;
import static wily.factocrafty.util.DirectionUtil.rotationCyclic;

public class MachineSidesConfig extends FactocraftyScreenWindow{
    Minecraft minecraft = Minecraft.getInstance();

    boolean blockSidesDragged = false;
    float configBlockRotateX = 30;
    float configBlockRotateY = -135;
    // ItemSides: 0, EnergySides: 1, FluidSides: 2
    int sideConfigState = 0;

    FactocraftyProcessBlockEntity be = parent.getMenu().be;
    public MachineSidesConfig(FactocraftyConfigWidget config, int x, int y, FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> screen) {
        super(config, 130, 90, x, y, 0, 0, screen);
        BlockState state = screen.getMenu().be.getBlockState();
        if (state.getBlock() instanceof FactocraftyMachineBlock mb) configBlockRotateY = 45 + 90 * state.getValue(mb.getFacingProperty()).get2DDataValue();
    }

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(ButtonTypes.LARGE,getX() + 7,getY() + 11,new EasyIcon(10,10,sideConfigState, 246), (b)-> sideConfigState = getNextSide(), getSidesConfigTooltip()), false);
        map.put(byButtonColor(ButtonTypes.LARGE,getX() + 7,getY() + 30,getSideIdentifier().getColor(),(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStateButtonPacket(be.getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide(), sideConfigState == 1 ? 0 : ((ISideType) getSideType().get(selectedDirection)).nextSlotIndex(getSlotsIdentifiers()))), getSideIdentifier().getTooltip((sideConfigState == 0 ? "slot" : sideConfigState < 2 ? "cell":"tank"))), false);
        map.put(byButtonType(ButtonTypes.LARGE,getX() + 7,getY() + 49,new EasyIcon(10,10,getStateSide(getSideType(),selectedDirection).ordinal() + 4, 246), (b)-> Factocrafty.NETWORK.sendToServer(new FactocraftyStateButtonPacket(be.getBlockPos(),sideConfigState,selectedDirection.ordinal(),getStateSide().nextStateSide(), sideConfigState == 1 ? 0 :((ISideType) getSideType().get(selectedDirection)).getSlotIndex(getSlotsIdentifiers()))),getStateSide().getTooltip()), false);
        return super.addButtons(map);
    }
    public static TransportState getStateSide(Map<Direction, ?> map, Direction d){
        if (map.get(d) instanceof ItemSide item) return item.transportState;
        else if (map.get(d) instanceof FluidSide fluid) return fluid.transportState;
        else return (TransportState) map.get(d);
    }
    public TransportState getStateSide(){return getStateSide(getSideType(),selectedDirection);}

    public Component getSidesConfigTooltip(){
        return Component.translatable("tooltip.factocrafty.config." + (sideConfigState == 0 ? "item" : sideConfigState < 2 ? "energy":"fluid"));
    }

    protected List<Integer> sidesType(){
        FactocraftyProcessBlockEntity be = parent.getMenu().be;
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
    public static Map<Direction, ?> getSideType(FactocraftyProcessBlockEntity be, int sideConfigState){
        return sideConfigState == 0 ? be.itemSides : sideConfigState == 1 ? be.energySides : sideConfigState == 2 ? be.fluidSides : null;
    }
    protected Map<Direction, ?> getSideType(){ return getSideType(be,sideConfigState);}
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
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (isVisible() && (i == 0 || i == 1) && (clickedSideConfig(d,e) || blockSidesDragged) && !dragging) {
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
    public void renderBg(PoseStack poseStack, int i, int j) {
        super.renderBg(poseStack,i,j);
        selectedDirection = nearestRotation(configBlockRotateX, configBlockRotateY,true);
        parent.renderGuiBlock(be,be.getBlockState(),getX() + width /2 - 8 ,getY() + 28, 2F,2F, configBlockRotateX, configBlockRotateY);
        //blit(poseStack, i, j, 0, 30, 30, baked.getQuads(state, selectedDirection, parent.getMenu().player.level.random).get(0).getSprite());
        minecraft.font.draw(poseStack, Component.translatable("tooltip.factocrafty.config.direction", Component.translatable("tooltip.factocrafty.config." +selectedDirection.getName()).getString()) ,getX() + 8,getY() + height - 16 , 4210752);
    }


    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }



}
