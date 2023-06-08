package wily.factocrafty.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factocrafty.block.machines.entity.ChangeableInputMachineBlockEntity;
import wily.factocrafty.client.screens.widgets.FactocraftyInfoWidget;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyProcessMenu;
import wily.factocrafty.network.FactocraftySyncInputTypePacket;

import java.util.Map;

import static wily.factoryapi.util.StorageStringUtil.getCompleteEnergyTooltip;
import static wily.factoryapi.util.StorageStringUtil.getFluidTooltip;

public class ChangeableInputMachineScreen extends BasicMachineScreen {


    public ChangeableInputMachineScreen(FactocraftyProcessMenu<FactocraftyMachineBlockEntity> abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }
    ChangeableInputMachineBlockEntity rBe = (ChangeableInputMachineBlockEntity) getMenu().be;

    protected String getRecipeTypeName(){
        String s = Registration.RECIPE_TYPES.getRegistrar().getId(rBe.recipeType).getPath();
        return s.contains("_") ? s.split("_")[0] : s;
    }
    @Override
    protected void init() {
        super.init();
        addWidget(new FactocraftyInfoWidget(relX() - 20,  relY() + 100,218 , 20,()->Component.translatable("tooltip.factocrafty.config", rBe.getBlockState().getBlock().getName().getString()), null, this::renderTooltip)).button =
                (x,y)->byButtonType(ButtonTypes.LARGE, x + 2, y + 2, new EasyIcon(10,10,rBe.inputType.isItem() ? 8 : 2,246),(b)-> Factocrafty.NETWORK.sendToServer( new FactocraftySyncInputTypePacket( rBe.getBlockPos(),rBe.inputType = ChangeableInputMachineBlockEntity.InputType.values()[rBe.inputType.ordinal() >= 1 ? 0: 1] )), Component.translatable("tooltip.factocrafty.config.input_type."+ rBe.inputType.getName(), I18n.get("category.factocrafty.recipe." + getRecipeTypeName())));
    }

    @Override
    protected void renderStorageTooltips(PoseStack poseStack, int i, int j) {
        if (energyCellType.inMouseLimit(i,j,  energyCellPosX,  17)) renderComponentTooltip(poseStack, getCompleteEnergyTooltip("tooltip.factory_api.energy_stored", rBe.energyStorage),i, j);
        else if (fluidTankType.inMouseLimit(i,j,   55,  13) && rBe.inputType.isFluid()) renderTooltip(poseStack, getFluidTooltip("tooltip.factory_api.fluid_stored", rBe.fluidTank),i, j);
    }
    @Override
    protected void renderStorageSprites(PoseStack poseStack, int i, int j) {
        super.renderStorageSprites(poseStack, i, j);
        if (rBe.inputType.isFluid()) {
            blit(poseStack,relX() + 55, relY() + 13,178,107,18,21);
            FactocraftyDrawables.MINI_FLUID_TANK.drawAsFluidTank(poseStack, relX() + 56, relY() + 14, getProgressScaled((int) getMenu().be.fluidTank.getFluidStack().getAmount(), (int) getMenu().be.fluidTank.getMaxFluid(), 19), getMenu().be.fluidTank.getFluidStack(), true);
        }
    }
}
