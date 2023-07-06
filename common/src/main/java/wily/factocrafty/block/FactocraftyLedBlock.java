package wily.factocrafty.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyLedBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyItem;

public class FactocraftyLedBlock extends FactocraftyBlock implements EntityBlock,IFactocraftyCYEnergyBlock {

    public static IntegerProperty LIGHT_VALUE = IntegerProperty.create("light", 0, 15);
    public final boolean hasRGB;

    public final int maxLight;

    public FactocraftyLedBlock(Properties properties, int maxLight, boolean hasRGB) {
        super(properties.lightLevel((b) -> b.getValue(LIGHT_VALUE)));
        this.hasRGB = hasRGB;
        this.maxLight = maxLight;
        registerDefaultState(defaultBlockState().setValue(FactocraftyLedBlock.LIGHT_VALUE, 0));
    }
    public FactocraftyLedBlock(Properties properties, boolean hasRGB) {
        this(properties.lightLevel((b) -> b.getValue(LIGHT_VALUE)), 15, hasRGB);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack hand = player.getItemInHand(interactionHand);
        if (hand.is(Registration.WRENCH.get()) || (hand.is(Registration.RGB_CONTROLLER.get()) && hand.getItem() instanceof ICraftyEnergyItem<?> i && i.getCraftyEnergy(hand).getEnergyStored() > 0))
            return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(blockPos) instanceof FactocraftyLedBlockEntity be) {
            if (player.isCrouching()) be.savedLightValue = be.savedLightValue - 1 < 0 ? maxLight : be.savedLightValue - 1;
            else be.savedLightValue = be.savedLightValue + 1 > maxLight ? 0 : be.savedLightValue + 1;
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return ((level1, blockPos, blockState1, blockEntity) -> {
            if (blockEntity instanceof FactocraftyLedBlockEntity be) be.tick();
        });
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FactocraftyLedBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_VALUE);
    }

    @Override
    public FactoryCapacityTiers getEnergyTier() {
        return FactoryCapacityTiers.BASIC;
    }
}
