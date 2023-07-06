package wily.factocrafty.item;

import dev.architectury.fluid.FluidStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.IFluidItem;
import wily.factoryapi.base.IPlatformFluidHandler;

import static net.minecraft.world.item.Item.getPlayerPOVHitResult;

public interface BucketLikeItem extends IFluidItem<IPlatformFluidHandler> {

    default InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level,player, ClipContext.Fluid.SOURCE_ONLY);
        BlockState blockState = level.getBlockState(hitResult.getBlockPos());
        if (getFluidStorage(stack).getTotalSpace() >= FluidStack.bucketAmount() && level.mayInteract(player, hitResult.getBlockPos()) && blockState.getBlock() instanceof BucketPickup pick){
            ItemContainerUtil.fillItem(FluidStack.create(((LiquidBlock)blockState.getBlock()).arch$getFluid(), FluidStack.bucketAmount()), player, hand);
            if (!player.isCreative()) level.setBlock(hitResult.getBlockPos(), Blocks.AIR.defaultBlockState(), 11);
           // pick.getPickupSound().ifPresent((p)-> player.playSound(p,1.0F,1.0F));
            if (this instanceof Item i)player.awardStat(Stats.ITEM_USED.get(i));
            return InteractionResultHolder.sidedSuccess( player.getItemInHand(hand), level.isClientSide());
        }
        return InteractionResultHolder.fail(stack);
    }
}
