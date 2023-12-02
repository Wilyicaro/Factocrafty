package wily.factocrafty.block.generator.entity;

import dev.architectury.fluid.FluidStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.FactocraftyMachineBlock;
import wily.factocrafty.block.entity.IMultiBlockShape;
import wily.factocrafty.init.Registration;
import wily.factocrafty.inventory.FactocraftyCYItemSlot;
import wily.factocrafty.inventory.FactocraftyFluidItemSlot;
import wily.factocrafty.inventory.FactocraftySlotWrapper;
import wily.factocrafty.tag.Blocks;
import wily.factocrafty.tag.Items;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static wily.factoryapi.util.StorageUtil.transferEnergyTo;

public class NuclearReactorBlockEntity extends GeneratorBlockEntity implements IMultiBlockShape {
    public static SlotsIdentifier PRINCIPAL_CHAMBER = new SlotsIdentifier(ChatFormatting.GREEN,"principal_chamber");
    public static SlotsIdentifier SECOND_CHAMBER = new SlotsIdentifier(ChatFormatting.GREEN,"second_chamber");
    public static SlotsIdentifier THIRD_CHAMBER = new SlotsIdentifier(ChatFormatting.GREEN,"third_chamber");

    public Bearer<Integer> injectionRate = Bearer.of(2);

