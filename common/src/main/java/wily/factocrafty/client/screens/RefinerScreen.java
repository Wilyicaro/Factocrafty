package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.FactocraftyProgressType;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.RefinerBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncRefiningTypePacket;
import wily.factoryapi.base.Storages;
import wily.factoryapi.util.ProgressElementRenderUtil;

import java.util.Map;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class RefinerScreen extends BasicMachineScreen {



    public RefinerScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        hasAdditionalResultSlot = false;
    }
    RefinerBlockEntity rBe = (RefinerBlockEntity) getMenu().be;

    public ResourceLocation GUI() {return new ResourceLocation(Factocrafty.MOD_ID , "textures/gui/container/refiner.png");}

    @Override
    public Map<EasyButton, Boolean> addButtons(Map<EasyButton, Boolean> map) {
        map.put(byButtonType(ButtonTypes.LARGE, relX() - 18, relY() + 102, new EasyIcon(10,10,rBe.refiningType == RefinerBlockEntity.RefiningType.ITEM ? 8 : 2,246),(b)-> Factocrafty.NETWORK.sendToServer( new FactocraftySyncRefiningTypePacket( rBe.getBlockPos(),rBe.refiningType = RefinerBlockEntity.RefiningType.values()[rBe.refiningType.ordinal() >= 1 ? 0: 1] )), Component.translatable("tooltip.factocrafty.config.refining." + rBe.refiningType.name().toLowerCase())),false);
        return map;
    }

    @Override
    protected void init() {
        super.init();
        addWidget(new FactocraftyInfoWidget(relX() - 20,  relY() + 100,218 , 20,Component.translatable("tooltip.factocrafty.config.refining"), null, this::renderTooltip));
    }

    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        if (energyCellType.inMouseLimit(i,j,  energyCellPosX,  17)) renderComponentTooltip(poseStack, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", rBe.energyStorage),i, j);
        else if (fluidTankType.inMouseLimit(i,j,   55,  13) && rBe.refiningType.equals(RefinerBlockEntity.RefiningType.FLUID)) renderTooltip(poseStack, getFluidTooltip("tooltip.factocrafty.fluid_stored", rBe.fluidTank),i, j);
        else if (FactocraftyProgressType.FLUID_TANK.inMouseLimit(i,j,   138,  17)) renderTooltip(poseStack, getFluidTooltip("tooltip.factocrafty.fluid_stored", rBe.resultTank),i, j);
    }
    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        if (rBe.refiningType.isFluid()) {
            blit(poseStack,relX() + 55, relY() + 13,178,107,18,21);
            ProgressElementRenderUtil.renderFluidTank(poseStack, this, relX() + 56, relY() + 14, getProgressScaled((int) getMenu().be.fluidTank.getFluidStack().getAmount(), (int) getMenu().be.fluidTank.getMaxFluid(), 19), FactocraftyProgressType.MINI_FLUID_TANK, getMenu().be.fluidTank.getFluidStack(), true);
        } else blit(poseStack,relX() + 55, relY() + 16,178,0,18,18);
        ProgressElementRenderUtil.renderFluidTank(poseStack,this,relX() + 138, relY() + 17, getProgressScaled((int) rBe.resultTank.getFluidStack().getAmount(), (int) rBe.resultTank.getMaxFluid(), 52), FactocraftyProgressType.FLUID_TANK, rBe.resultTank.getFluidStack(), true);
    }
}
