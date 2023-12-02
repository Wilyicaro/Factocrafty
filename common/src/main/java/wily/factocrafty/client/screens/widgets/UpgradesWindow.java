package wily.factocrafty.client.screens.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.client.screens.FactocraftyStorageScreen;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.network.FactocraftySyncIntegerBearerPacket;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;

import java.util.List;

public class UpgradesWindow extends SlotsWindow{

    int blockedUpgradeChanges = -1;
    public UpgradesWindow(AbstractDrawableButton<?> button, int x, int y, FactocraftyStorageScreen<? extends FactocraftyMenuBlockEntity> parent, int[] slots) {
        super(button, 34,87, x, y, 222, 0, parent,slots);
    }


    private int getUpgradeY(int index){
        return 38 + 10*index;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        parent.getMenu().be.storedUpgrades.forEach((item)-> {
            int index = parent.getMenu().be.storedUpgrades.indexOf(item);
            if (IFactoryDrawableType.getMouseLimit(d,e, getX() + 11,getY() + getUpgradeY(index),14,14) && parent.getMenu().be.selectedUpgrade.get() == index){
                blockedUpgradeChanges = blockedUpgradeChanges == index ? -1 : index;
                playDownSound(1.4F);
            }
        });
        return super.mouseClicked(d, e, i);
    }

    @Override
    public void renderToolTip(GuiGraphics graphics, int i, int j) {
        super.renderToolTip(graphics, i, j);
        if (parent.getMenu().be.selectedUpgrade.get() >= 0 && parent.getMenu().be.storedUpgrades.size() > parent.getMenu().be.selectedUpgrade.get() && blockedUpgradeChanges != parent.getMenu().be.selectedUpgrade.get()) {
            ItemStack stack = parent.getMenu().be.storedUpgrades.get(parent.getMenu().be.selectedUpgrade.get());
            graphics.renderComponentTooltip(font,List.of(stack.getHoverName(), Component.translatable( "gui.factocrafty.window.upgrade.efficiency" , parent.getMenu().be.storedUpgrades.getUpgradeEfficiency(((FactocraftyUpgradeItem)stack.getItem()).upgradeType) * 100 + "%")), i, j);
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        graphics.pose().pushPose();
        graphics.pose().translate(getX(), getY(),0F);
        graphics.pose().scale(0.9F, 0.9F, 1F);
        if (blockedUpgradeChanges >= parent.getMenu().be.storedUpgrades.size()) blockedUpgradeChanges = -1;
        parent.getMenu().be.storedUpgrades.forEach((item)-> {
            int index = parent.getMenu().be.storedUpgrades.indexOf(item);
            graphics.renderFakeItem(item, 11,  getUpgradeY(index));
            if (IFactoryDrawableType.getMouseLimit(i,j, getX() + 11,getY() + getUpgradeY(index),14,14) && !IFactoryDrawableType.getMouseLimit(i,j, getX() + 11,getY() + getUpgradeY(index + 1),14,14)  ){
                if (parent.getMenu().be.selectedUpgrade.get() != index) Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(parent.getMenu().be.getBlockPos(), index, parent.getMenu().be.additionalSyncInt.indexOf(parent.getMenu().be.selectedUpgrade)));
            }
        });
        if (!IFactoryDrawableType.getMouseLimit(i,j, getX() + 11,getY() + 38,14,44) && parent.getMenu().be.selectedUpgrade.get() != blockedUpgradeChanges) Factocrafty.NETWORK.sendToServer(new FactocraftySyncIntegerBearerPacket(parent.getMenu().be.getBlockPos(), blockedUpgradeChanges, parent.getMenu().be.additionalSyncInt.indexOf(parent.getMenu().be.selectedUpgrade)));
        if (parent.getMenu().be.selectedUpgrade.get() >= 0 && blockedUpgradeChanges != parent.getMenu().be.selectedUpgrade.get())
            graphics.fillGradient(RenderType.guiOverlay(), 13,  getUpgradeY(parent.getMenu().be.selectedUpgrade.get()) + 2, 25, getUpgradeY(parent.getMenu().be.selectedUpgrade.get()) + 14, -2130706433, -2130706433, 0);
        graphics.pose().popPose();
    }

}
