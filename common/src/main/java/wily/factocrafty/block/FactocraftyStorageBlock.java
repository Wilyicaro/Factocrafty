package wily.factocrafty.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.architectury.fluid.FluidStack;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMenuBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.item.FactocraftyUpgradeItem;
import wily.factocrafty.item.WeldableItem;
import wily.factocrafty.item.WrenchItem;
import wily.factocrafty.recipes.ShapedTagRecipe;
import wily.factocrafty.util.CompoundTagUtil;
import wily.factocrafty.util.FactocraftyRecipeUtil;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.util.StorageStringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FactocraftyStorageBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");


    public FactoryCapacityTiers capacityTier;

    protected final LoadingCache<Item, Function<RecipeManager,ItemStack>> baseDropCache;
    protected final LoadingCache<Item, Function<RecipeManager,ItemStack>> repairItemCache;
    public FactocraftyStorageBlock(FactoryCapacityTiers tier, Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
        capacityTier = tier;
        baseDropCache = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<>() {
            @Override
            public Function<RecipeManager,ItemStack> load(Item item) {
                return (manager)-> {
                    Bearer<ItemStack> stack = Bearer.of(ItemStack.EMPTY);
                    FactocraftyRecipeUtil.getRecipesStream(manager, RecipeType.CRAFTING).filter(rcp -> rcp.getResultItem(RegistryAccess.EMPTY).is(item)).forEach(r -> {
                        for (Ingredient first : r.getIngredients()) {
                            ItemStack itemStack = FactocraftyRecipeUtil.getFactocraftyStack(first);
                            if (itemStack.getItem() instanceof BlockItem b && (b.getBlock() instanceof FactocraftyBlock || b.getBlock() instanceof FactocraftyStorageBlock)) {
                                stack.set(itemStack);
                                break;
                            }
                        }
                    });
                    return stack.get();
                };
            }
        });
        repairItemCache = CacheBuilder.newBuilder().maximumSize(25).build(new CacheLoader<>() {
            @Override
            public Function<RecipeManager,ItemStack> load(Item item) {
                return (manager)-> {
                    Bearer<ItemStack> stack = Bearer.of(ItemStack.EMPTY);
                    FactocraftyRecipeUtil.getRecipesStream(manager, RecipeType.CRAFTING).filter(rcp -> rcp instanceof ShapedTagRecipe r && r.getResultItem(RegistryAccess.EMPTY).is(item)).map(r -> (ShapedTagRecipe) r).forEach(r -> {
                        for (Pair<Ingredient, CompoundTag> pair : r.recipeItems) {
                            ItemStack itemStack = FactocraftyRecipeUtil.getFactocraftyStack(pair.first());
                            if (itemStack.getItem() instanceof WeldableItem) {
                                itemStack.setTag(pair.second());
                                stack.set(itemStack);
                                break;
                            }
                        }
                    });
                    return stack.get();
                };
            }
        });
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

    public ItemStack getBaseDropItem(RecipeManager manager){
        ItemStack baseDrop = baseDropCache.getUnchecked(asItem()).apply(manager);
        return baseDrop.isEmpty() && this instanceof IFactocraftyCYEnergyBlock b? b.getBurnRepairItem(manager) : baseDrop;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
            ItemStack stack = player.getItemInHand(interactionHand);
            boolean fluidItem = ItemContainerUtil.isFluidContainer(stack);
            boolean isWrench = stack.getItem() instanceof WrenchItem;
            if (!level.isClientSide) {
                if (!stack.isEmpty() && level.getBlockEntity(blockPos) instanceof IFactoryExpandedStorage be) {
                    if ((isWrench || fluidItem && !be.getTanks().isEmpty())) {
                        if (fluidItem) {
                            if (this.interactFluidItem(be, player, interactionHand).shouldSwing())
                                return InteractionResult.SUCCESS;
                        } else return InteractionResult.FAIL;
                    }
                    if (be instanceof FactocraftyMenuBlockEntity fBe && stack.getItem() instanceof FactocraftyUpgradeItem && fBe.hasUpgradeStorage() && fBe.storedUpgrades.add(player.isCreative()? stack.copy() : stack)){
                        return InteractionResult.SUCCESS;
                    }
                    if (this instanceof IFactocraftyCYEnergyBlock b && ItemStack.isSameItem(stack,b.getBurnRepairItem(level.getRecipeManager())) && CompoundTagUtil.compoundContains(b.getBurnRepairItem(level.getRecipeManager()).getOrCreateTag(),stack.getOrCreateTag()) && be instanceof FactocraftyStorageBlockEntity sBe && sBe.hasEnergyCell() && sBe.energyStorage.storedTier.isBurned()){
                        sBe.energyStorage.storedTier = FactoryCapacityTiers.BASIC;
                        if (!player.isCreative()) stack.shrink(1);
                        level.playSound(null,blockPos, SoundEvents.VILLAGER_WORK_MASON, SoundSource.BLOCKS,1.0F,1.0F);
                        return InteractionResult.SUCCESS;
                    }
                }
                 this.interactWith(level, blockPos, player);
            } else return InteractionResult.SUCCESS;
            return InteractionResult.CONSUME;
    }
    public InteractionResult interactFluidItem(IFactoryExpandedStorage storage, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        for(IPlatformFluidHandler tank : storage.getTanks()) {
            FluidStack fluidStack = ItemContainerUtil.getFluid(player, hand);
            if (tank.getTransport().canExtract() && !tank.getFluidStack().isEmpty() && (fluidStack.isEmpty() || fluidStack.isFluidEqual(tank.getFluidStack())) && (!(stack.getItem() instanceof BucketItem) || (tank.getFluidStack().getAmount() >= FluidStack.bucketAmount()))) {
                if (!tank.drain((int) ItemContainerUtil.fillItem(tank.getFluidStack(),player,hand), false).isEmpty())
                    return InteractionResult.SUCCESS;
            }
            if (fluidStack.isEmpty() || !tank.isFluidValid(0,fluidStack) || !(fluidStack.isFluidEqual(tank.getFluidStack()) || tank.getFluidStack().isEmpty()) || !tank.getTransport().canInsert()) continue;
            if (tank.getTotalSpace() > 0 && !(stack.getItem() instanceof BucketItem) || tank.getTotalSpace() >= FluidStack.bucketAmount()) {
                if (!ItemContainerUtil.drainItem(tank.fill(ItemContainerUtil.getFluid(stack),false), player, hand).isEmpty())
                    return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    private void interactWith(Level world, BlockPos pos, Player player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ExtendedMenuProvider) {
            MenuRegistry.openExtendedMenu((ServerPlayer) player, (ExtendedMenuProvider) be);
        }
    }
    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (this instanceof IFactocraftyCYEnergyBlock b) {
            list.add(b.getEnergyTier().getEnergyTierComponent(false));
            list.add(StorageStringUtil.getMaxCraftyTransferTooltip((int) (b.getEnergyTier().initialCapacity * b.getEnergyTier().getConductivity())).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {

        List<ItemStack> list = new ArrayList<>();
        ItemStack itself = new ItemStack(asItem());
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);

            if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof FactocraftyMenuBlockEntity be)
                if (( tool.getItem() instanceof WrenchItem || (EnchantmentHelper.getEnchantments(tool).containsKey(Enchantments.SILK_TOUCH) && tool.isCorrectToolForDrops(blockState)) || be.getLevel().random.nextFloat() <= 0.5)) {
                be.saveToItem(itself);
                list.add(itself);
            }else list.add(getBaseDropItem(be.getLevel().getRecipeManager()));
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
            if (be instanceof FactocraftyStorageBlockEntity blockEntity)
                blockEntity.tick();

        };
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

}
