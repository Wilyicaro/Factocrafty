package wily.factocrafty.client.screens.widgets.windows;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.client.screens.FactocraftyMachineScreen;
import wily.factocrafty.client.screens.widgets.FactocraftyConfigWidget;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.network.FactocraftySyncSelectedUpgradePacket;
import wily.factoryapi.base.*;

import java.util.List;

public class UpgradesWindow extends SlotsWindow{

    int blockedUpgradeChanges = -1;
    public UpgradesWindow(FactocraftyConfigWidget config, int x, int y, FactocraftyMachineScreen<? extends FactocraftyProcessBlockEntity> parent, int[] slots) {
        super(config, 34,87, x, y, 222, 0, parent,slots);
    }


    private int getUpgradeY(int index){
        return 38 + 10*index;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        parent.getMenu().be.storedUpgrades.forEach((item)-> {
            int index = parent.getMenu().be.storedUpgrades.indexOf(item);
            if (IFactoryDrawableType.getMouseLimit(d,e, getX() + 11,getY() + getUpgradeY(index),14,14) && parent.getMenu().be.selectedUpgrade == index){
                blockedUpgradeChanges = blockedUpgradeChanges == index ? -1 : index;
                playDownSound(1.4F);
            }
        });
        return super.mouseClicked(d, e, i);
    }

    @Override
    public void renderToolTip(PoseStack poseStack, int i, int j) {
        super.renderToolTip(poseStack, i, j);
        if (parent.getMenu().be.selectedUpgrade >= 0 && blockedUpgradeChanges != parent.getMenu().be.selectedUpgrade) {
            ItemStack stack = parent.getMenu().be.storedUpgrades.get(parent.getMenu().be.selectedUpgrade);
            parent.renderComponentTooltip(poseStack, List.of(stack.getHoverName(), Component.translatable( "gui.factocrafty.window.upgrade.efficiency" , parent.getMenu().be.storedUpgrades.getUpgradeEfficiency(((FactocraftyUpgradeItem)stack.getItem()).upgradeType) * 100 + "%")), i, j);
        }
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        poseStack.pushPose();
        poseStack.translate(getX(), getY(),0F);
        poseStack.scale(0.9F, 0.9F, 1F);
        if (blockedUpgradeChanges >= parent.getMenu().be.storedUpgrades.size()) blockedUpgradeChanges = -1;
        parent.getMenu().be.storedUpgrades.forEach((item)-> {
            int index = parent.getMenu().be.storedUpgrades.indexOf(item);
            itemRenderer.renderGuiItem(poseStack,item, 11,  getUpgradeY(index));
            if (IFactoryDrawableType.getMouseLimit(i,j, getX() + 11,getY() + getUpgradeY(index),14,14)){
                if (parent.getMenu().be.selectedUpgrade != index) Factocrafty.NETWORK.sendToServer(new FactocraftySyncSelectedUpgradePacket(parent.getMenu().be.getBlockPos(), parent.getMenu().be.selectedUpgrade = index));
            }
        });
        if (!IFactoryDrawableType.getMouseLimit(i,j, getX() + 11,getY() + 38,14,44) && parent.getMenu().be.selectedUpgrade != blockedUpgradeChanges) Factocrafty.NETWORK.sendToServer(new FactocraftySyncSelectedUpgradePacket(parent.getMenu().be.getBlockPos(), parent.getMenu().be.selectedUpgrade = blockedUpgradeChanges));;
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        if (parent.getMenu().be.selectedUpgrade >= 0 && blockedUpgradeChanges != parent.getMenu().be.selectedUpgrade)
            fillGradient(poseStack, 13,  getUpgradeY(parent.getMenu().be.selectedUpgrade) + 2, 25, getUpgradeY(parent.getMenu().be.selectedUpgrade) + 14, -2130706433, -2130706433, 0);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }

}
