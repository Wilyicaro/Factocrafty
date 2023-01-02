package wily.factocrafty.item;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.IFluidItem;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class FluidCellItem extends Item implements IFluidItem<IPlatformFluidHandler> {
    public FluidCellItem(Properties properties) {
        super(properties);

    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (getFluidStorage(itemStack).getFluidStack().isEmpty()) return super.getName(itemStack);
        else return Component.translatable("item.factocrafty.fluid_cell_filled",getFluidStorage(itemStack).getFluidStack().getName());
    }

    public long capacity = FluidStack.bucketAmount();

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(StorageStringUtil.getFluidTooltip("tooltip.factocrafty.fluid_stored", getFluidStorage(itemStack)));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level,player, ClipContext.Fluid.SOURCE_ONLY);
        BlockState blockState = level.getBlockState(hitResult.getBlockPos());
        if ( getFluidStorage(stack).getFluidStack().isEmpty() && level.mayInteract(player, hitResult.getBlockPos()) && blockState.getBlock() instanceof BucketPickup pick){
            ItemStack filled = stack.copy();
            filled.setCount(1);
            ItemContainerUtil.fillItem(filled,FluidStack.create(((LiquidBlock)blockState.getBlock()).arch$getFluid(), FluidStack.bucketAmount()));
            if (stack.getCount() > 1){
                stack.shrink(1);
                player.setItemInHand(hand, stack);
                player.addItem(filled);
            } else player.setItemInHand(hand,filled);
            if (!player.isCreative()) level.setBlock(hitResult.getBlockPos(), Blocks.AIR.defaultBlockState(), 11);
            pick.getPickupSound().ifPresent((p)-> player.playSound(p,1.0F,1.0F));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
            return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {return getFluidStorage(itemStack).getTotalSpace() > 0 && !getFluidStorage(itemStack).getFluidStack().isEmpty();}

    public int getBarWidth(ItemStack itemStack) {
        return Math.round( getFluidStorage(itemStack).getFluidStack().getAmount() * 13.0F / (float)this.getFluidStorage(itemStack).getMaxFluid());
    }

    public int getBarColor(ItemStack itemStack) {
        return FluidStackHooks.getColor( getFluidStorage(itemStack).getFluidStack().getFluid());
    }



    @Override
    public IPlatformFluidHandler getFluidStorage(ItemStack stack) {
        return FactoryAPIPlatform.getFluidItemHandlerApi(capacity, stack, (a) -> true, TransportState.EXTRACT_INSERT);
    }
}
