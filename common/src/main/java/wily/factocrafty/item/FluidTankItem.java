package wily.factocrafty.item;

import dev.architectury.fluid.FluidStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.FactocraftyExpectPlatform;
import wily.factocrafty.block.storage.fluid.FactocraftyFluidTankBlock;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class FluidTankItem extends BlockItem implements BucketLikeItem {
    public FluidTankItem(FactocraftyFluidTankBlock block, Properties properties) {
        super(block, properties);
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(StorageStringUtil.getFluidTooltip("tooltip.factory_api.fluid_stored", getFluidStorage(itemStack),false));
        
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        return BucketLikeItem.super.use(level, player, interactionHand);
    }

    @Override
    public FluidStorageBuilder getFluidStorageBuilder(ItemStack stack) {
        FactoryCapacityTiers capacityTier = ((FactocraftyFluidTankBlock)getBlock()).capacityTier;
        long capacity = capacityTier.capacityMultiplier * 16 * FluidStack.bucketAmount();
        //if (stack != null && stack.hasTag()){
        //    FactocraftyFluidTankBlockEntity be = FactocraftyBlockEntityWLRenderer.getFluidTank(Direction.UP,capacityTier);
        //    be.load(be.getUpdateTag().merge( stack.getTag().getCompound(BLOCK_ENTITY_TAG)));
        //    capacity = be.fluidTank.getMaxFluid();
        //}
        return new FluidStorageBuilder(capacity, (a) -> !FactocraftyExpectPlatform.isGas(a.getFluid()), TransportState.EXTRACT_INSERT);
    }
    @Override
    public IPlatformFluidHandler getFluidStorage(ItemStack stack) {
        return FactoryAPIPlatform.getFluidItemHandlerApi(stack,getFluidStorageBuilder(stack));
    }
}
