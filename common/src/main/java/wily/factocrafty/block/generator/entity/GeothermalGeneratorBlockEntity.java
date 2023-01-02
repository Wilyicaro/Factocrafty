package wily.factocrafty.block.generator.entity;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import wily.factocrafty.init.Registration;
import wily.factocrafty.util.registering.FactocraftyMenus;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;

import java.util.List;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntity {

    public GeothermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.GEOTHERMAL_GENERATOR,Registration.GEOTHERMAL_GENERATOR_BLOCK_ENTITY.get(), blockPos, blockState);
        replaceSidedStorage(BlockSide.LEFT,fluidSides, new FluidSide(lavaTank, TransportState.EXTRACT_INSERT));
    }

    public GeothermalGeneratorBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(FactocraftyMenus.GEOTHERMAL_GENERATOR,blockEntityType, blockPos, blockState);
    }

    public IPlatformFluidHandler lavaTank = FactoryAPIPlatform.getFluidHandlerApi(FluidStack.bucketAmount() * 6, this, f -> FuelRegistry.get(new ItemStack(f.getFluid().getBucket())) > 0, SlotsIdentifier.LAVA,TransportState.EXTRACT_INSERT);
    @Override
    public void addSlots(NonNullList<FactoryItemSlot> slots, Player player) {
        super.addSlots(slots, player);
        slots.set(0,new FactoryItemSlot(this.inventory, SlotsIdentifier.ORANGE,TransportState.EXTRACT_INSERT,0,56,53){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return lavaTank.isFluidValid(0, ItemContainerUtil.getFluid(itemStack)) || fluidTank.isFluidValid(0, ItemContainerUtil.getFluid(itemStack));
            }
        });
    }
    @Override
    protected void consumeFuel(){
        burnTime.setInt(0,200);
        lavaTank.drain(getPlatformFluidConsume(1), false);
    }
    @Override
    protected boolean canConsumeFuel(){
        return lavaTank.getFluidStack().getAmount() >= getPlatformFluidConsume(1);
    }
    public void addTanks(List<IPlatformFluidHandler> list) {
        super.addTanks(list);
        list.add(lavaTank.identifier().differential(),lavaTank);
    }
}
