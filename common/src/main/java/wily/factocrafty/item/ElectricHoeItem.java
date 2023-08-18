package wily.factocrafty.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import wily.factoryapi.base.CraftyTransaction;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;
import wily.factoryapi.base.TransportState;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.world.item.HoeItem.TILLABLES;

public class ElectricHoeItem extends EnergyDiggerItem {



    public ElectricHoeItem(Tier tier, int f, float g, FactoryCapacityTiers energyTier, Properties properties) {
        super(tier,f, g, BlockTags.MINEABLE_WITH_HOE, energyTier, TransportState.INSERT, properties);

    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockPos blockPos;
        Level level = useOnContext.getLevel();
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = TILLABLES.get(level.getBlockState(blockPos = useOnContext.getClickedPos()).getBlock());
        if (pair == null) {
            return InteractionResult.PASS;
        }
        Predicate<UseOnContext> predicate = pair.getFirst();
        Consumer<UseOnContext> consumer = pair.getSecond();
        ItemStack hoe = useOnContext.getItemInHand();
        if (predicate.test(useOnContext) && isActivated(hoe)) {
            Player player2 = useOnContext.getPlayer();
            level.playSound(player2, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!level.isClientSide) {
                consumer.accept(useOnContext);
                if (player2 != null) {
                    getEnergyStorage(hoe).consumeEnergy(new CraftyTransaction(1, energyTier),false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

}
