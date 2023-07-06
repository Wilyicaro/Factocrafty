package wily.factocrafty.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.Factocrafty;
import wily.factocrafty.block.entity.CYEnergyStorage;
import wily.factocrafty.block.entity.FactocraftyProcessBlockEntity;
import wily.factocrafty.block.entity.FactocraftyStorageBlockEntity;
import wily.factoryapi.base.ICraftyEnergyItem;
import wily.factoryapi.base.ICraftyEnergyStorage;

public class FactocraftyItemMenuContainer extends AbstractContainerMenu {
    public final Player player;
    public final ItemStack itemContainer;

    public BlockPos blockPos;
    public FactocraftyItemMenuContainer(@Nullable MenuType<?> menuType, int i, Player player) {
        super(menuType, i);
        this.player = player;
        this.itemContainer = player.getItemInHand(player.getUsedItemHand());
    }
    public FactocraftyItemMenuContainer(@Nullable MenuType<?> menuType, int i, Player player, BlockPos blockPos) {
        this(menuType,i,player);
        this.blockPos = blockPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
    @Override
    public void broadcastChanges() {
        updateChanges();
        super.broadcastChanges();
    }

    @Override
    public void sendAllDataToRemote() {
        updateChanges();
        super.sendAllDataToRemote();
    }

    protected void updateChanges() {
        if (player instanceof ServerPlayer sp && blockPos != null) {
            if (player.level().getBlockEntity(blockPos) instanceof FactocraftyStorageBlockEntity be) {
                be.syncAdditionalMenuData(this, sp);
            }
        }
    }
    @Override
    public boolean stillValid(Player player) {
        boolean b = true;
        if (itemContainer.getItem() instanceof ICraftyEnergyItem<?> item) {
            ICraftyEnergyStorage storage = item.getCraftyEnergy(itemContainer);
            if(player.level().random.nextFloat() >= 0.7 && player.tickCount % 10 == 0) storage.consumeEnergy(1,false);
            if (storage.getEnergyStored() <= 0) b = false;
        }
        return b;
    }
}
