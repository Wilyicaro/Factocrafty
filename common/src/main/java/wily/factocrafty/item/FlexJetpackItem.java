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
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IFluidItem;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.StorageStringUtil;

import java.util.List;

public class FlexJetpackItem extends JetpackItem implements IFluidItem<IPlatformFluidHandler> {


    public FlexJetpackItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(StorageStringUtil.getFluidTooltip("tooltip.factocrafty.fluid_stored", getFluidStorage(itemStack)));
    }

    @Override
    protected boolean canLaunchJetpack(ItemStack stack) {
        return getFluidStorage(stack).getFluidStack().getAmount() >= 1;
    }

    @Override
    protected ItemStack consumeFuel(ItemStack stack) {
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
    public IPlatformFluidHandler getFluidStorage(ItemStack stack) {
        return FactoryAPIPlatform.getFluidItemHandlerApi(2*FluidStack.bucketAmount(), stack, (a) -> a.getFluid().isSame(FactocraftyFluids.GASOLINE.get()), TransportState.EXTRACT_INSERT);
    }
}
