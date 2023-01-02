package wily.factocrafty.forge.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.item.FluidCellItem;
import wily.factoryapi.forge.base.ForgeItemFluidHandler;

import java.util.function.Consumer;

@Mixin(FluidCellItem.class)
public class AddFluidItemCapability extends Item{


    public AddFluidItemCapability(Properties arg) {
        super(arg);



    }


    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        FluidCellItem cell = ((FluidCellItem)(Object)this);
        return (ForgeItemFluidHandler)cell.getFluidStorage(stack);
    }
}
