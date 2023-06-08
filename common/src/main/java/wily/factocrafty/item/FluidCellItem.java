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

public class FluidCellItem extends Item implements BucketLikeItem {
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
        list.add(StorageStringUtil.getFluidTooltip("tooltip.factory_api.fluid_stored", getFluidStorage(itemStack)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return BucketLikeItem.super.use(level, player, interactionHand);
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
    public FluidStorageBuilder getFluidStorageBuilder(ItemStack stack) {
        return new FluidStorageBuilder(capacity, (a) -> true, TransportState.EXTRACT_INSERT);
    }
}
