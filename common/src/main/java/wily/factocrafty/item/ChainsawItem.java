package wily.factocrafty.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.TransportState;

public class ChainsawItem extends EnergyDiggerItem {



    public ChainsawItem(Tier tier, int f, float g, FactoryCapacityTiers energyTier, Properties properties) {
        super(tier,f, g, BlockTags.MINEABLE_WITH_AXE, energyTier, TransportState.INSERT, properties);
    }

}
