package wily.factocrafty.block.generator.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.List;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity {

    public GeothermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.GEOTHERMAL_GENERATOR_MENU.get(),Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
        replaceSidedStorage(BlockSide.LEFT,fluidSides, new TransportSide(lavaTank.identifier(), TransportState.EXTRACT_INSERT));
    }


    public IPlatformFluidHandler lavaTank = FactoryAPIPlatform.getFluidHandlerApi(FluidStack.bucketAmount() * 6, this, f -> FuelRegistry.get(new ItemStack(f.getFluid().getBucket())) > 0, SlotsIdentifier.LAVA,TransportState.EXTRACT_INSERT);
    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = super.getSlots(player);
        slots.set(0,new FactocraftyFluidItemSlot(this,0,56,53, SlotsIdentifier.INPUT,TransportState.INSERT){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return lavaTank.isFluidValid(ItemContainerUtil.getFluid(itemStack)) || fluidTank.isFluidValid(ItemContainerUtil.getFluid(itemStack));
            }
        });
        return slots;
    }
    @Override
    protected void consumeFuel(){
        burnTime.first().maxProgress = 200;
        burnTime.first().set(200);
        lavaTank.drain(getPlatformFluidConsume(1), false);
    }
    @Override
    protected boolean canConsumeFuel(){
        return lavaTank.getFluidStack().getAmount() >= getPlatformFluidConsume(1);
    }
    public void addTanks(List<IPlatformFluidHandler<?>> list) {
        super.addTanks(list);
        list.add(lavaTank);
    }
}
