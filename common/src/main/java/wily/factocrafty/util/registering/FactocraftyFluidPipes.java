package wily.factocrafty.util.registering;


import net.minecraft.resources.ResourceLocation;
import wily.factocrafty.block.transport.fluid.FluidPipeBlock;
import wily.factocrafty.block.transport.fluid.FluidPipeBlockEntity;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.TransportState;

public enum FactocraftyFluidPipes implements IFactocraftyConduit<FactocraftyFluidPipes, FluidPipeBlock, FluidPipeBlockEntity> {
    BASIC_FLUID_PIPE(FactoryCapacityTiers.BASIC, Shape.FLUID_PIPE),ADVANCED_FLUID_PIPE(FactoryCapacityTiers.ADVANCED, Shape.FLUID_PIPE), HIGH_FLUID_PIPE(FactoryCapacityTiers.HIGH, Shape.FLUID_PIPE),
    ULTIMATE_FLUID_PIPE(FactoryCapacityTiers.ULTIMATE, Shape.LARGE_FLUID_PIPE),QUANTUM_FLUID_PIPE(FactoryCapacityTiers.QUANTUM, Shape.LARGE_FLUID_PIPE);

    public final FactoryCapacityTiers capacityTier;
    public final Shape pipeShape;

    FactocraftyFluidPipes(FactoryCapacityTiers capacityTier, Shape pipeShape){
        this.capacityTier = capacityTier;
        this.pipeShape = pipeShape;
    }

    @Override
    public Storages.Storage<?> getTransferenceStorage() {
        return Storages.FLUID;
    }

    @Override
    public ResourceLocation getSideModelLocation(TransportState state) {
        return new ResourceLocation("factocrafty:block/transport/fluid/" + getName() + "_side" +(state == TransportState.EXTRACT_INSERT ? "" : "_" + state.toString()));
    }

    @Override
    public Shape getConduitShape() {
        return pipeShape;
    }
    @Override
    public FactoryCapacityTiers getCapacityTier() {
        return capacityTier;
    }


}
