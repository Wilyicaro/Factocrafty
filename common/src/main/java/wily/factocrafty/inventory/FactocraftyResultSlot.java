package wily.factocrafty.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factocrafty.block.entity.FactocraftyMachineBlockEntity;
import wily.factoryapi.base.FactoryItemSlot;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

public class FactocraftyResultSlot extends FactoryItemSlot {
    private FactocraftyMachineBlockEntity be;
    private final Player player;
    private int removeCount;


    public FactocraftyResultSlot(FactocraftyMachineBlockEntity be, @Nullable Player player, int i, int j, int k) {
        super(be.inventory, SlotsIdentifier.OUTPUT, TransportState.EXTRACT,i, j, k);
        this.be = be;

        this.player = player;
    }

    public ItemStack remove(int i) {
        if (this.hasItem()) {
            this.removeCount += Math.min(i, this.getItem().getCount());
        }

        return super.remove(i);
    }

    public void onTake(Player player, ItemStack itemStack) {
        this.checkTakeAchievements(itemStack);
        super.onTake(player, itemStack);
    }

    protected void onQuickCraft(ItemStack itemStack, int i) {
        this.removeCount += i;
        this.checkTakeAchievements(itemStack);
    }

    protected void checkTakeAchievements(ItemStack itemStack) {
        if (player != null) {
            itemStack.onCraftedBy(this.player.level, this.player, this.removeCount);
            if (this.player instanceof ServerPlayer && this.be != null) {
                be.awardUsedRecipesAndPopExperience((ServerPlayer) this.player);
            }

            this.removeCount = 0;
        }
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }
}
