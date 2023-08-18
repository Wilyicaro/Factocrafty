package wily.factocrafty.item;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.FluidStackHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.util.registering.FactocraftyFluids;
import wily.factoryapi.base.IFluidHandlerItem;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class FlexJetpackItem extends JetpackItem implements IFluidHandlerItem<IPlatformFluidHandler<?>> {


    public FlexJetpackItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(StorageStringUtil.getFluidTooltip("tooltip.factory_api.fluid_stored", getFluidStorage(itemStack)));
    }

    @Override
    public boolean canLaunchJetpack(ItemStack stack) {
        return getFluidStorage(stack).getFluidStack().getAmount() >= 1;
    }

    @Override
    public ItemStack consumeFuel(ItemStack stack) {
        getFluidStorage(stack).drain(1,false);
        return stack;
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
    public long getCapacity() {
        return 2*FluidStack.bucketAmount();
    }
    @Override
    public boolean isFluidValid(FluidStack fluidStack) {
        return fluidStack.getFluid().isSame(FactocraftyFluids.GASOLINE.get());
    }
}
