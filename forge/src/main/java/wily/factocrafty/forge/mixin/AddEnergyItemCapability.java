package wily.factocrafty.forge.mixin;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factocrafty.item.EnergyItem;
import wily.factocrafty.item.FluidCellItem;
import wily.factoryapi.forge.base.FactoryCapabilities;
import wily.factoryapi.forge.base.ForgeItemFluidHandler;

@Mixin(EnergyItem.class)
public class AddEnergyItemCapability extends Item{


    public AddEnergyItemCapability(Properties arg) {
        super(arg);


    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        EnergyItem cell = ((EnergyItem)(Object)this);
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                return FactoryCapabilities.ENERGY_CAPABILITY.orEmpty(capability,LazyOptional.of(()->cell.getCraftyEnergy(stack)).cast());
            }
        };
    }
}
