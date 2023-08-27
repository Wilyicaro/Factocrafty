package wily.factocrafty.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;

import java.util.List;

public class FactocraftyUpgradeItem extends Item {
    private String tooltipName;
    private Component tooltip;
    public UpgradeType upgradeType;

    public FactocraftyUpgradeItem(Properties properties, UpgradeType Type) {
        super(properties);
        upgradeType = Type;
        this.tooltipName = upgradeType.getName();
    }
    public FactocraftyUpgradeItem(Properties properties, UpgradeType Type, Component tooltip ) {
        super(properties);
        upgradeType = Type;
        this.tooltip = tooltip;

    }
    public boolean isEnabled(){
        return true;
    }

    public boolean isSameType(FactocraftyUpgradeItem upg){
        return upgradeType == upg.upgradeType;
    }
    public boolean isValid(FactocraftyStorageBlockEntity blockEntity){
        return isEnabled() && blockEntity.storedUpgrades.stream().map((i-> (FactocraftyUpgradeItem)i.getItem())).allMatch(this::isUpgradeCompatibleWith);
    }

    public boolean isUpgradeCompatibleWith(FactocraftyUpgradeItem upg){
        return true;
    }
    public Component getDisabledMessage(){
        return Component.translatable("tooltip." + Factocrafty.MOD_ID + ".upgrade.disabled").setStyle(Style.EMPTY.applyFormat((ChatFormatting.RED)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!isEnabled())
            tooltip.add(getDisabledMessage());
        tooltip.add(Component.translatable("tooltip." + Factocrafty.MOD_ID + ".upgrade_right_click").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GOLD).withItalic(true)));
        if (this.tooltip != null || tooltipName != null)
            tooltip.add( this.tooltip == null ? Component.translatable("tooltip." + Factocrafty.MOD_ID + ".upgrade." + this.tooltipName).setStyle(Style.EMPTY.applyFormat((ChatFormatting.GRAY))) : this.tooltip);

    }
}