    protected int ticks;
    public NuclearReactorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Registration.NUCLEAR_REACTOR_MENU.get(), Registration.NUCLEAR_REACTOR_BLOCK_ENTITY.get(), blockPos, blockState);
        additionalSyncInt.add(injectionRate);
        inventory.setValid(p-> Container.stillValidBlockEntity(this,p,actualMultiBlockPositions.isEmpty() ? 8 : (Math.max(distances.getX(),Math.max(distances.getY(),distances.getZ())) + 8)));
        burnTime = new Progress(Progress.Identifier.BURN_TIME,8,17,1000);
    }
    public static CenteredPredicate nuclearReactorPredicate = (pos, state, distance, maxDistance) -> {
        int x = distance.getX(),y = distance.getY(), z = distance.getZ();
        if (x >= maxDistance.getX() || y >= maxDistance.getY() || z >= maxDistance.getZ()) {
            return maxDistance.equals(Vec3i.ZERO) || state.is(Blocks.NUCLEAR_CASINGS);
        }else {
            boolean isConnector = (y + z == 0 && x > 0) || (x + z == 0 && y > 0) || (x + y == 0 && z > 0);
            if (isConnector && !state.is(Blocks.NUCLEAR_CASINGS)) return false;
            return (x + y + z == 0 || state.isAir() || state.is(Blocks.NUCLEAR_CASINGS));
        }
    };
    public boolean hasSecondChamber(){
        return hasChamber(0) || hasChamber(1) || hasChamber(2);
    }
    public boolean hasThirdChamber(){
        return hasChamber(3) || hasChamber(4) || hasChamber(5);
    }
    public boolean hasChamber(int index){
        return hasCasing(index,b->b.is(Registration.NUCLEAR_REACTOR_CHAMBER.get()));
    }
    protected boolean hasCasing(int index, Predicate<BlockState> predicate){
        BlockState state = level.getBlockState(getBlockPos().relative(Direction.values()[index]));
        return state.is(Blocks.NUCLEAR_CASINGS) && predicate.test(state);
    }


    public NonNullList<FactoryItemSlot> getSlots(@Nullable Player player) {
        NonNullList<FactoryItemSlot> slots = NonNullList.create();
        slots.add(new FactocraftyCYItemSlot(this, 0,148,53, TransportState.EXTRACT, FactoryCapacityTiers.BASIC));
        slots.add(new FactocraftyFluidItemSlot(this, 1,16,53,SlotsIdentifier.WATER, TransportState.INSERT));
        for (int i = 0; i < 3; i++)
            for(int j = 0; j < 6; ++j) {
                for(int k = 0; k < 3; ++k) {
                    int chamber = i;
                    int d = ((chamber-1) * 3) + j / 2;
                    slots.add(new FactocraftySlotWrapper(new FactoryItemSlot(inventory,i == 0 ? PRINCIPAL_CHAMBER : i <= 1 ? SECOND_CHAMBER : THIRD_CHAMBER,TransportState.EXTRACT_INSERT,(2 + 18 * i) + (k + j * 3), 8 + k * 18, 8 + j * 18){
                        public boolean isActive() {
                            return (chamber == 0) || hasChamber(d);
                        }

                        @Override
                        public boolean mayPlace(ItemStack itemStack) {
                            return itemStack.is(Items.NUCLEAR_COMPONENTS);
                        }
                    }));
                }
            }
        return slots;
    }

    @Override
    protected long getTankCapacity() {
        return 16*FluidStack.bucketAmount();
    }

    @Override
    public int getInitialEnergyCapacity() {
        return super.getInitialEnergyCapacity();
    }

    @Override
    protected int getGenerationRate() {
        return (int)Math.pow(burnTime.first().get(),1.12F);
    }

    @Override
    protected boolean isBurning() {
        return burnTime.first().get() >= 100;
    }

    protected void consumeFuel(){
        for (int i : componentsSlots) {
            ItemStack item = inventory.getItem(i);
            int injection = getFuelInjection(item);
            if (item.is(Items.NUCLEAR_RODS) && injection > 0 &&  ticks % (120 / injection) == 0){
                burnTime.first().add(1);
                if (level.random.nextFloat() >= 0.6 && item.hurt(1,level.random,null))
                    if (item.getDamageValue() >= item.getMaxDamage()) inventory.setItem(i,new ItemStack(Registration.CONTROL_ROD.get()));
            }
        }
    }
    public int getFuelInjection(ItemStack stack){
        return stack.getMaxDamage() * injectionRate.get() / 1000;
    }
    protected boolean canConsumeFuel(){
        return !fluidTank.getFluidStack().isEmpty() && hasFuelRods();
    }

    private boolean hasFuelRods(){return inventory.hasAnyMatching(i-> i.is(Items.NUCLEAR_RODS));}

    protected List<BlockPos> actualMultiBlockPositions = Collections.emptyList();

    protected Vec3i distances = Vec3i.ZERO;
    
    private final int[] componentsSlots = IntStream.range(2,56).toArray();


    public void serverTick(){
        ticks++;
        xLoop: for (int dX = 24; dX >= 1; dX--)
            for (int dY = 24; dY >= 1; dY--)
                for (int dZ = 24; dZ >= 1; dZ--) {
                    Vec3i distance = new Vec3i(dX,dZ,dY);
                    List<BlockPos> positions = IMultiBlockShape.getCenteredMultiBlockShape(nuclearReactorPredicate,distance,level,getBlockPos());
                    if (!positions.isEmpty()){
                        if (positions.size() != actualMultiBlockPositions.size() || !actualMultiBlockPositions.stream().allMatch(p->p.equals(positions.get(actualMultiBlockPositions.indexOf(p))))) {
                            actualMultiBlockPositions = positions;
                            distances = distance;
                            int airCount = 0;
                            int casingCount = 0;
                            for (BlockPos pos : positions) {
                                Vec3 center = pos.getCenter();
                                ((ServerLevel)level).sendParticles(ParticleTypes.WAX_OFF, center.x, center.y, center.z,1,0.0, 0.2, 0.1, 0.8);
                                setCasingNuclearCorePos(pos,getBlockPos());
                                if (level.getBlockState(pos).isAir()) airCount++;
                                else if (level.getBlockState(pos).is(Registration.NUCLEAR_REACTOR_CASING.get())) casingCount++;
                            }
                            long i = getTankCapacity() + 8 * airCount * FluidStack.bucketAmount();
                            int maxEnergy = getInitialEnergyCapacity() + casingCount * 2500;
                            if (airCount > 0 && fluidTank.getMaxFluid() != i) fluidTank.setCapacity(i);
                            if (casingCount > 0 && energyStorage.getMaxEnergyStored() != maxEnergy) energyStorage.capacity = maxEnergy;
                        }
                        break xLoop;
                    }else if (isRemoved() || (!actualMultiBlockPositions.isEmpty() && distance.equals(new Vec3i(1,1,1)))){
                        actualMultiBlockPositions.forEach(pos->setCasingNuclearCorePos(pos,null));
                        distances = Vec3i.ZERO;
                        actualMultiBlockPositions.clear();
                    }
                }

        if (actualMultiBlockPositions.isEmpty() || isRemoved()){
            for (Direction d : Direction.values()) {
                if (hasCasing(d.ordinal(), b-> true))
                    setCasingNuclearCorePos(getBlockPos().relative(d),isRemoved() ? null : getBlockPos());
            }
        }
        boolean wasBurning = isBurning();
        int energy = 0;

        boolean hasFluid = fluidTank.getFluidStack().getAmount() >= getPlatformFluidConsume(5);
        if (burnTime.first().get() >= 25 && ticks % (int)Math.pow(burnTime.first().get(),0.5) == 0)
            burnTime.first().shrink(1);
        if(isBurning()) {
            if (energyStorage.getSpace() > 0 && hasFluid){
                progress.first().add(1);
                energy = energyStorage.receiveEnergy(new CraftyTransaction(getGenerationRate(), energyStorage.supportedTier), false).energy;
                if (progress.first().get() >= progress.first().maxProgress) {
                    fluidTank.drain(getPlatformFluidConsume(5),false);
                    progress.first().set(0);
                }
            }
        } else if (progress.first().get() > 0) progress.first().shrink(2); else progress.first().set(0);
        if (hasFluid && canConsumeFuel())
            consumeFuel();

        energyTick.set(energy);

        for (Direction direction : Direction.values()) {
            BlockPos pos = getBlockPos().relative(direction);
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null)
                FactoryAPIPlatform.getPlatformFactoryStorage(be).getStorage(Storages.CRAFTY_ENERGY,direction.getOpposite()).ifPresent((e)-> transferEnergyTo(this, direction, e));
        }
        if (wasBurning != getBlockState().getValue(FactocraftyMachineBlock.ACTIVE)) level.setBlock(getBlockPos(),getBlockState().setValue(FactocraftyMachineBlock.ACTIVE, burnTime.first().get() > 0), 3);

    }
    public void setCasingNuclearCorePos(BlockPos casingPos,@Nullable BlockPos nuclearCorePos){
        level.getBlockEntity(casingPos,Registration.REACTOR_CASING_BLOCK_ENTITY.get()).ifPresent(b-> b.setNuclearCorePos(nuclearCorePos)) ;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.factocrafty.nuclear_reactor");
    }
}
