package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;

import java.util.Map;

import static wily.factocrafty.util.ScreenUtil.renderScaled;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class RefinerScreen extends ChangeableInputMachineScreen {


    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/refiner.png");

    public RefinerScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    RefinerBlockEntity rBe = (RefinerBlockEntity) getMenu().be;

    public ResourceLocation GUI() {return BACKGROUND_LOCATION;}

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(ButtonTypes.SMALL, relX() + 85,relY() + 26,new EasyIcon(7,7,3,239),(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(rBe.getBlockPos(),Math.min(rBe.recipeIndex.get() + 1,rBe.recipeSize.get() - 1),rBe.additionalSyncInt.indexOf(rBe.recipeIndex))),Component.literal("Heat up")),false);
        map.put(byButtonType(ButtonTypes.SMALL, relX() + 85,relY() + 48,new EasyIcon(7,7,4,239),(b)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(rBe.getBlockPos(),Math.max(rBe.recipeIndex.get() - 1,0),rBe.additionalSyncInt.indexOf(rBe.recipeIndex))),Component.literal("Heat down")),false);
        return super.addButtons(map);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int i, int j) {
        super.renderLabels(poseStack,i,j);
        String s = I18n.get("gui.factocrafty.heat",rBe.recipeHeat.get());
        renderScaled(poseStack,s,  (imageWidth - (font.width(s) / 2)) / 2 + 4, 18,0.5F,0xFF9933,true);
    }

    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        super.renderStorageTooltips(poseStack,i,j);
        if (FactocraftyDrawables.FLUID_TANK.inMouseLimit(i,j,   138,  17)) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.resultTank),i, j);
    }
    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        FactocraftyDrawables.FLUID_TANK.drawAsFluidTank(poseStack,relX() + 138, relY() + 17, getProgressScaled((int) rBe.resultTank.getFluidStack().getAmount(), (int) rBe.resultTank.getMaxFluid(), 52), rBe.resultTank.getFluidStack(), true);
    }
}
