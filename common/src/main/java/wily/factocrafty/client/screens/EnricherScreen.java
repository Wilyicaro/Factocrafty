package wily.factocrafty.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.MaterialColor;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.block.machines.entity.EnricherBlockEntity;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncProgressPacket;
import wily.factocrafty.util.ScreenUtil;

import java.util.Map;

import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class EnricherScreen extends ChangeableInputMachineScreen{
    public EnricherScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        imageHeight = 171;
        inventoryLabelY += 7;
    }

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(ButtonTypes.SMALL,relX() + 157,relY() + 71,new EasyIcon(7,7,2,239),(i)-> Factocrafty.NETWORK.sendToServer(new FactocraftySyncProgressPacket(eBe.getBlockPos(), eBe.getProgresses().indexOf(eBe.matterAmount),new int[1],eBe.matterAmount.maxProgress)),Component.translatable("tooltip.factocrafty.config.eject")),false);
        return super.addButtons(map);
    }


    public static ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/enricher.png");
    @Override
    public ResourceLocation GUI() {
        return BACKGROUND_LOCATION;
    }
    EnricherBlockEntity eBe = (EnricherBlockEntity) getMenu().be;
    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        super.renderStorageTooltips(poseStack,i,j);
        if (FactocraftyDrawables.FLUID_TANK.inMouseLimit(i,j,   138,  17)) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.resultTank),i, j);
        if (FactocraftyDrawables.MATTER_PROGRESS.inMouseLimit(i,j,   20,  74)) renderTooltip(poseStack, eBe.getMatterMaterial().isEmpty() ?   eBe.getMatterMaterial().getComponent() : Component.translatable("tooltip.factocrafty.matter", eBe.getMatterMaterial().getComponent().getString(),eBe.matterAmount.get()[0]),i, j);
    }
    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        int c = eBe.getMatterMaterial().getColor().col;
        RenderSystem.setShaderColor(ScreenUtil.getRed(c),ScreenUtil.getGreen(c),ScreenUtil.getBlue(c),1.0F);
        FactocraftyDrawables.MATTER_PROGRESS.drawProgress(poseStack,relX() + 20, relY() + 74, getProgressScaled(eBe.matterAmount.get()[0], eBe.matterAmount.maxProgress,135));
        RenderSystem.setShaderColor(1.0F,1.0F,1.0F,1.0F);
        super.renderStorageSprites(poseStack, i, j);
        FactocraftyDrawables.FLUID_TANK.drawAsFluidTank(poseStack,relX() + 138, relY() + 17, getProgressScaled((int) rBe.resultTank.getFluidStack().getAmount(), (int) rBe.resultTank.getMaxFluid(), 52), rBe.resultTank.getFluidStack(), true);
    }
}
