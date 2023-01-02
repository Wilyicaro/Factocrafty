package wily.factocrafty.block;

import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.WrenchItem;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.Storages;

import java.util.ArrayList;
import java.util.List;

public class FactocraftyStorageBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");


    public FactoryCapacityTiers capacityTier;
    public FactocraftyStorageBlock(FactoryCapacityTiers tier, Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
        capacityTier = tier;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean p_196243_5_) {
        if (state.getBlock() != oldState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof IFactoryStorage be) {
                be.getStorage(Storages.ITEM).ifPresent((i)->{
                    Containers.dropContents(world, pos,  i);
                    world.updateNeighbourForOutputSignal(pos, this);
                });
                // be.grantStoredRecipeExperience(world, Vec3.atCenterOf(pos));

            }

            super.onRemove(state, world, pos, oldState, p_196243_5_);
        }
    }



    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
            ItemStack stack = player.getItemInHand(interactionHand);
            boolean fluidItem = ItemContainerUtil.isFluidContainer(stack);
            boolean isWrench = stack.is(Registration.WRENCH.get());

            if( level.getBlockEntity(blockPos) instanceof IFactoryStorage be && (isWrench || fluidItem  && !be.getTanks().isEmpty())){
                if (fluidItem)
                    this.interactFluidItem(be,player,interactionHand);
                else if (isWrench)
                    return InteractionResult.FAIL;
            }
                else if (!level.isClientSide) this.interactWith(level, blockPos, player);
            if (level.isClientSide) return InteractionResult.SUCCESS;
            return InteractionResult.CONSUME;

    }
    public InteractionResult interactFluidItem(IFactoryStorage storage, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
            storage.getTanks().forEach((tank) -> {
                FluidStack fluidStack = ItemContainerUtil.getFluid(player, hand);
                if (tank.getTransport().canExtract() && fluidStack.isEmpty() && !tank.getFluidStack().isEmpty() && (!(stack.getItem() instanceof BucketItem) || ((stack.getItem() instanceof BucketItem) && tank.getFluidStack().getAmount() >= FluidStack.bucketAmount()))) {
                    tank.drain((int) ItemContainerUtil.fillItem(tank.getFluidStack(),player,hand), false);
                    return;
                }
                if (fluidStack.isEmpty() || !tank.isFluidValid(0,fluidStack) || (!fluidStack.isFluidEqual(tank.getFluidStack())) && !tank.getFluidStack().isEmpty() && !tank.getTransport().canInsert()) return;
                if (tank.getTotalSpace() > 0 && !(stack.getItem() instanceof BucketItem) || tank.getTotalSpace() >= FluidStack.bucketAmount()) {
                    ItemContainerUtil.drainItem(tank.fill(ItemContainerUtil.getFluid(stack),false), player, hand);
                }
            });

            return InteractionResult.SUCCESS;
    }

    private void interactWith(Level world, BlockPos pos, Player player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof MenuProvider) {
            MenuRegistry.openExtendedMenu((ServerPlayer) player, (ExtendedMenuProvider) be);
            //player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if(capacityTier !=null)
        list.add(capacityTier.getEnergyTierComponent());
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {

        List<ItemStack> list = new ArrayList<>();
        ItemStack itself = new ItemStack(asItem());
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);


            if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof FactocraftyProcessBlockEntity be)
                if (( tool.getItem() instanceof WrenchItem || (EnchantmentHelper.getEnchantments(tool).containsKey(Enchantments.SILK_TOUCH) && tool.isCorrectToolForDrops(blockState)) || be.getLevel().random.nextFloat() <= 0.5)) {
                CompoundTag tag = new CompoundTag();
                tag.put("BlockEntityTag", be.getUpdateTag());
                itself.setTag(tag);
                list.add(itself);
            }else list.add(new ItemStack(Registration.MACHINE_FRAME_BASE.get()));
            else list.add(itself);

        return list;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return(level1, pos, blockState1, be) -> {
            if (be instanceof FactocraftyStorageBlockEntity factocraftyMachineBlockEntity)
                factocraftyMachineBlockEntity.tick();

        };
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

}
