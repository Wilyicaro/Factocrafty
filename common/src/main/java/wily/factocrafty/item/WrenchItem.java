package wily.factocrafty.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factocrafty.block.transport.fluid.FluidPipeBlockEntity;
import wily.factocrafty.init.Registration;
import wily.factocrafty.network.FactocraftyStorageSidesPacket;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.Storages;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties.durability(225));
    }

    protected boolean canUseWrench(ItemStack stack){
        return true;
    }
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        ItemStack stack = useOnContext.getItemInHand();
        if (canUseWrench(stack)){
            if (level.getBlockEntity(pos) instanceof FactocraftyStorageBlockEntity be) {
                level.playSound(null, pos, Registration.WRENCH_TIGHT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                BlockState blockState = be.getBlockState();
                FluidState fluidState = level.getFluidState(pos);
                FactoryAPIPlatform.filteredHandlersCache.clear();
                if (player.isShiftKeyDown()) {
                    BlockEntity blockEntity = blockState.hasBlockEntity() ? be : null;
                    Block.dropResources(blockState, level, pos, blockEntity, player, useOnContext.getItemInHand());
                    if (be.hasInventory()) be.inventory.clearContent();
                    boolean bl2 = level.setBlock(pos, fluidState.createLegacyBlock(), 3);
                    if (bl2) {
                        level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, blockState));
                    }
                    whenUseWrench(level.random.nextInt(2, 6), useOnContext);
                } else {
                    be.getLevel().setBlock(be.getBlockPos(), be.getBlockState().rotate(Rotation.CLOCKWISE_90), 3);
                    whenUseWrench(1, useOnContext);
                }
                return InteractionResult.SUCCESS;
            }
            if (level.getBlockEntity(pos) instanceof FluidPipeBlockEntity be){
                whenUseWrench(level.random.nextInt(2, 4), useOnContext);
                if (level.isClientSide) {
                    level.playSound(player, pos, Registration.WRENCH_TIGHT.get(), SoundSource.PLAYERS, 0.8F, 1.0F);
                    Vec3 center = useOnContext.getClickedPos().getCenter();
                    Vec3 subtract = center.subtract(useOnContext.getClickLocation());
                    Direction direction = player.isShiftKeyDown() ? useOnContext.getClickedFace() : Direction.getNearest(subtract.x, subtract.y, subtract.z).getOpposite();
                    be.getStorageSides(Storages.FLUID).ifPresent(t ->
                        Factocrafty.NETWORK.sendToServer(new FactocraftyStorageSidesPacket(pos, 2, direction.ordinal(), t.get(direction).withTransport(t.getTransport(direction).next()).getTransport(), 0))
                    );
                    be.setChanged();
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

        }
        return super.useOn(useOnContext);
    }
    protected void whenUseWrench(int used, UseOnContext useOnContext){
        useOnContext.getItemInHand().hurtAndBreak(used, useOnContext.getPlayer(), player -> player.broadcastBreakEvent(useOnContext.getHand()));
    }
}
