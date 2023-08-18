package wily.factocrafty.item;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class DrillItem extends EnergyDiggerItem {

    public DrillItem(Tier tier, int f, float g, FactoryCapacityTiers energyTier, Properties properties) {
        super(tier,f, g, BlockTags.MINEABLE_WITH_PICKAXE, energyTier, TransportState.INSERT, properties);

    }
    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return super.getRarity(itemStack);
    }
}
